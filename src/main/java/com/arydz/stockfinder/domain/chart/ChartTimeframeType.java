package com.arydz.stockfinder.domain.chart;

import com.arydz.stockfinder.domain.chart.db.CandleDailyEntity;
import com.arydz.stockfinder.domain.chart.db.CandleHourlyEntity;
import com.arydz.stockfinder.domain.chart.db.CandleWeeklyEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@Getter
@JsonSerialize(using = ChartTimeframeSerializer.class)
public enum ChartTimeframeType {

    WEEKLY("Weekly", CandleWeeklyEntity.ENTITY_NAME, CandleWeeklyEntity.class),
    DAILY("Daily", CandleDailyEntity.ENTITY_NAME, CandleDailyEntity.class),
    HOURLY("Hourly", CandleHourlyEntity.ENTITY_NAME, CandleHourlyEntity.class);

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
