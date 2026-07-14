package com.uis.ejeMvc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MetodoPago {
    EFECTIVO("efectivo"),
    TARJETA("tarjeta"),
    TRANSFERENCIA("transferencia"),
    MIXTO("mixto");

    private final String dbValue;

    MetodoPago(String dbValue) {
        this.dbValue = dbValue;
    }

    @JsonValue
    public String getDbValue() {
        return dbValue;
    }

    @JsonCreator
    public static MetodoPago from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Arrays.stream(values())
                .filter(v -> v.dbValue.equalsIgnoreCase(value.trim()) || v.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Método de pago inválido: " + value));
    }
}
