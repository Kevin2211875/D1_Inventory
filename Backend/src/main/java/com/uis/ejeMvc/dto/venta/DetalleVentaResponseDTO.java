package com.uis.ejeMvc.dto.venta;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class DetalleVentaResponseDTO {
    private final Integer idDetalleVenta;
    private final Integer idProducto;
    private final String codigoBarras;
    private final String producto;
    private final Integer idLote;
    private final String numeroLote;
    private final BigDecimal cantidad;
    private final BigDecimal precioUnitario;
    private final BigDecimal descuento;
    private final BigDecimal subtotal;
}
