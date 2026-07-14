package com.uis.ejeMvc.controller;

import com.uis.ejeMvc.dto.producto.AgregarStockRequestDTO;
import com.uis.ejeMvc.dto.producto.ProductoResumenDTO;
import com.uis.ejeMvc.dto.producto.RegistrarProductoRequestDTO;
import com.uis.ejeMvc.security.roles.SecureRoles;
import com.uis.ejeMvc.service.ProductoAdminService;
import com.uis.ejeMvc.service.ProductoConsultaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consulta de productos para el punto de venta: búsqueda por texto/categoría y lectura por código de barras
 * (pensada para el escáner). Es de solo lectura y la puede usar cualquier miembro del personal de la tienda
 * ({@link SecureRoles#STAFF_TIENDA}).
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@PreAuthorize(SecureRoles.STAFF_TIENDA)
public class ProductoController {

    private final ProductoConsultaService productoConsultaService;
    private final ProductoAdminService productoAdminService;

    @GetMapping
    public ResponseEntity<Page<ProductoResumenDTO>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer idCategoria,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productoConsultaService.buscarProductos(nombre, idCategoria, pageable));
    }

    @GetMapping("/codigo-barras/{codigoBarras}")
    public ResponseEntity<ProductoResumenDTO> porCodigoBarras(@PathVariable String codigoBarras) {
        return ResponseEntity.ok(productoConsultaService.buscarPorCodigoBarras(codigoBarras));
    }

    /**
     * Registra un producto nuevo. Solo vendedor o gerente. Si el producto ya existe responde {@code 409} con
     * el producto existente (ver {@link com.uis.ejeMvc.exception.ProductoDuplicadoException}) para ofrecer
     * "agregar stock" en su lugar.
     */
    @PostMapping
    @PreAuthorize(SecureRoles.VENDEDOR_O_GERENTE)
    public ResponseEntity<ProductoResumenDTO> registrar(@Valid @RequestBody RegistrarProductoRequestDTO request) {
        ProductoResumenDTO creado = productoAdminService.registrarProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /** Agrega un lote de stock a un producto existente. Solo vendedor o gerente. */
    @PostMapping("/{idProducto}/stock")
    @PreAuthorize(SecureRoles.VENDEDOR_O_GERENTE)
    public ResponseEntity<ProductoResumenDTO> agregarStock(
            @PathVariable Integer idProducto,
            @Valid @RequestBody AgregarStockRequestDTO request) {
        return ResponseEntity.ok(productoAdminService.agregarStock(idProducto, request));
    }
}
