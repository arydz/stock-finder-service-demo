package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.common.EnvProperties;
import com.arydz.stockfinder.domain.common.db.SqlUtils;
import com.arydz.stockfinder.domain.stock.db.StockEntity;
import com.arydz.stockfinder.domain.stock.db.StockFields;
import com.arydz.stockfinder.domain.stock.db.StockTableDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.util.List;

import static java.lang.String.format;

@Repository
@RequiredArgsConstructor
class StockDao {

    private static final List<StockFields> EXCLUDED_STOCK_FIELDS = List.of(StockFields.MARKET_INDEX_ID);

    private final EnvProperties properties;
    private final SqlUtils sqlUtils;
    private final StockTableDefinition stockTableDefinition;

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<StockEntity> entityList) {

        if (properties.getBatchSize() <= 0) {
            throw new IllegalArgumentException(format("Batch size is %s. Batch processing can't be disabled.", properties.getBatchSize()));
        }

        String upsertQuery = sqlUtils.prepareUpsert(StockEntity.ENTITY_NAME, stockTableDefinition, EXCLUDED_STOCK_FIELDS);

        jdbcTemplate.batchUpdate(upsertQuery,
                entityList,
                properties.getBatchSize(),
                (PreparedStatement ps, StockEntity entity) -> {
                    ps.setString(1, entity.getTicker());
                    ps.setString(2, entity.getTitle());
                    ps.setInt(3, entity.getEdgarCik());
                }
        );
    }
}
