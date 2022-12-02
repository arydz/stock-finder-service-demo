package com.arydz.stockfinder.domain.housekeeping;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.lingala.zip4j.model.FileHeader;

import java.util.function.Predicate;

@RequiredArgsConstructor
public enum ExtractionMode {

    EXCLUDE_JSON_FOLDERS(header -> {
        String fileName = header.getFileName();
        return header.isDirectory() && fileName.endsWith("csv/");
    }),
    EXCLUDE_CSV_FOLDERS(header -> {
        String fileName = header.getFileName();
        return header.isDirectory() && fileName.endsWith("json/");
    });

    @Getter
    private final Predicate<FileHeader> extractable;
}
