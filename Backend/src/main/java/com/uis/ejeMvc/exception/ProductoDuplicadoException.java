package com.uis.ejeMvc.exception;

import com.uis.ejeMvc.dto.producto.ProductoResumenDTO;
import lombok.Getter;

/**
 * Se lanza al intentar registrar un producto que ya existe (por código de barras o por nombre+marca).
 * Lleva el producto existente para que el cliente pueda ofrecer "agregar stock" en su lugar.
 */
@Getter
public class ProductoDuplicadoException extends RuntimeException {

    private final transient ProductoResumenDTO productoExistente;

    public ProductoDuplicadoException(String mensaje, ProductoResumenDTO productoExistente) {
        super(mensaje);
        this.productoExistente = productoExistente;
    }
}
