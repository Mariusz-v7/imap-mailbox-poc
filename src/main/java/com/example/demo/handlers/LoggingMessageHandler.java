package com.example.demo.handlers;

import com.example.demo.MessageContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

@Slf4j
public class LoggingMessageHandler implements MessageHandler {
    @Override
    public void handle(MessageContainer<?> message) {
        log.info("Received message: {}", message.getTransformed());
    }
}
