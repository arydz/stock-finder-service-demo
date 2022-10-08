package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.stock.db.StockEntity;
import com.arydz.stockfinder.domain.stock.model.EdgarStock;
import com.arydz.stockfinder.domain.stock.model.Stock;
import com.googlecode.jmapper.JMapper;
import com.googlecode.jmapper.api.JMapperAPI;
import org.springframework.stereotype.Component;

import static com.googlecode.jmapper.api.JMapperAPI.global;
import static com.googlecode.jmapper.api.JMapperAPI.mappedClass;

@Component
class StockMapper {

    private final JMapper<StockEntity, EdgarStock> edgarToEntityJMapper = new JMapper<>(StockEntity.class, EdgarStock.class);

    private final JMapperAPI jmapperApi = new JMapperAPI()
            .add(mappedClass(Stock.class)
                    .add(global().excludedAttributes("marketIndexName"))
            );
    private final JMapper<Stock, StockEntity> entityToStockJMapper = new JMapper<>(Stock.class, StockEntity.class, jmapperApi);


    public StockEntity mapEdgarStockToEntity(EdgarStock edgarStock) {
        return edgarToEntityJMapper.getDestination(edgarStock);
    }

    public Stock mapEntityToStock(StockEntity stockEntity) {
        return entityToStockJMapper.getDestination(stockEntity);
    }
}
