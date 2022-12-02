package com.arydz.stockfinder.domain.chart;

import com.arydz.stockfinder.domain.chart.db.ChartDailyEntity;
import com.arydz.stockfinder.domain.chart.db.ChartHourlyEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@Getter
@JsonSerialize(using = ChartTimeframeSerializer.class)
public enum ChartTimeframeType {

    DAILY("Daily", ChartDailyEntity.ENTITY_NAME, ChartDailyEntity.class),
    HOURLY("Hourly", ChartHourlyEntity.ENTITY_NAME, ChartHourlyEntity.class);

    private final String description;

    private final String table;

    private final Class<?> entityProjection;

    ChartTimeframeType(String description, String table, Class<?> entityProjection) {
        this.description = description;
        this.table = table;
        this.entityProjection = entityProjection;
    }

    public String getEntityProjectionName() {
        return this.entityProjection.getSimpleName();
    }
}
