package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.common.SqlUtils;
import com.arydz.stockfinder.domain.stock.db.StockEntity;
import com.arydz.stockfinder.domain.stock.db.StockFields;
import com.arydz.stockfinder.domain.stock.db.StockTableDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
class StockDao {

    private static final List<StockFields> EXCLUDED_STOCK_FIELDS = List.of(StockFields.MARKET_INDEX_ID);

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    private final SqlUtils sqlUtils;
    private final StockTableDefinition stockTableDefinition;

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<StockEntity> entityList) {

        String upsertQuery = sqlUtils.prepareUpsert("STOCK", stockTableDefinition, EXCLUDED_STOCK_FIELDS);

        jdbcTemplate.batchUpdate(upsertQuery,
                entityList,
                batchSize,
                (PreparedStatement ps, StockEntity entity) -> {
                    ps.setString(1, entity.getTicker());
                    ps.setString(2, entity.getTitle());
                    ps.setInt(3, entity.getEdgarCik());
                }
        );
    }
}
