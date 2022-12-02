package com.arydz.stockfinder.domain.chart.db;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ChartEntity {

    @Id
    private Long id;

    @Column(name = "FK_STOCK_ID")
    private Long fkStockId;

    @Column(name = "DATE_TIME")
    private LocalDateTime dateTime;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal close;

    private BigDecimal low;

    private BigDecimal volume;
}
