package com.example.demo;

import lombok.Data;
import org.springframework.messaging.Message;

@Data
public class MessageContainer <T>{
    private final Message<?> original;
    private final Message<T> transformed;
}
