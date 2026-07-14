package com.uis.ejeMvc.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MetodoPagoConverter implements AttributeConverter<MetodoPago, String> {
    @Override
    public String convertToDatabaseColumn(MetodoPago attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public MetodoPago convertToEntityAttribute(String dbData) {
        return MetodoPago.from(dbData);
    }
}
