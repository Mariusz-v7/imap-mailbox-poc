package com.example.demo;

import com.example.demo.filters.MessageFilter;
import com.sun.mail.imap.IMAPMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mail.SearchTermStrategy;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class MailClientConfiguration {
    private final ApplicationProperties properties;
    private final ApplicationContext context;
    private final MailReceiverFactory mailReceiverFactory;
    private final TaskScheduler scheduler;

    // TODO: tests: https://github.com/spring-projects/spring-integration/blob/main/spring-integration-mail/src/test/java/org/springframework/integration/mail/dsl/MailTests.java
    @PostConstruct
    void postConstruct() {
        AutowireCapableBeanFactory autowireCapableBeanFactory = context.getAutowireCapableBeanFactory();

        for (ApplicationProperties.MailBoxConfiguration configuration : properties.getMailboxes()) {
            SearchTermStrategy searchTermStrategy = null;
            ApplicationProperties.MailBoxConfiguration.Schedule schedule = configuration.getSchedule();

            MailReceiverContainer mailReceiver = mailReceiverFactory.create(configuration);
            scheduler.schedule(() -> receiveEmails(mailReceiver), new CronTrigger(schedule.getCron()));
        }
    }

    private void receiveEmails(
            MailReceiverContainer receiver
    ) {
        log.info("[{}] Receiving emails...", receiver.getName());

        Object[] messages;
        try {
            messages = receiver.getReceiver().receive();
        } catch (MessagingException e) {
            log.error("[{}] Failed to receive emails", receiver.getName(), e);
            return;
        }

        log.info("[{}] Received {} messages", receiver.getName(), messages.length);

        processRawMessages(messages, receiver);
        afterProcessing(messages, receiver);
    }

    private void processRawMessages(
            Object[] raw,
            MailReceiverContainer receiver
    ) {
        List<Message<?>> rawMessages = Arrays.stream(raw)
                .filter(message -> filterByClassInstance(message, receiver))
                .map(message -> (Message<?>) message)
                .collect(Collectors.toList());

        List<MessageContainer<?>> transformedMessages = rawMessages.stream()
                .map(message -> new MessageContainer<>(message, transform(message, receiver)))
                .collect(Collectors.toList());

        List<MessageContainer<?>> filteredMessages = transformedMessages.stream()
                .filter(message -> filterMessage(message, receiver))
                .collect(Collectors.toList());

        log.info("[{}] {} messages left after filtering", receiver.getName(), filteredMessages.size());

        filteredMessages.forEach(message -> receiver.getHandler().handle(message));
    }

    private boolean filterByClassInstance(Object message, MailReceiverContainer receiver) {
        if (!(message instanceof Message)) {
            log.error("[{}] Unsupported message class: {}", receiver.getName(), message.getClass());
            return false;
        }

        return true;
    }

    private Message<?> transform(Message<?> message, MailReceiverContainer receiver) {
        Message<?> transformed = message;
        for (Transformer transformer : receiver.getTransformers()) {
            transformed = transformer.transform(transformed);
        }

        return transformed;
    }

    private boolean filterMessage(MessageContainer<?> message, MailReceiverContainer receiver) {
        for (MessageFilter filter : receiver.getFilters()) {
            if (!filter.accept(message)) {
                return false;
            }
        }

        return true;
    }

    private void afterProcessing(Object[] rawMessages, MailReceiverContainer receiver) {
        for (Object rawMessage : rawMessages) {
            if (!(rawMessage instanceof Message)) {
                log.error("[{}] Unsupported message class: {}", receiver.getName(), rawMessage.getClass());
                continue;
            }

            Message<?> message = (Message<?>) rawMessage;

            Object payload = message.getPayload();
            if (!(payload instanceof IMAPMessage)) {
                log.error("[{}] Unsupported payload class: {}", receiver.getName(), payload.getClass());
                return;
            }

            IMAPMessage imapPayload = (IMAPMessage) payload;
            Date receivedDate;
            try {
                receivedDate = imapPayload.getReceivedDate();
            } catch (MessagingException e) {
                log.error("[{}] Failed to determine received date", receiver.getName(), e);
                return;
            }

            Date sentDate;
            try {
                sentDate = imapPayload.getSentDate();
            } catch (MessagingException e) {
                log.error("[{}] Failed to determine sent date", receiver.getName(), e);
                return;
            }

            Instant receivedInstant = receivedDate.toInstant();
            Instant previousReceivedMessage = receiver.getContext().getLastReceivedMessageDate();
            if (previousReceivedMessage == null || previousReceivedMessage.isBefore(receivedInstant)) {
                receiver.getContext().setLastReceivedMessageDate(receivedInstant);
            }

            Instant sentInstant = sentDate.toInstant();
            Instant previousSentMessage = receiver.getContext().getLastSentMessageDate();
            if (previousSentMessage == null || previousSentMessage.isBefore(sentInstant)) {
                receiver.getContext().setLastSentMessageDate(sentInstant);
            }
        }

        log.info("[{}] Last received message date: {}, last sent message date: {}", receiver.getName(), receiver.getContext().getLastReceivedMessageDate(), receiver.getContext().getLastSentMessageDate());
    }

}
