package com.arydz.stockfinder.domain.stock.db;

import com.arydz.stockfinder.domain.dictionary.model.MarketIndexEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static com.arydz.stockfinder.domain.stock.db.StockEntity.ENTITY_NAME;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ENTITY_NAME)
public class StockEntity {

    public static final String ENTITY_NAME = "STOCK";

    @Id
    @GeneratedValue(generator = "stock_id_seq")
    @GenericGenerator(name = "stock_id_seq",strategy = "increment")
    private Long id;

    private String ticker;

    private String title;

    @Column(name = "EDGAR_CIK")
    private Integer edgarCik;

    @ManyToOne
    @JoinColumn(name="MARKET_INDEX_ID")
    private MarketIndexEntity marketIndexEntity;
}


