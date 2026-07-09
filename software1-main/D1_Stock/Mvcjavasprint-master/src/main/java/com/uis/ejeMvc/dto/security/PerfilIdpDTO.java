package com.uis.ejeMvc.dto.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Datos del usuario tomados del JWT de Keycloak: identidad básica más sus roles ya traducidos
 * a nombres internos D1 (ver {@link com.uis.ejeMvc.enums.rolesSecurity.RolUsuario}).
 */
@Getter
@Builder
@AllArgsConstructor
public class PerfilIdpDTO {

    /** Identificador único del usuario en Keycloak (claim {@code sub}). */
    private final String sub;

    /** Nombre de usuario (claim {@code preferred_username}). */
    private final String username;

    /** Correo (claim {@code email}). */
    private final String email;

    /** Nombre completo (claim {@code name}). */
    private final String nombreCompleto;

    /** Roles internos D1 que el usuario tiene según el JWT. */
    private final List<String> rolesInternos;
}
