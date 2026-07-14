package com.uis.ejeMvc.dto.venta;

import com.uis.ejeMvc.enums.MetodoPago;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class VentaResponseDTO {
    private final Integer idVenta;
    private final LocalDateTime fechaVenta;
    private final MetodoPago metodoPago;
    private final BigDecimal total;
    private final Integer idCliente;
    private final Integer idUsuarioVendedor;
    private final UUID keycloakSubVendedor;
    private final List<DetalleVentaResponseDTO> detalles;
}
