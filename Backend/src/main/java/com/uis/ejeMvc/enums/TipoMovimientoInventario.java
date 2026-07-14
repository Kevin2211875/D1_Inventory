package com.uis.ejeMvc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TipoMovimientoInventario {
    COMPRA("compra"),
    VENTA("venta"),
    MERMA("merma"),
    AJUSTE_ENTRADA("ajuste_entrada"),
    AJUSTE_SALIDA("ajuste_salida");

    private final String dbValue;

    TipoMovimientoInventario(String dbValue) {
        this.dbValue = dbValue;
    }

    @JsonValue
    public String getDbValue() {
        return dbValue;
    }

    @JsonCreator
    public static TipoMovimientoInventario from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Arrays.stream(values())
                .filter(v -> v.dbValue.equalsIgnoreCase(value.trim()) || v.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de movimiento inválido: " + value));
    }
}
