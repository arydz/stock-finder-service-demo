package com.arydz.stockfinder.domain.chart;

import com.arydz.stockfinder.domain.common.EnvProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ChartRepository {

    private static final Long START_DATE_OFFSET = 90L;
    private static final Long END_DATE_OFFSET = 21L;
    private static final BigDecimal STOCK_CLOSE_MIN = BigDecimal.valueOf(0.5);

    private static final String FK_STOCK_ID_PARAM = "fkStockId";
    private static final String DATE_LIMIT_PARAM = "dateLimit";
    private static final int DEFAULT_DAYS_OFFSET = 31;
    private static final String QUERY_SELECT_CANDLE_LIST_PATTERN = "SELECT %s (%s) FROM %s c %s ORDER BY c.dateTime ASC";
    private static final String NATIVE_QUERY_SELECT_CANDLE_LIST_BY_AUDIT_PATTERN = "SELECT * FROM %s c WHERE c.fk_stock_id=:fkStockId";
    private static final String DEFAULT_COLUMNS = "c.id, c.dateTime, c.open, c.high, c.close, c.low, c.volume";

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private Long batchSize;

    private final EnvProperties envProperties;

    @PersistenceContext
    private final EntityManager em;


    @Transactional
    public void refreshQuoteDescriptiveView() {
        Query query = em.createNativeQuery("REFRESH MATERIALIZED VIEW CONCURRENTLY QUOTES_DESCRIPTIVE");
        query.executeUpdate();
    }

}
