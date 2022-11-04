package com.arydz.stockfinder.domain.stock.model;

import com.arydz.stockfinder.domain.dictionary.model.MarketIndexEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Stock {

    private Long id;

    private String ticker;

    private String title;

    @JMap("marketIndexEntity")
    private String marketIndexName;

    @JMapConversion(from = {"marketIndexEntity"}, to = {"marketIndexName"})
    public String conversionMarketIndex(MarketIndexEntity marketIndexEntity) {

        return marketIndexEntity.getName();
    }

}
