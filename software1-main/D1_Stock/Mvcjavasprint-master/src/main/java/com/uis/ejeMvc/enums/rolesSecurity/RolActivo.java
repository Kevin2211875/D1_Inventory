package com.uis.ejeMvc.enums.rolesSecurity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

    /**
     * Indica si con este rol activo se pueden registrar ventas en el punto de venta.
     * <p>Vender es una operación de tienda: la realizan el {@link #VENDEDOR}, el {@link #GERENTE} y quien es
     * ambos ({@link #GERENTE_VENDEDOR}). El {@link #ADMINISTRADOR} gestiona la plataforma, no opera la caja,
     * por lo que no puede registrar ventas mientras actúa con ese rol.</p>
     */
    public boolean permiteRegistrarVentas() {
        return this == VENDEDOR || this == GERENTE || this == GERENTE_VENDEDOR;
    }

    /**
     * Roles activos que puede elegir un usuario según sus roles internos D1 (los de {@link RolUsuario}).
     * <p>Si es gerente y vendedor a la vez se ofrece solo la combinación {@link #GERENTE_VENDEDOR} en lugar de
     * los dos por separado; el {@link #ADMINISTRADOR} siempre se ofrece de forma independiente cuando está
     * presente. Es la única fuente de verdad sobre qué rol activo es válido para un usuario.</p>
     */
    public static List<RolActivo> disponiblesPara(Collection<String> rolesInternos) {
        if (rolesInternos == null || rolesInternos.isEmpty()) {
            return List.of();
        }

        boolean gerente = rolesInternos.contains(RolUsuario.GERENTE.getNombreInterno());
        boolean vendedor = rolesInternos.contains(RolUsuario.VENDEDOR.getNombreInterno());
        boolean administrador = rolesInternos.contains(RolUsuario.ADMINISTRADOR.getNombreInterno());

        List<RolActivo> disponibles = new ArrayList<>();
        if (gerente && vendedor) {
            disponibles.add(GERENTE_VENDEDOR);
        } else if (gerente) {
            disponibles.add(GERENTE);
        } else if (vendedor) {
            disponibles.add(VENDEDOR);
        }
        if (administrador) {
            disponibles.add(ADMINISTRADOR);
        }
        return List.copyOf(disponibles);
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
