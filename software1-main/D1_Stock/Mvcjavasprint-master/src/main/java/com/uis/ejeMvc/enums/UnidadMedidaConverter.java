package com.uis.ejeMvc.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UnidadMedidaConverter implements AttributeConverter<UnidadMedida, String> {
    @Override
    public String convertToDatabaseColumn(UnidadMedida attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public UnidadMedida convertToEntityAttribute(String dbData) {
        return UnidadMedida.from(dbData);
    }
}
