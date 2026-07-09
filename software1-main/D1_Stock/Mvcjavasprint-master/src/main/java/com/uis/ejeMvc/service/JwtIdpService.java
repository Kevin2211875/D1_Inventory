package com.uis.ejeMvc.service;

import com.uis.ejeMvc.dto.security.PerfilIdpDTO;

/**
 * Acceso al perfil del usuario autenticado a partir del JWT de Keycloak presente en el contexto de seguridad.
 */
public interface JwtIdpService {

    /**
     * @return el perfil del usuario de la petición actual.
     * @throws org.springframework.web.server.ResponseStatusException 401 si no hay un JWT autenticado.
     */
    PerfilIdpDTO obtenerPerfilIdp();

    /**
     * Decodifica y valida un access token de Keycloak y extrae el perfil con roles.
     *
     * @param accessToken JWT emitido por Keycloak
     * @return perfil del usuario con roles internos D1
     */
    PerfilIdpDTO extraerPerfilDesdeToken(String accessToken);
}
