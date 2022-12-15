package com.arydz.stockfinder.domain.chart;

import com.arydz.stockfinder.domain.common.DescriptiveType;
import com.arydz.stockfinder.domain.common.DescriptiveTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonSerialize(using = DescriptiveTypeSerializer.class)
public enum ChartTimeframeType implements DescriptiveType {

    DAILY("Daily"),
    HOURLY("Hourly");

    private final String description;
}
