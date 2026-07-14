package com.uis.ejeMvc.service.impl;

import com.uis.ejeMvc.dto.producto.ProductoResumenDTO;
import com.uis.ejeMvc.mapper.ProductoResumenMapper;
import com.uis.ejeMvc.model.Inventario;
import com.uis.ejeMvc.model.Producto;
import com.uis.ejeMvc.repository.InventarioRepository;
import com.uis.ejeMvc.repository.ProductoRepository;
import com.uis.ejeMvc.service.ProductoConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoConsultaServiceImpl implements ProductoConsultaService {

    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    @Override
    public ProductoResumenDTO buscarPorCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe enviar el código de barras.");
        }
        Producto producto = productoRepository.findByCodigoBarrasAndActivoTrue(codigoBarras.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado o inactivo."));
        return toResumen(producto);
    }

    @Override
    public Page<ProductoResumenDTO> buscarProductos(String nombre, Integer idCategoria, Pageable pageable) {
        String nombreNormalizado = normalizar(nombre);
        // El orden lo fijan las propias consultas (ORDER BY nombre); ignoramos cualquier sort entrante para
        // no romper la consulta nativa por categoría ni volver la paginación no determinista.
        Pageable paginacion = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Page<Producto> productos;
        if (idCategoria != null) {
            productos = productoRepository.buscarActivosPorCategoriaIncluyendoHijas(idCategoria, nombreNormalizado, paginacion);
        } else if (nombreNormalizado != null) {
            productos = productoRepository.findByActivoTrueAndNombreContainingIgnoreCaseOrderByNombreAsc(nombreNormalizado, paginacion);
        } else {
            productos = productoRepository.findByActivoTrueOrderByNombreAsc(paginacion);
        }

        return productos.map(this::toResumen);
    }

    private ProductoResumenDTO toResumen(Producto producto) {
        Inventario inventario = inventarioRepository.findConsultaByProductoId(producto.getIdProducto()).orElse(null);
        return ProductoResumenMapper.toResumen(producto, inventario);
    }

    private String normalizar(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
