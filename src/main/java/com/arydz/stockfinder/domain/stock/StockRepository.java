package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.stock.db.StockEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface StockRepository extends JpaRepository<StockEntity, Long> {

    @Query(value = "SELECT * FROM STOCK s "
            + "WHERE 1=1 "
            + "AND (:ticker IS NULL OR UPPER(s.ticker) LIKE UPPER(CONCAT('%', :ticker,'%'))) "
            + "AND (:title IS NULL OR UPPER(s.title) LIKE UPPER(CONCAT('%', :title,'%'))) "
            + "/*:pageable*/", nativeQuery = true)
    Page<StockEntity> findAll(@Param("ticker") String ticker, @Param("title") String title, Pageable pageable);
}
