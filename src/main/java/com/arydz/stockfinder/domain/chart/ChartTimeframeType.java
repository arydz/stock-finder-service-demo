package com.arydz.stockfinder.domain.chart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChartTimeframeType {

    DAILY("Daily"),
    HOURLY("Hourly");

    private final String description;
}
