package com.uis.ejeMvc.service;

import com.uis.ejeMvc.dto.usuario.CrearUsuarioRequestDTO;
import com.uis.ejeMvc.dto.usuario.UsuarioKeycloakDTO;

import java.util.List;

/**
 * Gestión de usuarios del realm contra la Admin REST API de Keycloak. Autenticación por service account del
 * cliente del backend (client_credentials), por lo que no se guardan credenciales de administrador.
 */
public interface KeycloakAdminService {

    List<UsuarioKeycloakDTO> listarUsuarios();

    UsuarioKeycloakDTO crearUsuario(CrearUsuarioRequestDTO request);

    void cambiarEstado(String userId, boolean enabled);

    /** Reemplaza los roles D1 del usuario por el rol indicado (GERENTE o VENDEDOR). */
    void cambiarRol(String userId, String rol);

    void resetPassword(String userId, String password, boolean temporal);
}
