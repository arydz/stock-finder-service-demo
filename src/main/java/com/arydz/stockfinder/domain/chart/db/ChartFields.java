package com.arydz.stockfinder.domain.chart.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChartFields {

    FK_STOCK_ID(-1),
    DATE_TIME(0),
    LOW(1),
    OPEN(2),
    VOLUME(3),
    HIGH(4),
    CLOSE(5);

    private final int csvOrder;
}