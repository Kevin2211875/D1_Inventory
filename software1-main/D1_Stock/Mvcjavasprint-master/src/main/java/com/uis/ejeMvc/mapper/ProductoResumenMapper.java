package com.uis.ejeMvc.mapper;

import com.uis.ejeMvc.dto.producto.ProductoResumenDTO;
import com.uis.ejeMvc.model.Inventario;
import com.uis.ejeMvc.model.Producto;

import java.math.BigDecimal;

/**
 * Construye el {@link ProductoResumenDTO} a partir de un {@link Producto} y su {@link Inventario} (que puede
 * no existir todavía). Es la única fuente de verdad del resumen de producto, compartida por la consulta del
 * punto de venta y por el registro/entrada de stock.
 */
public final class ProductoResumenMapper {

    private ProductoResumenMapper() {}

    public static ProductoResumenDTO toResumen(Producto producto, Inventario inventario) {
        return ProductoResumenDTO.builder()
                .idProducto(producto.getIdProducto())
                .codigoBarras(producto.getCodigoBarras())
                .nombre(producto.getNombre())
                .marca(producto.getMarca())
                .unidadMedida(producto.getUnidadMedida())
                .precioVenta(producto.getPrecioVenta())
                .porcentajeIva(producto.getPorcentajeIva())
                .esPerecedero(producto.getEsPerecedero())
                .idCategoria(producto.getCategoria().getIdCategoria())
                .categoria(producto.getCategoria().getNombre())
                .stockActual(inventario == null ? BigDecimal.ZERO : inventario.getCantidadActual())
                .ubicacion(inventario == null ? null : inventario.getUbicacion())
                .build();
    }
}
