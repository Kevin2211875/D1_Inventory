package com.uis.ejeMvc.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoMovimientoInventarioConverter implements AttributeConverter<TipoMovimientoInventario, String> {
    @Override
    public String convertToDatabaseColumn(TipoMovimientoInventario attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public TipoMovimientoInventario convertToEntityAttribute(String dbData) {
        return TipoMovimientoInventario.from(dbData);
    }
}
