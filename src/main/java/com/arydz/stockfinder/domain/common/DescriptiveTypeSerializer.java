package com.arydz.stockfinder.domain.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class DescriptiveTypeSerializer<T extends DescriptiveType> extends JsonSerializer<T> {

    @Override
    public void serialize(T value, JsonGenerator generator,
                          SerializerProvider provider) throws IOException {

        generator.writeStartObject();
        generator.writeFieldName("name");
        generator.writeString(value.name());
        generator.writeFieldName("description");
        generator.writeString(value.getDescription());
        generator.writeEndObject();
    }
}
