package com.arydz.stockfinder.domain.stock.model;

import com.arydz.stockfinder.domain.common.api.PageableParams;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;

@Value
@AllArgsConstructor
public class FilterStockParams implements PageableParams {

    String ticker;

    String title;

    @NotNull
    Integer page;
    @NotNull
    Integer size;
    @NotNull
    String sortColumn;
    @NotNull
    Sort.Direction sortDirection;

}



