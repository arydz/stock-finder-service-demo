package com.arydz.stockfinder.domain.chart.db;

import com.arydz.stockfinder.domain.common.db.TableDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.StringJoiner;

@Component
public final class ChartTableDefinition implements TableDefinition<ChartFields> {

    @Override
    public String getColumnsWithUnnamedParametersSqlPart(List<ChartFields> excludedColumns) {
        String columns = columnsDefinition(excludedColumns);
        String unnamedParameters = unnamedParametersDefinition(excludedColumns);
        return String.format("(%s) VALUES (%s)", columns, unnamedParameters);
    }

    private String columnsDefinition(List<ChartFields> excludedColumns) {

        StringJoiner result = new StringJoiner(COLUMN_DELIMITER);
        for (ChartFields value : ChartFields.values()) {
            if (!excludedColumns.contains(value)) {
                result.add(value.name());
            }
        }
        return result.toString();
    }

    private String unnamedParametersDefinition(List<ChartFields> excludedColumns) {

        StringJoiner result = new StringJoiner(COLUMN_DELIMITER);
        for (ChartFields value : ChartFields.values()) {
            if (!excludedColumns.contains(value)) {
                result.add(UNNAMED_PARAMETER);
            }
        }
        return result.toString();
    }

    @Override
    public String onConflictColumns() {
        return new StringJoiner(COLUMN_DELIMITER)
                .add(ChartFields.FK_STOCK_ID.name())
                .add(ChartFields.DATE_TIME.name())
                .toString();
    }
}
