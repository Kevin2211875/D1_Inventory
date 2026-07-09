package com.uis.ejeMvc.controller;

import com.uis.ejeMvc.dto.loggin.LoginRequestDTO;
import com.uis.ejeMvc.dto.loggin.LoginResponseDTO;
import com.uis.ejeMvc.dto.security.PerfilIdpDTO;
import com.uis.ejeMvc.service.JwtIdpService;
import com.uis.ejeMvc.service.KeycloakAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakAuthService keycloakAuthService;
    private final JwtIdpService jwtIdpService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = keycloakAuthService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<PerfilIdpDTO> perfilActual() {
        return ResponseEntity.ok(jwtIdpService.obtenerPerfilIdp());
    }
}
