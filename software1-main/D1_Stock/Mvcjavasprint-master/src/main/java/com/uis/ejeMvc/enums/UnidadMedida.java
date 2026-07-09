package com.uis.ejeMvc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UnidadMedida {
    UNIDAD("unidad"),
    KG("kg"),
    G("g"),
    LITRO("litro"),
    ML("ml"),
    PAQUETE("paquete"),
    CAJA("caja"),
    BOLSA("bolsa"),
    METRO("metro");

    private final String dbValue;

    UnidadMedida(String dbValue) {
        this.dbValue = dbValue;
    }

    @JsonValue
    public String getDbValue() {
        return dbValue;
    }

    @JsonCreator
    public static UnidadMedida from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Arrays.stream(values())
                .filter(v -> v.dbValue.equalsIgnoreCase(value.trim()) || v.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unidad de medida inválida: " + value));
    }
}
