package com.uis.ejeMvc.service.impl;

import com.uis.ejeMvc.dto.security.PerfilIdpDTO;
import com.uis.ejeMvc.security.KeycloakJwtRolesConverter;
import com.uis.ejeMvc.service.JwtIdpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Lee el JWT autenticado del {@link SecurityContextHolder} y arma el {@link PerfilIdpDTO}.
 * Los roles internos se derivan de las {@code authorities} (con prefijo {@code ROLE_}) que ya calculó
 * {@link com.uis.ejeMvc.security.KeycloakJwtRolesConverter}, así hay una única fuente de verdad.
 */
@Service
@RequiredArgsConstructor
public class JwtIdpServiceImpl implements JwtIdpService {

    private static final String PREFIJO_ROL = "ROLE_";

    private final JwtDecoder jwtDecoder;
    private final KeycloakJwtRolesConverter keycloakJwtRolesConverter;

    @Override
    public PerfilIdpDTO obtenerPerfilIdp() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay un JWT autenticado en el contexto.");
        }

        return construirPerfil(jwtAuth.getToken(), jwtAuth.getAuthorities());
    }

    @Override
    public PerfilIdpDTO extraerPerfilDesdeToken(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El access token es obligatorio.");
        }

        Jwt jwt = jwtDecoder.decode(accessToken);
        return construirPerfil(jwt, keycloakJwtRolesConverter.convert(jwt));
    }

    private PerfilIdpDTO construirPerfil(Jwt jwt, Iterable<? extends GrantedAuthority> authorities) {
        List<String> rolesInternos = java.util.stream.StreamSupport.stream(authorities.spliterator(), false)
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith(PREFIJO_ROL))
                .map(a -> a.substring(PREFIJO_ROL.length()))
                .toList();

        return PerfilIdpDTO.builder()
                .sub(jwt.getSubject())
                .username(jwt.getClaimAsString("preferred_username"))
                .email(jwt.getClaimAsString("email"))
                .nombreCompleto(jwt.getClaimAsString("name"))
                .rolesInternos(rolesInternos)
                .build();
    }
}
