package com.uis.ejeMvc.service.impl;

import com.uis.ejeMvc.dto.producto.AgregarStockRequestDTO;
import com.uis.ejeMvc.dto.producto.ProductoResumenDTO;
import com.uis.ejeMvc.dto.producto.RegistrarProductoRequestDTO;
import com.uis.ejeMvc.enums.TipoMovimientoInventario;
import com.uis.ejeMvc.exception.ProductoDuplicadoException;
import com.uis.ejeMvc.mapper.ProductoResumenMapper;
import com.uis.ejeMvc.model.Categoria;
import com.uis.ejeMvc.model.Inventario;
import com.uis.ejeMvc.model.LoteProducto;
import com.uis.ejeMvc.model.MovimientoInventario;
import com.uis.ejeMvc.model.Producto;
import com.uis.ejeMvc.repository.CategoriaRepository;
import com.uis.ejeMvc.repository.InventarioRepository;
import com.uis.ejeMvc.repository.LoteProductoRepository;
import com.uis.ejeMvc.repository.MovimientoInventarioRepository;
import com.uis.ejeMvc.repository.ProductoRepository;
import com.uis.ejeMvc.service.ProductoAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoAdminServiceImpl implements ProductoAdminService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final InventarioRepository inventarioRepository;
    private final LoteProductoRepository loteProductoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    @Override
    public ProductoResumenDTO registrarProducto(RegistrarProductoRequestDTO request) {
        String codigoBarras = normalizar(request.getCodigoBarras());
        String nombre = request.getNombre().trim();
        String marca = normalizar(request.getMarca());

        verificarNoDuplicado(codigoBarras, nombre, marca);

        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoría indicada no existe."));

        Producto producto = new Producto();
        producto.setCodigoBarras(codigoBarras);
        producto.setNombre(nombre);
        producto.setDescripcion(normalizar(request.getDescripcion()));
        producto.setCategoria(categoria);
        producto.setMarca(marca);
        producto.setUnidadMedida(request.getUnidadMedida());
        producto.setPrecioVenta(request.getPrecioVenta().setScale(2, RoundingMode.HALF_UP));
        producto.setPorcentajeIva(request.getPorcentajeIva() == null
                ? BigDecimal.ZERO
                : request.getPorcentajeIva().setScale(2, RoundingMode.HALF_UP));
        producto.setEsPerecedero(Boolean.TRUE.equals(request.getEsPerecedero()));
        producto.setActivo(true);
        producto.setFechaCreacion(LocalDateTime.now());
        Producto guardado = productoRepository.save(producto);

        Inventario inventario = new Inventario();
        inventario.setProducto(guardado);
        inventario.setCantidadActual(BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP));
        inventario.setCantidadMinima(request.getCantidadMinima() == null
                ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                : request.getCantidadMinima().setScale(3, RoundingMode.HALF_UP));
        inventario.setUbicacion(normalizar(request.getUbicacion()));
        inventario.setFechaActualizacion(LocalDateTime.now());
        inventarioRepository.save(inventario);

        if (request.getStockInicial() != null) {
            aplicarEntradaStock(guardado, inventario, request.getStockInicial());
        }

        return ProductoResumenMapper.toResumen(guardado, inventario);
    }

    @Override
    public ProductoResumenDTO agregarStock(Integer idProducto, AgregarStockRequestDTO request) {
        Producto producto = productoRepository.findByIdProductoAndActivoTrue(idProducto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado o inactivo."));

        Inventario inventario = inventarioRepository.findByProducto_IdProducto(idProducto)
                .orElseGet(() -> crearInventarioVacio(producto));

        aplicarEntradaStock(producto, inventario, request);

        return ProductoResumenMapper.toResumen(producto, inventario);
    }

    /**
     * Registra la entrada como un lote (coherente con la venta FEFO), genera el movimiento de compra y suma
     * la cantidad al inventario. Reutilizado por el alta con stock inicial y por la entrada de stock aislada.
     */
    private void aplicarEntradaStock(Producto producto, Inventario inventario, AgregarStockRequestDTO request) {
        if (Boolean.TRUE.equals(producto.getEsPerecedero()) && request.getFechaVencimiento() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El producto es perecedero: debe indicar la fecha de vencimiento del lote."
            );
        }
        if (request.getFechaVencimiento() != null && request.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de vencimiento no puede estar en el pasado.");
        }

        BigDecimal cantidad = request.getCantidad().setScale(3, RoundingMode.HALF_UP);
        BigDecimal precioCompra = request.getPrecioCompra().setScale(2, RoundingMode.HALF_UP);

        LoteProducto lote = new LoteProducto();
        lote.setProducto(producto);
        lote.setNumeroLote(normalizar(request.getNumeroLote()));
        lote.setFechaIngreso(LocalDate.now());
        lote.setFechaVencimiento(request.getFechaVencimiento());
        lote.setCantidadInicial(cantidad);
        lote.setCantidadDisponible(cantidad);
        lote.setPrecioCompra(precioCompra);
        LoteProducto loteGuardado = loteProductoRepository.save(lote);

        inventario.setCantidadActual(inventario.getCantidadActual().add(cantidad));
        inventario.setFechaActualizacion(LocalDateTime.now());
        inventarioRepository.save(inventario);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setLote(loteGuardado);
        movimiento.setTipoMovimiento(TipoMovimientoInventario.COMPRA);
        movimiento.setCantidad(cantidad);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setObservacion(loteGuardado.getNumeroLote() == null
                ? "Entrada de stock"
                : "Entrada de stock lote " + loteGuardado.getNumeroLote());
        movimientoInventarioRepository.save(movimiento);
    }

    private void verificarNoDuplicado(String codigoBarras, String nombre, String marca) {
        if (codigoBarras != null) {
            productoRepository.findByCodigoBarras(codigoBarras).ifPresent(p -> {
                throw new ProductoDuplicadoException(
                        "Ya existe un producto con el código de barras " + codigoBarras + ".", resumenDe(p));
            });
        }
        productoRepository.buscarPorNombreYMarca(nombre, marca).ifPresent(p -> {
            throw new ProductoDuplicadoException(
                    "Ya existe un producto con ese nombre" + (marca == null ? "." : " y marca."), resumenDe(p));
        });
    }

    private ProductoResumenDTO resumenDe(Producto producto) {
        Inventario inventario = inventarioRepository.findConsultaByProductoId(producto.getIdProducto()).orElse(null);
        return ProductoResumenMapper.toResumen(producto, inventario);
    }

    private Inventario crearInventarioVacio(Producto producto) {
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setCantidadActual(BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP));
        inventario.setCantidadMinima(BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP));
        inventario.setFechaActualizacion(LocalDateTime.now());
        return inventario;
    }

    private String normalizar(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
