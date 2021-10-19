package com.example.demo.transformers;

import com.example.demo.ProcessingContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.mail.SearchTermStrategy;
import org.springframework.integration.mail.transformer.MailToStringTransformer;
import org.springframework.integration.transformer.Transformer;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public enum MessageTransformers {
    TO_STRING((context, settings) -> new MailToStringTransformer());

    private final BiFunction<ProcessingContext, Map<String, String>, Transformer> factory;
}
