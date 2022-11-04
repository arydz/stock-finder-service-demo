package com.arydz.stockfinder.domain.dictionary;

import com.arydz.stockfinder.domain.dictionary.model.MarketIndex;
import com.arydz.stockfinder.domain.dictionary.model.MarketIndexEntity;
import com.googlecode.jmapper.JMapper;
import com.googlecode.jmapper.api.JMapperAPI;
import org.springframework.stereotype.Component;

import static com.googlecode.jmapper.api.JMapperAPI.attribute;
import static com.googlecode.jmapper.api.JMapperAPI.mappedClass;

@Component
public class MarketIndexMapper {

    private final JMapperAPI jmapperApi = new JMapperAPI()
            .add(mappedClass(MarketIndex.class)
                    .add(attribute("id").value("id"))
                    .add(attribute("country").value("country"))
                    .add(attribute("name").value("name")));

    private final JMapper<MarketIndex, MarketIndexEntity> toMarketIndexMapper = new JMapper<>(MarketIndex.class, MarketIndexEntity.class, jmapperApi);

    public MarketIndex mapToMarketIndex(MarketIndexEntity entity) {
        return toMarketIndexMapper.getDestination(entity);
    }
}
