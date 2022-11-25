package com.arydz.stockfinder.domain.chart.db;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = CandleDailyEntity.ENTITY_NAME)
public class CandleDailyEntity extends CandleEntity {
    public static final String ENTITY_NAME = "CANDLE_DAILY";
}
