package com.arydz.stockfinder.domain.chart;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

class ChartTimeframeSerializer extends JsonSerializer<ChartTimeframeType> {

    @Override
    public void serialize(ChartTimeframeType value, JsonGenerator generator,
                          SerializerProvider provider) throws IOException {

        generator.writeStartObject();
        generator.writeFieldName("code");
        generator.writeString(value.name());
        generator.writeFieldName("name");
        generator.writeString(value.getDescription());
        generator.writeEndObject();
    }
}
