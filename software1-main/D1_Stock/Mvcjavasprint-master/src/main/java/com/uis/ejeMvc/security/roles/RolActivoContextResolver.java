package com.uis.ejeMvc.security.roles;

import com.uis.ejeMvc.dto.security.PerfilIdpDTO;
import com.uis.ejeMvc.enums.rolesSecurity.RolActivo;
import com.uis.ejeMvc.enums.rolesSecurity.RolUsuario;
import com.uis.ejeMvc.service.JwtIdpService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RolActivoContextResolver {

    public static final String HEADER_ROL_ACTIVO = "X-D1-ROL-ACTIVO";
    /** Cache de {@link #resolverParaRequestActual()} por petición. */
    private static final String ATTR_RESOLUCION_ROL_ACTIVO = "D1_RESOLUCION_ROL_ACTIVO";

    private final JwtIdpService jwtIdpService;

    private record CacheRolActivo(Optional<RolActivo> valor) {}

    /**
     * Rol con el que el usuario actúa en esta petición (gerente, vendedor, la combinación gerente+vendedor o
     * administrador, según lo que traiga el JWT).
     * <p>Con más de una opción posible, la cabecera {@link #HEADER_ROL_ACTIVO} es obligatoria.</p>
     */
    public Optional<RolActivo> resolverParaRequestActual() {
        HttpServletRequest request = currentRequest();
        Object cached = request.getAttribute(ATTR_RESOLUCION_ROL_ACTIVO);
        if (cached instanceof CacheRolActivo c) {
            return c.valor();
        }

        PerfilIdpDTO perfil = jwtIdpService.obtenerPerfilIdp();
        List<String> roles = perfil.getRolesInternos();
        List<RolActivo> disponibles = rolesActivosDisponibles(roles);

        if (disponibles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No hay roles activos disponibles para este usuario.");
        }

        String rawHeader = request.getHeader(HEADER_ROL_ACTIVO);

        RolActivo elegido;
        if (rawHeader == null || rawHeader.isBlank()) {
            if (disponibles.size() > 1) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Debe enviar la cabecera " + HEADER_ROL_ACTIVO + " cuando el usuario tiene múltiples roles activos."
                );
            }
            elegido = disponibles.get(0);
        } else {
            elegido = RolActivo.fromHeader(rawHeader);
            if (elegido == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Valor inválido en " + HEADER_ROL_ACTIVO + ". Valores permitidos: "
                                + RolActivo.textoValoresPermitidosEnCabecera() + "."
                );
            }
            if (!disponibles.contains(elegido)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El rol activo enviado no está disponible para el usuario autenticado."
                );
            }
        }

        Optional<RolActivo> res = Optional.of(elegido);
        request.setAttribute(ATTR_RESOLUCION_ROL_ACTIVO, new CacheRolActivo(res));
        return res;
    }

    /**
     * Roles del JWT que pueden elegirse como {@linkplain RolActivo rol activo}. Si el usuario es gerente y
     * vendedor a la vez se ofrece solo la combinación {@link RolActivo#GERENTE_VENDEDOR}.
     */
    private List<RolActivo> rolesActivosDisponibles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }

        boolean hasGerente = roles.contains(RolUsuario.GERENTE.getNombreInterno());
        boolean hasVendedor = roles.contains(RolUsuario.VENDEDOR.getNombreInterno());
        boolean hasAdministrador = roles.contains(RolUsuario.ADMINISTRADOR.getNombreInterno());

        List<RolActivo> out = new ArrayList<>();
        if (hasGerente && hasVendedor) {
            out.add(RolActivo.GERENTE_VENDEDOR);
        } else if (hasGerente) {
            out.add(RolActivo.GERENTE);
        } else if (hasVendedor) {
            out.add(RolActivo.VENDEDOR);
        }

        if (hasAdministrador) {
            out.add(RolActivo.ADMINISTRADOR);
        }

        return out.stream().filter(Objects::nonNull).toList();
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo resolver el contexto HTTP actual.");
        }
        return attrs.getRequest();
    }
}
