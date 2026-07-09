package com.uis.ejeMvc.dto.producto;

import com.uis.ejeMvc.enums.UnidadMedida;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductoResumenDTO {
    private final Integer idProducto;
    private final String codigoBarras;
    private final String nombre;
    private final String marca;
    private final UnidadMedida unidadMedida;
    private final BigDecimal precioVenta;
    private final BigDecimal porcentajeIva;
    private final Boolean esPerecedero;
    private final Integer idCategoria;
    private final String categoria;
    private final BigDecimal stockActual;
    private final String ubicacion;
}
