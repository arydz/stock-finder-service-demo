package com.arydz.stockfinder.domain.chart.db;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = CandleWeeklyEntity.ENTITY_NAME)
public class CandleWeeklyEntity extends CandleEntity {

    public static final String ENTITY_NAME = "CANDLE_WEEKLY";
}
