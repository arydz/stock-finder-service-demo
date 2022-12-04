package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.stock.db.StockEntity;
import com.arydz.stockfinder.domain.stock.model.SimpleStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
interface StockRepository extends JpaRepository<StockEntity, Long> {

    @Query(value = "SELECT * FROM STOCK s "
            + "WHERE 1=1 "
            + "AND (:ticker IS NULL OR UPPER(s.ticker) LIKE UPPER(CONCAT('%', :ticker,'%'))) "
            + "AND (:title IS NULL OR UPPER(s.title) LIKE UPPER(CONCAT('%', :title,'%'))) "
            + "/*:pageable*/", nativeQuery = true)
    Page<StockEntity> findAll(@Param("ticker") String ticker, @Param("title") String title, Pageable pageable);

    @Query(value = "SELECT new com.arydz.stockfinder.domain.stock.model.SimpleStock(id, ticker) FROM StockEntity")
    List<SimpleStock> findAllSimpleStocks();

    @Modifying
    @Transactional
    @Query(value = "UPDATE STOCK SET MARKET_INDEX_ID = :marketIndexId WHERE ID = :stockId", nativeQuery = true)
    void updateMarketIndex(@Param("stockId") Long stockId, @Param("marketIndexId") Long marketIndexId);
}
