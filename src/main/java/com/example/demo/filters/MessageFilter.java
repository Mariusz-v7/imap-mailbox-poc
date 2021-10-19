package com.example.demo.filters;

import com.example.demo.MessageContainer;
import org.springframework.messaging.Message;

public interface MessageFilter {
    boolean accept(MessageContainer<?> message);
}
