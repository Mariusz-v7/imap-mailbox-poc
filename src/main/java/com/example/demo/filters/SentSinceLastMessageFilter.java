package com.example.demo.filters;

import com.example.demo.MessageContainer;
import com.example.demo.ProcessingContext;
import com.sun.mail.imap.IMAPMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.mail.MessagingException;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class SentSinceLastMessageFilter implements MessageFilter {
    private final ProcessingContext context;

    @Override
    public boolean accept(MessageContainer<?> message) {
        Instant lastMessageInstant = context.getLastSentMessageDate();

        Object originalPayload = message.getOriginal().getPayload();
        if (!(originalPayload instanceof IMAPMessage)) {
            log.error("[{}] Unsupported payload class: {}", context.getReceiverName(), originalPayload.getClass());
            return false;
        }

        IMAPMessage payload = (IMAPMessage) originalPayload;
        try {
            Date sentDate = payload.getSentDate();
            Instant sentInstant = sentDate.toInstant();

            return sentInstant.isAfter(lastMessageInstant);
        } catch (MessagingException e) {
            log.error("[{}] Failed to get sent date for a message", context.getReceiverName());
            return false;
        }
    }
}
