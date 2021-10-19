package com.example.demo.handlers;

import com.example.demo.ProcessingContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Getter
public enum MessageHandlers {
    LOGGING_HANDLER((context, settings) -> new LoggingMessageHandler()),
    ;

    private final BiFunction<ProcessingContext, Map<String, String>, MessageHandler> factory;
}
