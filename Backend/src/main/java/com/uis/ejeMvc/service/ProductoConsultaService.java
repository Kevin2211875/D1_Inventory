package com.uis.ejeMvc.service;

import com.uis.ejeMvc.dto.producto.ProductoResumenDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoConsultaService {
    ProductoResumenDTO buscarPorCodigoBarras(String codigoBarras);
    Page<ProductoResumenDTO> buscarProductos(String nombre, Integer idCategoria, Pageable pageable);
}
