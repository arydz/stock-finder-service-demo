package com.arydz.stockfinder.domain.common.db;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SqlUtils {

    private static final String UPSERT_QUERY = "INSERT INTO %s %s ON CONFLICT (%s) DO NOTHING";

    public <T extends Enum<T>> String prepareUpsert(String tableName, TableDefinition<T> tableDefinition, List<T> excludedColumns) {
        String columnsWithUnnamedParameters = tableDefinition.getColumnsWithUnnamedParametersSqlPart(excludedColumns);
        String onConflictColumns = tableDefinition.onConflictColumns();
        return String.format(UPSERT_QUERY, tableName, columnsWithUnnamedParameters, onConflictColumns);
    }
}
