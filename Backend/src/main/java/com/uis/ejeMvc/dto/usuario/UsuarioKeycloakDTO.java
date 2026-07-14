package com.uis.ejeMvc.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** Vista de un usuario del realm para el apartado de gestión del gerente. */
@Getter
@Builder
@AllArgsConstructor
public class UsuarioKeycloakDTO {
    private final String id;
    private final String username;
    private final String email;
    private final String nombreCompleto;
    private final boolean enabled;
    /** Roles internos D1 del usuario (GERENTE / VENDEDOR / ADMINISTRADOR). */
    private final List<String> roles;
}
