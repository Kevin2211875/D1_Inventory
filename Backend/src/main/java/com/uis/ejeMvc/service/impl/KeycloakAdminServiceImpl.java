package com.uis.ejeMvc.service.impl;

import com.uis.ejeMvc.dto.security.KeycloakTokenResponse;
import com.uis.ejeMvc.dto.usuario.CrearUsuarioRequestDTO;
import com.uis.ejeMvc.dto.usuario.UsuarioKeycloakDTO;
import com.uis.ejeMvc.enums.rolesSecurity.RolUsuario;
import com.uis.ejeMvc.service.KeycloakAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakAdminServiceImpl implements KeycloakAdminService {

    private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA_MAPAS =
            new ParameterizedTypeReference<>() {};

    private final RestTemplate restTemplate;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private volatile String cachedToken;
    private volatile Instant tokenExpiry;

    // ---------------------------------------------------------------------------------------------------
    // Operaciones públicas
    // ---------------------------------------------------------------------------------------------------

    @Override
    public List<UsuarioKeycloakDTO> listarUsuarios() {
        List<Map<String, Object>> usuarios = getLista(adminBase() + "/users?first=0&max=1000");
        return usuarios.stream().map(this::toUsuarioDTO).toList();
    }

    @Override
    public UsuarioKeycloakDTO crearUsuario(CrearUsuarioRequestDTO request) {
        String rol = validarRolAsignable(request.getRol());

        String username = StringUtils.hasText(request.getUsername())
                ? request.getUsername().trim()
                : request.getEmail().trim();

        Map<String, Object> userRep = new LinkedHashMap<>();
        userRep.put("username", username);
        userRep.put("email", request.getEmail().trim());
        if (StringUtils.hasText(request.getNombre())) {
            userRep.put("firstName", request.getNombre().trim());
        }
        if (StringUtils.hasText(request.getApellido())) {
            userRep.put("lastName", request.getApellido().trim());
        }
        userRep.put("enabled", true);
        userRep.put("emailVerified", true);
        userRep.put("credentials", List.of(Map.of(
                "type", "password",
                "value", request.getPassword(),
                "temporary", true
        )));

        ResponseEntity<Void> respuesta = enviar(adminBase() + "/users", HttpMethod.POST, userRep, Void.class);
        String userId = extraerIdDeLocation(respuesta);
        if (userId == null) {
            userId = buscarIdPorUsername(username);
        }

        asignarRolRealm(userId, rol);
        return getUsuario(userId);
    }

    @Override
    public void cambiarEstado(String userId, boolean enabled) {
        enviar(adminBase() + "/users/" + userId, HttpMethod.PUT, Map.of("enabled", enabled), Void.class);
    }

    @Override
    public void cambiarRol(String userId, String rol) {
        String nuevo = validarRolAsignable(rol);

        List<Map<String, Object>> actuales = getLista(adminBase() + "/users/" + userId + "/role-mappings/realm");
        List<Map<String, Object>> aQuitar = actuales.stream()
                .filter(m -> esRolD1(String.valueOf(m.get("name"))))
                .toList();
        if (!aQuitar.isEmpty()) {
            enviar(adminBase() + "/users/" + userId + "/role-mappings/realm", HttpMethod.DELETE, aQuitar, Void.class);
        }
        asignarRolRealm(userId, nuevo);
    }

    @Override
    public void resetPassword(String userId, String password, boolean temporal) {
        Map<String, Object> credencial = Map.of(
                "type", "password",
                "value", password,
                "temporary", temporal
        );
        enviar(adminBase() + "/users/" + userId + "/reset-password", HttpMethod.PUT, credencial, Void.class);
    }

    // ---------------------------------------------------------------------------------------------------
    // Ayudas de dominio
    // ---------------------------------------------------------------------------------------------------

    private void asignarRolRealm(String userId, String rolName) {
        ResponseEntity<Map<String, Object>> roleResp = enviar(
                adminBase() + "/roles/" + rolName, HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> roleRep = roleResp.getBody();
        if (roleRep == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El rol " + rolName + " no existe en el realm.");
        }
        enviar(adminBase() + "/users/" + userId + "/role-mappings/realm", HttpMethod.POST, List.of(roleRep), Void.class);
    }

    private UsuarioKeycloakDTO getUsuario(String userId) {
        ResponseEntity<Map<String, Object>> resp = enviar(
                adminBase() + "/users/" + userId, HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> usuario = resp.getBody();
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado en Keycloak.");
        }
        return toUsuarioDTO(usuario);
    }

    private UsuarioKeycloakDTO toUsuarioDTO(Map<String, Object> usuario) {
        String id = String.valueOf(usuario.get("id"));
        return UsuarioKeycloakDTO.builder()
                .id(id)
                .username(asString(usuario.get("username")))
                .email(asString(usuario.get("email")))
                .nombreCompleto(nombreCompleto(usuario))
                .enabled(Boolean.TRUE.equals(usuario.get("enabled")))
                .roles(rolesD1DeUsuario(id))
                .build();
    }

    private List<String> rolesD1DeUsuario(String userId) {
        List<Map<String, Object>> mappings = getLista(adminBase() + "/users/" + userId + "/role-mappings/realm");
        return mappings.stream()
                .map(m -> RolUsuario.fromGlobalRole(String.valueOf(m.get("name"))))
                .filter(java.util.Optional::isPresent)
                .map(o -> o.get().getNombreInterno())
                .distinct()
                .toList();
    }

    private String buscarIdPorUsername(String username) {
        List<Map<String, Object>> encontrados = getLista(
                adminBase() + "/users?exact=true&username=" + username);
        if (encontrados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Keycloak no devolvió el id del usuario creado.");
        }
        return String.valueOf(encontrados.get(0).get("id"));
    }

    private String validarRolAsignable(String rol) {
        String normalizado = rol == null ? "" : rol.trim().toUpperCase(Locale.ROOT);
        if (normalizado.equals(RolUsuario.GERENTE.getGlobalRoleIdp())
                || normalizado.equals(RolUsuario.VENDEDOR.getGlobalRoleIdp())) {
            return normalizado;
        }
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Rol no permitido. Solo se puede asignar GERENTE o VENDEDOR.");
    }

    private boolean esRolD1(String name) {
        return RolUsuario.fromGlobalRole(name).isPresent();
    }

    private String nombreCompleto(Map<String, Object> usuario) {
        String nombre = asString(usuario.get("firstName"));
        String apellido = asString(usuario.get("lastName"));
        String completo = ((nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido)).trim();
        return completo.isEmpty() ? null : completo;
    }

    // ---------------------------------------------------------------------------------------------------
    // Transporte HTTP hacia Keycloak
    // ---------------------------------------------------------------------------------------------------

    private List<Map<String, Object>> getLista(String url) {
        ResponseEntity<List<Map<String, Object>>> resp = enviar(url, HttpMethod.GET, null, LISTA_MAPAS);
        return resp.getBody() == null ? List.of() : resp.getBody();
    }

    private <T> ResponseEntity<T> enviar(String url, HttpMethod method, Object body, Class<T> tipo) {
        try {
            return restTemplate.exchange(url, method, new HttpEntity<>(body, authHeaders()), tipo);
        } catch (HttpClientErrorException e) {
            throw traducir(e);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo comunicar con Keycloak.");
        }
    }

    private <T> ResponseEntity<T> enviar(String url, HttpMethod method, Object body, ParameterizedTypeReference<T> tipo) {
        try {
            return restTemplate.exchange(url, method, new HttpEntity<>(body, authHeaders()), tipo);
        } catch (HttpClientErrorException e) {
            throw traducir(e);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo comunicar con Keycloak.");
        }
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(obtenerTokenAdmin());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /** Token de administración por client_credentials (service account), cacheado hasta poco antes de expirar. */
    private synchronized String obtenerTokenAdmin() {
        if (cachedToken != null && tokenExpiry != null && Instant.now().isBefore(tokenExpiry)) {
            return cachedToken;
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<KeycloakTokenResponse> resp = restTemplate.exchange(
                    tokenUrl(), HttpMethod.POST, new HttpEntity<>(form, headers), KeycloakTokenResponse.class);
            KeycloakTokenResponse token = resp.getBody();
            if (token == null || !StringUtils.hasText(token.getAccessToken())) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Keycloak no devolvió token de administración.");
            }
            cachedToken = token.getAccessToken();
            long expiresIn = token.getExpiresIn() == null ? 60L : token.getExpiresIn();
            tokenExpiry = Instant.now().plusSeconds(Math.max(expiresIn - 30, 10));
            return cachedToken;
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo obtener el token de administración. Verifique el client-secret y que el service account "
                            + "del cliente tenga los roles de realm-management (manage-users, view-users).");
        }
    }

    private ResponseStatusException traducir(HttpClientErrorException e) {
        HttpStatusCode status = e.getStatusCode();
        if (status.isSameCodeAs(HttpStatus.NOT_FOUND)) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso no encontrado en Keycloak.");
        }
        if (status.isSameCodeAs(HttpStatus.CONFLICT)) {
            return new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe en Keycloak.");
        }
        if (status.isSameCodeAs(HttpStatus.BAD_REQUEST)) {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Keycloak rechazó la solicitud (datos inválidos).");
        }
        if (status.isSameCodeAs(HttpStatus.UNAUTHORIZED) || status.isSameCodeAs(HttpStatus.FORBIDDEN)) {
            return new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "El backend no tiene permisos de administración en Keycloak (revise los roles realm-management del service account).");
        }
        return new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error de Keycloak: " + status);
    }

    private String extraerIdDeLocation(ResponseEntity<?> respuesta) {
        URI location = respuesta.getHeaders().getLocation();
        if (location == null) {
            return null;
        }
        String path = location.getPath();
        int barra = path.lastIndexOf('/');
        return barra >= 0 ? path.substring(barra + 1) : null;
    }

    private String adminBase() {
        return authServerUrl + "/admin/realms/" + realm;
    }

    private String tokenUrl() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
