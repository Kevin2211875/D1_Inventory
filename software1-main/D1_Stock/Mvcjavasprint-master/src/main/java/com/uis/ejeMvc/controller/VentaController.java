package com.uis.ejeMvc.controller;

import com.uis.ejeMvc.dto.venta.RegistrarVentaRequestDTO;
import com.uis.ejeMvc.dto.venta.VentaResponseDTO;
import com.uis.ejeMvc.enums.rolesSecurity.RolActivo;
import com.uis.ejeMvc.security.roles.RequiereRolActivo;
import com.uis.ejeMvc.security.roles.RolActivoContextResolver;
import com.uis.ejeMvc.security.roles.SecureRoles;
import com.uis.ejeMvc.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Punto de venta: registra ventas de productos.
 *
 * <p>Solo lo puede usar el personal que opera la caja ({@link SecureRoles#VENDEDOR_O_GERENTE}). Además del
 * rol de acceso, se exige que el {@linkplain RolActivo rol activo} de la petición permita vender
 * ({@link RolActivo#permiteRegistrarVentas()}): así un usuario que también es administrador no puede
 * registrar ventas mientras actúa con ese rol.</p>
 */
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    private final RolActivoContextResolver rolActivoContextResolver;

    @PostMapping
    @PreAuthorize(SecureRoles.VENDEDOR_O_GERENTE)
    @RequiereRolActivo
    public ResponseEntity<VentaResponseDTO> registrarVenta(@Valid @RequestBody RegistrarVentaRequestDTO request) {
        exigirRolActivoQuePuedaVender();
        VentaResponseDTO venta = ventaService.registrarVenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(venta);
    }

    private void exigirRolActivoQuePuedaVender() {
        RolActivo rolActivo = rolActivoContextResolver.resolverParaRequestActual()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "No hay un rol activo válido para esta operación."));

        if (!rolActivo.permiteRegistrarVentas()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El rol activo '" + rolActivo.getHeaderValue() + "' no puede registrar ventas. "
                            + "Cambie su rol activo a VENDEDOR o GERENTE."
            );
        }
    }
}
