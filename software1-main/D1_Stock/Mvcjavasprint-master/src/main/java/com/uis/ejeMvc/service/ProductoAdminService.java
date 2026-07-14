package com.uis.ejeMvc.service;

import com.uis.ejeMvc.dto.producto.AgregarStockRequestDTO;
import com.uis.ejeMvc.dto.producto.ProductoResumenDTO;
import com.uis.ejeMvc.dto.producto.RegistrarProductoRequestDTO;

/**
 * Alta de productos y entrada de stock por lote. Operaciones de tienda restringidas a vendedor/gerente.
 */
public interface ProductoAdminService {

    /**
     * Registra un producto nuevo. Si ya existe (código de barras o nombre+marca) lanza
     * {@link com.uis.ejeMvc.exception.ProductoDuplicadoException}.
     */
    ProductoResumenDTO registrarProducto(RegistrarProductoRequestDTO request);

    /** Agrega un lote de stock a un producto existente y actualiza su inventario. */
    ProductoResumenDTO agregarStock(Integer idProducto, AgregarStockRequestDTO request);
}
