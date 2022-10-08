package com.arydz.stockfinder.domain.stock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EdgarStock {

    @JMap("edgarCik")
    @JsonProperty("cik_str")
    private int cik;

    @JMap
    private String ticker;

    @JMap
    private String title;
}
