package com.arydz.stockfinder.domain.chart.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Candle and Heiken Ashi charts
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChartSimple {

    private Long id;

    @CsvBindByName(column = "Date")
    @CsvDate(value = "dd-MM-yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;

    @CsvBindByName(column = "Open")
    private BigDecimal open;

    @CsvBindByName(column = "High")
    private BigDecimal high;

    @CsvBindByName(column = "Close")
    private BigDecimal close;

    @CsvBindByName(column = "Low")
    private BigDecimal low;

    @CsvBindByName(column = "Volume")
    private BigDecimal volume;
}
