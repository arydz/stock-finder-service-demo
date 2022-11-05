package com.arydz.stockfinder.domain.common.api;

import org.springframework.data.domain.Sort;

public interface PageableParams {

    Integer getPage();
    
    Integer getSize();
    
    String getSortColumn();
    
    Sort.Direction getSortDirection();
}
