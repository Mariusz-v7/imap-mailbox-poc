package com.example.demo;

import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
public class ProcessingContext {
    private final String receiverName;
    private volatile Instant lastReceivedMessageDate;
    private volatile Instant lastSentMessageDate;
}
