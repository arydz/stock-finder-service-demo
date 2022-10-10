package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.stock.db.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface StockRepository extends JpaRepository<StockEntity, Long> {

}
