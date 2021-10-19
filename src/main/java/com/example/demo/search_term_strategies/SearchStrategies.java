package com.example.demo.search_term_strategies;

import com.example.demo.ProcessingContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.mail.SearchTermStrategy;

import java.util.Map;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Getter
public enum SearchStrategies {
    EMAIL_FROM((context, settings) -> new FromSearchTermStrategy(settings.get("email-from"))),
    RECEIVED_SINCE_LAST_MESSAGE((context, settings) -> new ReceivedSinceLastMessageStrategy(context)),
    SENT_SINCE_LAST_MESSAGE((context, settings) -> new SentSinceLastMessageStrategy(context)),
    ;

    private final BiFunction<ProcessingContext, Map<String, String>, SearchTermStrategy> factory;
}
