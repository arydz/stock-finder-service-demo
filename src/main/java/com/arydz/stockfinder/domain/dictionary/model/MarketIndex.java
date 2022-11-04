package com.arydz.stockfinder.domain.dictionary.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarketIndex {

    private Long id;

    private String name;

    private String country;
}
