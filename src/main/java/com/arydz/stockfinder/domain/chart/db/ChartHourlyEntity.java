package com.arydz.stockfinder.domain.chart.db;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = ChartHourlyEntity.ENTITY_NAME)
public class ChartHourlyEntity extends ChartEntity {

    public static final String ENTITY_NAME = "CHART_HOURLY";
}
