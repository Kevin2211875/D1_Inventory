package com.uis.ejeMvc.enums.rolesSecurity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Roles canónicos de la tienda D1 y su correspondencia con los roles del IdP (Keycloak).
 * <ul>
 *   <li>Cada rol tiene un nombre interno (para usar en el código y en {@code @PreAuthorize}).</li>
 *   <li>Puede mapearse a un rol de cliente ({@code resource_access.<client>.roles}) y/o a un rol global
 *       del realm ({@code realm_access.roles}) del JWT que emite Keycloak.</li>
 * </ul>
 * <p>Los nombres del IdP de abajo son valores de ejemplo: ajústalos a como estén definidos los roles
 * en tu realm/cliente de Keycloak.</p>
 */
@Getter
@AllArgsConstructor
public enum RolUsuario {

    GERENTE("GERENTE", "GERENTE", "GERENTE"),
    VENDEDOR("VENDEDOR", "VENDEDOR", "VENDEDOR"),
    ADMINISTRADOR("ADMINISTRADOR", "ADMINISTRADOR", "ADMINISTRADOR");

    private final String nombreInterno;
    private final String clientRoleIdp; // rol específico del cliente en el IdP (resource_access)
    private final String globalRoleIdp; // rol global del realm en el IdP (realm_access)

    // MAPAS de traducción IdP -> rol interno
    private static final Map<String, RolUsuario> CLIENT_MAP =
            Arrays.stream(values())
                    .filter(r -> r.clientRoleIdp != null)
                    .collect(Collectors.toMap(r -> r.clientRoleIdp, r -> r));

    private static final Map<String, RolUsuario> GLOBAL_MAP =
            Arrays.stream(values())
                    .filter(r -> r.globalRoleIdp != null)
                    .collect(Collectors.toMap(r -> r.globalRoleIdp, r -> r));

    public static Optional<RolUsuario> fromClientRole(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(CLIENT_MAP.get(token));
    }

    public static Optional<RolUsuario> fromGlobalRole(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(GLOBAL_MAP.get(token));
    }

    /**
     * Traduce las listas de roles del IdP (globales del realm y de cliente) a nombres internos D1, sin duplicados.
     * Usado por el conversor del JWT autenticado y por el servicio de perfil.
     */
    public static List<String> mapearRolesInternos(List<String> rolesGlobales, List<String> rolesCliente) {
        Set<String> unicos = new LinkedHashSet<>();
        List<String> globales = rolesGlobales == null ? List.of() : rolesGlobales;
        for (String rol : globales) {
            fromGlobalRole(rol).ifPresent(r -> unicos.add(r.getNombreInterno()));
        }
        List<String> cliente = rolesCliente == null ? List.of() : rolesCliente;
        for (String rol : cliente) {
            fromClientRole(rol).ifPresent(r -> unicos.add(r.getNombreInterno()));
        }
        return List.copyOf(unicos);
    }
}
