package com.arydz.stockfinder.domain.chart.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
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

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal close;

    private BigDecimal low;

    private BigDecimal volume;
}
