package com.uis.ejeMvc.service;

import com.uis.ejeMvc.dto.loggin.LoginResponseDTO;

public interface KeycloakAuthService {

    /**
     * Autentica al usuario contra Keycloak con correo y contraseña.
     *
     * @param email    correo del usuario (se envía como {@code username} a Keycloak)
     * @param password contraseña del usuario
     * @return tokens emitidos por Keycloak y el perfil del usuario con sus roles
     */
    LoginResponseDTO login(String email, String password);
}
