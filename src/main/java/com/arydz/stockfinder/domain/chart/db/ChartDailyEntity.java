package com.arydz.stockfinder.domain.chart.db;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = ChartDailyEntity.ENTITY_NAME)
public class ChartDailyEntity extends ChartEntity {
    public static final String ENTITY_NAME = "CHART_DAILY";
}
