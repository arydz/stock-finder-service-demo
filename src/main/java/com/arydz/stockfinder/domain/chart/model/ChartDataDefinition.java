package com.arydz.stockfinder.domain.chart.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Define a chart data types via vendor
 */
@Getter
@RequiredArgsConstructor
public enum ChartDataDefinition {

    DEFAULT_VENDOR(List.of("nasdaq", "nyse", "sp500"));

    private final List<String> importableFolderNameList;

    public boolean isImportable(String folderName) {
        return importableFolderNameList.contains(folderName);
    }
}
