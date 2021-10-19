package com.example.demo.filters;

import com.example.demo.ProcessingContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Getter
public enum MessageFilters {
    RECEIVED_SINCE_LAST_MESSAGE((context, settings) -> new ReceivedSinceLastMessageFilter(context)),
    SENT_SINCE_LAST_MESSAGE((context, settings) -> new SentSinceLastMessageFilter(context)),
    ;

    private final BiFunction<ProcessingContext, Map<String, String>, MessageFilter> factory;
}
