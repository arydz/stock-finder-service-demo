package com.arydz.stockfinder.domain.file;

import com.arydz.stockfinder.domain.common.DescriptiveTypeSerializer;
import com.arydz.stockfinder.domain.common.DescriptiveType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.lingala.zip4j.model.FileHeader;

import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
@JsonSerialize(using = DescriptiveTypeSerializer.class)
public enum ExtractionMode implements DescriptiveType {

    EXCLUDE_JSON_FOLDERS( "Exclude JSON folders", header -> {
        String fileName = header.getFileName();
        return header.isDirectory() && fileName.endsWith("csv/");
    }),
    EXCLUDE_CSV_FOLDERS( "Exclude CSV folders", header -> {
        String fileName = header.getFileName();
        return header.isDirectory() && fileName.endsWith("json/");
    });

    private final String description;

    @JsonIgnore
    private final Predicate<FileHeader> extractable;
}
