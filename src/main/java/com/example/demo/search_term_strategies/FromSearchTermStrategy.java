package com.example.demo.search_term_strategies;

import org.springframework.integration.mail.SearchTermStrategy;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;

public class FromSearchTermStrategy implements SearchTermStrategy {
    private final FromTerm from;

    FromSearchTermStrategy(String from) {
        try {
            this.from = new FromTerm(new InternetAddress(from));
        } catch (AddressException e) {
            throw new IllegalArgumentException("Failed to instantiate internet address with value: " + from, e);
        }
    }

    @Override
    public SearchTerm generateSearchTerm(Flags supportedFlags, Folder folder) {
        return from;
    }
}
