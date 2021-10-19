package com.example.demo.handlers;

import com.example.demo.MessageContainer;
import org.springframework.messaging.Message;

public interface MessageHandler {
    void handle(MessageContainer<?> message);
}
