package com.example.demo;

import com.example.demo.filters.MessageFilter;
import com.example.demo.handlers.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.SearchTermStrategy;
import org.springframework.integration.transformer.Transformer;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MailReceiverFactory {
    public MailReceiverContainer create(ApplicationProperties.MailBoxConfiguration configuration) {
        ApplicationProperties.MailBoxConfiguration.Connection connection = configuration.getConnection();
        ApplicationProperties.MailBoxConfiguration.Receiver receiverSettings = configuration.getReceiverSettings();
        ApplicationProperties.MailBoxConfiguration.Schedule schedule = configuration.getSchedule();

        String usr = URLEncoder.encode(connection.getUser(), StandardCharsets.UTF_8);
        String pwd = URLEncoder.encode(connection.getPassword(), StandardCharsets.UTF_8);
        String connectionString = "imap://" + usr + ":" + pwd + "@" + connection.getHost() + ":" + connection.getPort() + "/" + connection.getFolder();
        String name = "imap://" + usr + ":******@" + connection.getHost() + ":" + connection.getPort() + "/" + connection.getFolder();

        log.info("Creating Mail Receiver => {}", name);

        Properties emailProperties = new Properties();
        if (connection.isSsl()) {
            emailProperties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            emailProperties.setProperty("mail.imap.socketFactory.fallback", "false");
        }

        if (connection.isDebug()) {
            emailProperties.setProperty("mail.debug", "true");
        }

        ImapMailReceiver receiver = new ImapMailReceiver(connectionString);
        receiver.setShouldMarkMessagesAsRead(receiverSettings.isMarkAsRead());
        receiver.setShouldDeleteMessages(receiverSettings.isDeleteMessages());
        receiver.setAutoCloseFolder(receiverSettings.isAutoCloseFolder());
        receiver.setJavaMailProperties(emailProperties);

        ProcessingContext context = new ProcessingContext(name);
        initContext(context);

        if (schedule.getSearchStrategy() != null) {
            BiFunction<ProcessingContext, Map<String, String>, SearchTermStrategy> searchStrategyFactory = schedule.getSearchStrategy().getFactory();
            Map<String, String> settings = schedule.getSearchStrategySettings();
            SearchTermStrategy searchTermStrategy = searchStrategyFactory.apply(context, settings);
            receiver.setSearchTermStrategy(searchTermStrategy);
        }

        MessageHandler handler = schedule.getHandler().getFactory().apply(context, schedule.getHandlerSettings());

        List<Transformer> transformers = schedule.getTransformers().stream()
                .map(tuple -> tuple.getTransformer().getFactory().apply(context, tuple.getSettings()))
                .collect(Collectors.toList());

        List<MessageFilter> filters = schedule.getFilters().stream()
                .map(tuple -> tuple.getFilter().getFactory().apply(context, tuple.getSettings()))
                .collect(Collectors.toList());


        return new MailReceiverContainer(name, receiver, context, transformers, filters, handler);
    }

    private void initContext(ProcessingContext context) {
        context.setLastReceivedMessageDate(Instant.now()); // TODO: get value from DB?
        context.setLastSentMessageDate(Instant.now()); // TODO: get value from DB?
    }

}
