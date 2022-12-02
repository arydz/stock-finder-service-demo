package com.arydz.stockfinder.domain.common.db;


import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;


/**
 * Classes implementing this interface will provide the required information for JdbcTemplate, like database table names, and ready-to-use columns with values when saving data.
 * On-conflict columns are optional when the upsert method is used.
 */
public interface TableDefinition<T> extends Serializable {

    String COLUMN_DELIMITER = ", ";
    String UNNAMED_PARAMETER = "?";

    String getColumnsWithUnnamedParametersSqlPart(List<T> excludedColumns);

    default String onConflictColumns() {
        return StringUtils.EMPTY;
    }
}
