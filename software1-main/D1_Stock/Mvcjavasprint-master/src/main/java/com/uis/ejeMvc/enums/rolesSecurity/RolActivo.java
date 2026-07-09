package com.uis.ejeMvc.enums.rolesSecurity;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Rol con el que un usuario <em>actúa</em> en una petición concreta.
 * <p>Un usuario puede tener varios {@link RolUsuario} en su JWT (p. ej. un gerente que también es vendedor);
 * el rol activo indica con cuál de ellos está operando. Cuando trae GERENTE y VENDEDOR a la vez se ofrece el
 * valor combinado {@link #GERENTE_VENDEDOR}.</p>
 * <p>El valor se transmite en la cabecera
 * {@link com.uis.ejeMvc.security.roles.RolActivoContextResolver#HEADER_ROL_ACTIVO HEADER_ROL_ACTIVO}
 * cuando el usuario tiene más de un rol activo posible.</p>
 */
public enum RolActivo {
    GERENTE("GERENTE"),
    VENDEDOR("VENDEDOR"),
    /** Usuario que es gerente y vendedor a la vez. */
    GERENTE_VENDEDOR("GERENTE + VENDEDOR"),
    ADMINISTRADOR("ADMINISTRADOR");

    private final String headerValue;

    RolActivo(String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    /** Texto para mensajes de error (valores de {@link #getHeaderValue()} en el orden del enum). */
    public static String textoValoresPermitidosEnCabecera() {
        return Arrays.stream(values())
                .map(RolActivo::getHeaderValue)
                .collect(Collectors.joining(", "));
    }

    public static RolActivo fromHeader(String raw) {
        if (raw == null) {
            return null;
        }
        String n = raw.trim().replaceAll("\\s+", " ");
        for (RolActivo v : values()) {
            if (v.headerValue.equalsIgnoreCase(n)) {
                return v;
            }
            if (v.name().equalsIgnoreCase(n.replace(' ', '_'))) {
                return v;
            }
        }
        String compact = n.replace(" ", "").replace("+", "").replace("-", "");
        if ("GERENTEVENDEDOR".equalsIgnoreCase(compact)) {
            return GERENTE_VENDEDOR;
        }
        return null;
    }
}
