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
public class ReceivedSinceLastMessageFilter implements MessageFilter {
    private final ProcessingContext context;

    @Override
    public boolean accept(MessageContainer<?> message) {
        Instant lastMessageInstant = context.getLastReceivedMessageDate();

        Object originalPayload = message.getOriginal().getPayload();
        if (!(originalPayload instanceof IMAPMessage)) {
            log.error("[{}] Unsupported payload class: {}", context.getReceiverName(), originalPayload.getClass());
            return false;
        }

        IMAPMessage payload = (IMAPMessage) originalPayload;
        try {
            Date receivedDate = payload.getReceivedDate();
            Instant receivedInstant = receivedDate.toInstant();

            return receivedInstant.isAfter(lastMessageInstant);
        } catch (MessagingException e) {
            log.error("[{}] Failed to get received date for a message", context.getReceiverName());
            return false;
        }
    }
}
