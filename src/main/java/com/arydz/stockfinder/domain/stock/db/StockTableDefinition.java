package com.arydz.stockfinder.domain.stock.db;

import com.arydz.stockfinder.domain.common.db.TableDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.StringJoiner;

@Component
public final class StockTableDefinition implements TableDefinition<StockFields> {

    @Override
    public String getColumnsWithUnnamedParametersSqlPart(List<StockFields> excludedColumns) {
        String columns = columnsDefinition(excludedColumns);
        String unnamedParameters = unnamedParametersDefinition(excludedColumns);
        return String.format("(%s) VALUES (%s)", columns, unnamedParameters);
    }

    private String columnsDefinition(List<StockFields> excludedColumns) {

        StringJoiner result = new StringJoiner(COLUMN_DELIMITER);
        for (StockFields value : StockFields.values()) {
            if (!excludedColumns.contains(value)) {
                result.add(value.name());
            }
        }
        return result.toString();
    }

    private String unnamedParametersDefinition(List<StockFields> excludedColumns) {

        StringJoiner result = new StringJoiner(COLUMN_DELIMITER);
        for (StockFields value : StockFields.values()) {
            if (!excludedColumns.contains(value)) {
                result.add(UNNAMED_PARAMETER);
            }
        }
        return result.toString();
    }

    @Override
    public String onConflictColumns() {
        return StockFields.TICKER.name();
    }
}
