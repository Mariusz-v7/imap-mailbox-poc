package com.example.demo;

import com.example.demo.filters.MessageFilter;
import com.example.demo.handlers.MessageHandler;
import lombok.Data;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.transformer.Transformer;

import java.util.List;

@Data
public class MailReceiverContainer {
    private final String name;
    private final MailReceiver receiver;
    private final ProcessingContext context;
    private final List<Transformer> transformers ;
    private final List<MessageFilter> filters ;
    private final MessageHandler handler;
}
