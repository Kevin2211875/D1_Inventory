package com.uis.ejeMvc.security;

import com.uis.ejeMvc.enums.rolesSecurity.RolUsuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convierte un JWT de Keycloak en las {@code authorities} de Spring Security.
 * <p>Lee los roles globales del realm ({@code realm_access.roles}) y los del cliente
 * ({@code resource_access.<client-id>.roles}), los traduce a nombres internos con
 * {@link RolUsuario#mapearRolesInternos(List, List)} y los expone como autoridades
 * {@code ROLE_<nombreInterno>} (el prefijo que espera {@code hasRole(...)}).</p>
 */
@Component
public class KeycloakJwtRolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String PREFIJO_ROL = "ROLE_";
    private static final String CLAIM_REALM_ACCESS = "realm_access";
    private static final String CLAIM_RESOURCE_ACCESS = "resource_access";
    private static final String CLAVE_ROLES = "roles";

    @Value("${keycloak.client-id:}")
    private String clientId;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> rolesGlobales = extraerRolesRealm(jwt);
        List<String> rolesCliente = extraerRolesCliente(jwt);

        return RolUsuario.mapearRolesInternos(rolesGlobales, rolesCliente).stream()
                .map(rol -> (GrantedAuthority) new SimpleGrantedAuthority(PREFIJO_ROL + rol))
                .collect(Collectors.toList());
    }

    /** Roles globales del realm: {@code realm_access.roles}. */
    private List<String> extraerRolesRealm(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap(CLAIM_REALM_ACCESS);
        return realmAccess == null ? List.of() : aListaDeStrings(realmAccess.get(CLAVE_ROLES));
    }

    /** Roles del cliente configurado: {@code resource_access.<client-id>.roles}. */
    private List<String> extraerRolesCliente(Jwt jwt) {
        if (clientId == null || clientId.isBlank()) {
            return List.of();
        }
        Map<String, Object> resourceAccess = jwt.getClaimAsMap(CLAIM_RESOURCE_ACCESS);
        if (resourceAccess == null || !(resourceAccess.get(clientId) instanceof Map<?, ?> cliente)) {
            return List.of();
        }
        return aListaDeStrings(cliente.get(CLAVE_ROLES));
    }

    private List<String> aListaDeStrings(Object roles) {
        if (!(roles instanceof Collection<?> coleccion)) {
            return List.of();
        }
        return coleccion.stream().map(String::valueOf).collect(Collectors.toList());
    }
}
