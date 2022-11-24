package com.arydz.stockfinder.domain.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class EnvProperties {

    // Stock finder
    String allowedOrigins;
    int maxInMemorySizeMb;
    Path sourcePath;

    // Edgar
    String edgarBaseUrl;
    String edgarTickersUrl;

    // Spring
    int batchSize;

    public EnvProperties(@Value("${sf.allowed.origins}") String allowedOrigins,
                         @Value("${sf.api.file.max-in-memory-size.mb}") int maxInMemorySizeMb,
                         @Value("${sf.source.path}") Path sourcePath,
                         @Value("${edgar.sec.base.url}") String edgarBaseUrl,
                         @Value("${edgar.sec.files.company.tickers.url}") String edgarTickersUrl,
                         @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}") int batchSize) {
        this.allowedOrigins = allowedOrigins;
        this.maxInMemorySizeMb = maxInMemorySizeMb;
        this.sourcePath = sourcePath;
        this.edgarBaseUrl = edgarBaseUrl;
        this.edgarTickersUrl = edgarTickersUrl;
        this.batchSize = batchSize;
    }
}
