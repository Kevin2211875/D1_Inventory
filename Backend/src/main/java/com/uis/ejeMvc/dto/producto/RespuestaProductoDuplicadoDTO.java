package com.uis.ejeMvc.dto.producto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Respuesta {@code 409} cuando se intenta registrar un producto que ya existe. Incluye el producto existente
 * para que el frontend pueda ofrecer directamente "agregar stock" en lugar de crear un duplicado.
 */
@Getter
@Builder
@AllArgsConstructor
public class RespuestaProductoDuplicadoDTO {

    /** Código estable para que el frontend distinga este caso: {@code PRODUCTO_DUPLICADO}. */
    private final String codigo;

    private final String message;

    private final ProductoResumenDTO productoExistente;
}
