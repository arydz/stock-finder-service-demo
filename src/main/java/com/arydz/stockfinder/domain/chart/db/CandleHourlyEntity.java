package com.arydz.stockfinder.domain.chart.db;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = CandleHourlyEntity.ENTITY_NAME)
public class CandleHourlyEntity extends CandleEntity {

    public static final String ENTITY_NAME = "CANDLE_HOURLY";
}
