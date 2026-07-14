package com.uis.ejeMvc.service.impl;

import com.uis.ejeMvc.dto.security.PerfilIdpDTO;
import com.uis.ejeMvc.dto.venta.DetalleVentaResponseDTO;
import com.uis.ejeMvc.dto.venta.ItemVentaRequestDTO;
import com.uis.ejeMvc.dto.venta.RegistrarVentaRequestDTO;
import com.uis.ejeMvc.dto.venta.VentaResponseDTO;
import com.uis.ejeMvc.enums.TipoMovimientoInventario;
import com.uis.ejeMvc.model.Cliente;
import com.uis.ejeMvc.model.DetalleVenta;
import com.uis.ejeMvc.model.Inventario;
import com.uis.ejeMvc.model.LoteProducto;
import com.uis.ejeMvc.model.MovimientoInventario;
import com.uis.ejeMvc.model.Producto;
import com.uis.ejeMvc.model.UsuarioSistema;
import com.uis.ejeMvc.model.Venta;
import com.uis.ejeMvc.repository.ClienteRepository;
import com.uis.ejeMvc.repository.InventarioRepository;
import com.uis.ejeMvc.repository.LoteProductoRepository;
import com.uis.ejeMvc.repository.MovimientoInventarioRepository;
import com.uis.ejeMvc.repository.ProductoRepository;
import com.uis.ejeMvc.repository.UsuarioSistemaRepository;
import com.uis.ejeMvc.repository.VentaRepository;
import com.uis.ejeMvc.service.JwtIdpService;
import com.uis.ejeMvc.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private static final BigDecimal CERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final LoteProductoRepository loteProductoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final JwtIdpService jwtIdpService;

    /**
     * Registra una venta completa en una sola transacción.
     *
     * Garantías principales:
     * - Resuelve el vendedor desde el sub de Keycloak.
     * - Bloquea inventario y lotes con PESSIMISTIC_WRITE para evitar sobreventa concurrente.
     * - Descuenta inventario general y lotes en orden FEFO: primero vence, primero sale.
     * - Genera detalle_venta y movimiento_inventario para auditoría.
     */
    @Override
    @Transactional
    public VentaResponseDTO registrarVenta(RegistrarVentaRequestDTO request) {
        validarRequest(request);

        UsuarioSistema vendedor = resolverUsuarioVendedorActual();
        Cliente cliente = resolverCliente(request.getIdCliente());

        Venta venta = new Venta();
        venta.setFechaVenta(LocalDateTime.now());
        venta.setMetodoPago(request.getMetodoPago());
        venta.setObservacion(request.getObservacion());
        venta.setCliente(cliente);
        venta.setUsuarioVendedor(vendedor);
        venta.setTotal(CERO);

        BigDecimal totalVenta = CERO;
        List<MovimientoInventario> movimientos = new ArrayList<>();

        for (ItemVentaRequestDTO item : request.getProductos()) {
            Producto producto = resolverProducto(item);
            BigDecimal cantidadSolicitada = normalizarCantidad(item.getCantidad());
            BigDecimal descuentoLinea = normalizarMoneda(item.getDescuento());

            Inventario inventario = inventarioRepository.findByProducto_IdProducto(producto.getIdProducto())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "El producto no tiene inventario registrado: " + producto.getNombre()
                    ));

            validarStockDisponible(producto, inventario, cantidadSolicitada);

            BigDecimal brutoLinea = calcularBruto(producto.getPrecioVenta(), cantidadSolicitada);
            if (descuentoLinea.compareTo(brutoLinea) > 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El descuento no puede ser mayor al valor bruto del producto: " + producto.getNombre()
                );
            }

            List<LoteProducto> lotes = loteProductoRepository.findLotesDisponiblesParaVentaForUpdate(producto.getIdProducto());
            ResultadoAsignacion resultado = asignarDetalleDesdeLotesOVentaSinLote(
                    venta,
                    movimientos,
                    producto,
                    lotes,
                    cantidadSolicitada,
                    descuentoLinea
            );

            inventario.setCantidadActual(inventario.getCantidadActual().subtract(cantidadSolicitada));
            inventario.setFechaActualizacion(LocalDateTime.now());

            totalVenta = totalVenta.add(resultado.subtotalTotal());
        }

        venta.setTotal(normalizarMoneda(totalVenta));
        Venta ventaGuardada = ventaRepository.save(venta);

        movimientos.forEach(m -> m.setObservacion("Salida por venta #" + ventaGuardada.getIdVenta()));
        movimientoInventarioRepository.saveAll(movimientos);

        return toResponse(ventaGuardada);
    }

    private ResultadoAsignacion asignarDetalleDesdeLotesOVentaSinLote(
            Venta venta,
            List<MovimientoInventario> movimientos,
            Producto producto,
            List<LoteProducto> lotes,
            BigDecimal cantidadSolicitada,
            BigDecimal descuentoLinea
    ) {
        BigDecimal restante = cantidadSolicitada;
        BigDecimal descuentoPendiente = descuentoLinea;
        BigDecimal subtotalTotal = CERO;

        if (lotes == null || lotes.isEmpty()) {
            BigDecimal subtotal = calcularSubtotal(producto.getPrecioVenta(), cantidadSolicitada, descuentoLinea);
            venta.agregarDetalle(crearDetalle(producto, null, cantidadSolicitada, producto.getPrecioVenta(), descuentoLinea, subtotal));
            movimientos.add(crearMovimiento(producto, null, cantidadSolicitada));
            return new ResultadoAsignacion(subtotal);
        }

        for (int i = 0; i < lotes.size() && restante.compareTo(BigDecimal.ZERO) > 0; i++) {
            LoteProducto lote = lotes.get(i);
            BigDecimal disponibleLote = lote.getCantidadDisponible();
            if (disponibleLote.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal cantidadTomada = disponibleLote.min(restante);
            boolean esUltimaAsignacion = restante.subtract(cantidadTomada).compareTo(BigDecimal.ZERO) == 0;

            BigDecimal descuentoAsignado = esUltimaAsignacion
                    ? descuentoPendiente
                    : prorratearDescuento(descuentoLinea, cantidadTomada, cantidadSolicitada);
            descuentoPendiente = descuentoPendiente.subtract(descuentoAsignado);

            BigDecimal subtotal = calcularSubtotal(producto.getPrecioVenta(), cantidadTomada, descuentoAsignado);
            subtotalTotal = subtotalTotal.add(subtotal);

            lote.setCantidadDisponible(lote.getCantidadDisponible().subtract(cantidadTomada));

            venta.agregarDetalle(crearDetalle(producto, lote, cantidadTomada, producto.getPrecioVenta(), descuentoAsignado, subtotal));
            movimientos.add(crearMovimiento(producto, lote, cantidadTomada));

            restante = restante.subtract(cantidadTomada);
        }

        if (restante.compareTo(BigDecimal.ZERO) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No hay lotes suficientes para completar la venta del producto: " + producto.getNombre()
            );
        }

        return new ResultadoAsignacion(normalizarMoneda(subtotalTotal));
    }

    private UsuarioSistema resolverUsuarioVendedorActual() {
        PerfilIdpDTO perfil = jwtIdpService.obtenerPerfilIdp();
        UUID keycloakSub;
        try {
            keycloakSub = UUID.fromString(perfil.getSub());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El token no contiene un sub UUID válido.");
        }

        UsuarioSistema usuario = usuarioSistemaRepository.findByKeycloakSub(keycloakSub).orElseGet(() -> {
            UsuarioSistema nuevo = new UsuarioSistema();
            nuevo.setKeycloakSub(keycloakSub);
            nuevo.setFechaCreacion(LocalDateTime.now());
            nuevo.setActivo(true);
            return nuevo;
        });

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario está inactivo en el sistema local.");
        }

        usuario.setUltimaEntrada(LocalDateTime.now());
        return usuarioSistemaRepository.save(usuario);
    }

    private Cliente resolverCliente(Integer idCliente) {
        if (idCliente == null) {
            return null;
        }
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado."));
    }

    private Producto resolverProducto(ItemVentaRequestDTO item) {
        if (item.getIdProducto() != null) {
            return productoRepository.findByIdProductoAndActivoTrue(item.getIdProducto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado o inactivo."));
        }
        if (item.getCodigoBarras() != null && !item.getCodigoBarras().isBlank()) {
            return productoRepository.findByCodigoBarrasAndActivoTrue(item.getCodigoBarras().trim())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado o inactivo."));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cada item debe enviar idProducto o codigoBarras.");
    }

    private void validarRequest(RegistrarVentaRequestDTO request) {
        if (request == null || request.getProductos() == null || request.getProductos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La venta debe contener al menos un producto.");
        }
        for (ItemVentaRequestDTO item : request.getProductos()) {
            if (item.getCantidad() == null || item.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos los productos deben tener cantidad mayor a cero.");
            }
        }
    }

    private void validarStockDisponible(Producto producto, Inventario inventario, BigDecimal cantidadSolicitada) {
        if (inventario.getCantidadActual().compareTo(cantidadSolicitada) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stock insuficiente para " + producto.getNombre() + ". Disponible: " + inventario.getCantidadActual()
            );
        }
    }

    private DetalleVenta crearDetalle(
            Producto producto,
            LoteProducto lote,
            BigDecimal cantidad,
            BigDecimal precioUnitario,
            BigDecimal descuento,
            BigDecimal subtotal
    ) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setLote(lote);
        detalle.setCantidad(cantidad.setScale(3, RoundingMode.HALF_UP));
        detalle.setPrecioUnitario(normalizarMoneda(precioUnitario));
        detalle.setDescuento(normalizarMoneda(descuento));
        detalle.setSubtotal(normalizarMoneda(subtotal));
        return detalle;
    }

    private MovimientoInventario crearMovimiento(Producto producto, LoteProducto lote, BigDecimal cantidad) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setLote(lote);
        movimiento.setTipoMovimiento(TipoMovimientoInventario.VENTA);
        movimiento.setCantidad(cantidad.setScale(3, RoundingMode.HALF_UP));
        movimiento.setFechaMovimiento(LocalDateTime.now());
        return movimiento;
    }

    private BigDecimal calcularBruto(BigDecimal precioUnitario, BigDecimal cantidad) {
        return normalizarMoneda(precioUnitario.multiply(cantidad));
    }

    private BigDecimal calcularSubtotal(BigDecimal precioUnitario, BigDecimal cantidad, BigDecimal descuento) {
        BigDecimal subtotal = calcularBruto(precioUnitario, cantidad).subtract(normalizarMoneda(descuento));
        return subtotal.max(CERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal prorratearDescuento(BigDecimal descuentoTotal, BigDecimal cantidadAsignada, BigDecimal cantidadTotal) {
        if (descuentoTotal == null || descuentoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return CERO;
        }
        return descuentoTotal
                .multiply(cantidadAsignada)
                .divide(cantidadTotal, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizarCantidad(BigDecimal value) {
        return value.setScale(3, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizarMoneda(BigDecimal value) {
        if (value == null) {
            return CERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private VentaResponseDTO toResponse(Venta venta) {
        return VentaResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .fechaVenta(venta.getFechaVenta())
                .metodoPago(venta.getMetodoPago())
                .total(venta.getTotal())
                .idCliente(venta.getCliente() == null ? null : venta.getCliente().getIdCliente())
                .idUsuarioVendedor(venta.getUsuarioVendedor().getIdUsuario())
                .keycloakSubVendedor(venta.getUsuarioVendedor().getKeycloakSub())
                .detalles(venta.getDetalles().stream().map(this::toDetalleResponse).toList())
                .build();
    }

    private DetalleVentaResponseDTO toDetalleResponse(DetalleVenta detalle) {
        return DetalleVentaResponseDTO.builder()
                .idDetalleVenta(detalle.getIdDetalleVenta())
                .idProducto(detalle.getProducto().getIdProducto())
                .codigoBarras(detalle.getProducto().getCodigoBarras())
                .producto(detalle.getProducto().getNombre())
                .idLote(detalle.getLote() == null ? null : detalle.getLote().getIdLote())
                .numeroLote(detalle.getLote() == null ? null : detalle.getLote().getNumeroLote())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .descuento(detalle.getDescuento())
                .subtotal(detalle.getSubtotal())
                .build();
    }

    private record ResultadoAsignacion(BigDecimal subtotalTotal) {}
}
