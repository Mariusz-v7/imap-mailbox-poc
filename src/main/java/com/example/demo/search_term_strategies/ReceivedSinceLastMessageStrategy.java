package com.example.demo.search_term_strategies;

import com.example.demo.ProcessingContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mail.SearchTermStrategy;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class ReceivedSinceLastMessageStrategy implements SearchTermStrategy {
    private final ProcessingContext context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public SearchTerm generateSearchTerm(Flags supportedFlags, Folder folder) {
        Instant lastMessageInstant = context.getLastReceivedMessageDate();
        Date lastMessageDate = Date.from(lastMessageInstant);

        log.info("[{}] Applying received date >= {}", context.getReceiverName(), dateFormat.format(lastMessageDate));

        return new ReceivedDateTerm(ComparisonTerm.GE, lastMessageDate);
    }
}
