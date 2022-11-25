package com.arydz.stockfinder.domain.housekeeping;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.lingala.zip4j.model.FileHeader;

import java.util.function.Predicate;

@RequiredArgsConstructor
public enum ExtractionMode {

    NO_EXCLUSION(header -> false),
    EXCLUDE_JSON_FILES(header -> {
        String fileName = header.getFileName();
        return header.isDirectory() && fileName.equalsIgnoreCase("json");
    }),
    EXCLUDE_CSV_FILES(header -> {
        String fileName = header.getFileName();
        return header.isDirectory() && fileName.equalsIgnoreCase("csv");
    });

    @Getter
    private final Predicate<FileHeader> directoryFilter;
}
