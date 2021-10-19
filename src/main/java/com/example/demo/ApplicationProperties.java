package com.example.demo;

import com.example.demo.filters.MessageFilters;
import com.example.demo.handlers.MessageHandlers;
import com.example.demo.search_term_strategies.SearchStrategies;
import com.example.demo.transformers.MessageTransformers;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties("app")
@Data
public class ApplicationProperties {
    private List<MailBoxConfiguration> mailboxes;

    @Data
    public static class MailBoxConfiguration {
        private Connection connection;
        private Receiver receiverSettings;
        private Schedule schedule;

        @Data
        public static class Connection {
            private String user;
            private String password;
            private String host;
            private String folder;
            private int port;
            private boolean debug;
            private boolean ssl;
        }

        @Data
        public static class Receiver {
            private boolean markAsRead;
            private boolean deleteMessages;
            private boolean autoCloseFolder;
        }

        @Data
        public static class Schedule {
            private String cron;
            private SearchStrategies searchStrategy;
            private Map<String, String> searchStrategySettings;
            private MessageHandlers handler;
            private Map<String, String> handlerSettings;
            private List<TransformerTuple> transformers;
            private List<FilterTuple> filters;
        }

        @Data
        public static class TransformerTuple {
            private MessageTransformers transformer;
            private Map<String, String> settings;
        }

        @Data
        public static class FilterTuple {
            private MessageFilters filter;
            private Map<String, String> settings;
        }
    }
}
