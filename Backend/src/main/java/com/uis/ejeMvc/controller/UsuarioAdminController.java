package com.uis.ejeMvc.controller;

import com.uis.ejeMvc.dto.usuario.CambiarEstadoRequestDTO;
import com.uis.ejeMvc.dto.usuario.CambiarRolRequestDTO;
import com.uis.ejeMvc.dto.usuario.CrearUsuarioRequestDTO;
import com.uis.ejeMvc.dto.usuario.ResetPasswordRequestDTO;
import com.uis.ejeMvc.dto.usuario.UsuarioKeycloakDTO;
import com.uis.ejeMvc.security.roles.SecureRoles;
import com.uis.ejeMvc.service.KeycloakAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Apartado de gestión de usuarios del gerente: listar, crear (con rol), activar/desactivar, cambiar rol y
 * resetear contraseña. Restringido a GERENTE; opera contra la Admin API de Keycloak.
 */
@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize(SecureRoles.SOLO_GERENTE)
public class UsuarioAdminController {

    private final KeycloakAdminService keycloakAdminService;

    @GetMapping
    public ResponseEntity<List<UsuarioKeycloakDTO>> listar() {
        return ResponseEntity.ok(keycloakAdminService.listarUsuarios());
    }

    @PostMapping
    public ResponseEntity<UsuarioKeycloakDTO> crear(@Valid @RequestBody CrearUsuarioRequestDTO request) {
        UsuarioKeycloakDTO creado = keycloakAdminService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{userId}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable String userId,
                                              @Valid @RequestBody CambiarEstadoRequestDTO request) {
        keycloakAdminService.cambiarEstado(userId, request.getEnabled());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/rol")
    public ResponseEntity<Void> cambiarRol(@PathVariable String userId,
                                           @Valid @RequestBody CambiarRolRequestDTO request) {
        keycloakAdminService.cambiarRol(userId, request.getRol());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> resetPassword(@PathVariable String userId,
                                              @Valid @RequestBody ResetPasswordRequestDTO request) {
        boolean temporal = request.getTemporal() == null || request.getTemporal();
        keycloakAdminService.resetPassword(userId, request.getPassword(), temporal);
        return ResponseEntity.noContent().build();
    }
}
