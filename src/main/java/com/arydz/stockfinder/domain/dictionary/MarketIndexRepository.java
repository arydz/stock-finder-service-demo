package com.arydz.stockfinder.domain.dictionary;

import com.arydz.stockfinder.domain.dictionary.model.MarketIndexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface MarketIndexRepository extends JpaRepository<MarketIndexEntity, Long> {

}
