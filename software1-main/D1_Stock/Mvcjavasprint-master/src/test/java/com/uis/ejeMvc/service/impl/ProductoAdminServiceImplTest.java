package com.uis.ejeMvc.service.impl;

import com.uis.ejeMvc.dto.producto.AgregarStockRequestDTO;
import com.uis.ejeMvc.dto.producto.ProductoResumenDTO;
import com.uis.ejeMvc.dto.producto.RegistrarProductoRequestDTO;
import com.uis.ejeMvc.enums.UnidadMedida;
import com.uis.ejeMvc.exception.ProductoDuplicadoException;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoAdminServiceImplTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private InventarioRepository inventarioRepository;
    @Mock private LoteProductoRepository loteProductoRepository;
    @Mock private MovimientoInventarioRepository movimientoInventarioRepository;

    @InjectMocks private ProductoAdminServiceImpl service;

    private Categoria categoria() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Abarrotes");
        return categoria;
    }

    private Producto productoExistente() {
        Producto producto = new Producto();
        producto.setIdProducto(10);
        producto.setNombre("Arroz Diana");
        producto.setCodigoBarras("7700000000001");
        producto.setCategoria(categoria());
        producto.setPrecioVenta(new BigDecimal("2500.00"));
        producto.setEsPerecedero(false);
        return producto;
    }

    @Test
    void registrarProducto_lanzaDuplicadoCuandoElCodigoDeBarrasYaExiste() {
        RegistrarProductoRequestDTO request = new RegistrarProductoRequestDTO();
        request.setCodigoBarras("7700000000001");
        request.setNombre("Arroz Diana");
        request.setIdCategoria(1);
        request.setUnidadMedida(UnidadMedida.UNIDAD);
        request.setPrecioVenta(new BigDecimal("2500"));

        when(productoRepository.findByCodigoBarras("7700000000001"))
                .thenReturn(Optional.of(productoExistente()));
        when(inventarioRepository.findConsultaByProductoId(10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrarProducto(request))
                .isInstanceOf(ProductoDuplicadoException.class)
                .satisfies(ex -> {
                    ProductoDuplicadoException dup = (ProductoDuplicadoException) ex;
                    assertThat(dup.getProductoExistente().getIdProducto()).isEqualTo(10);
                });

        verify(productoRepository, never()).save(any());
    }

    @Test
    void registrarProducto_creaProductoEInventarioCuandoNoExiste() {
        RegistrarProductoRequestDTO request = new RegistrarProductoRequestDTO();
        request.setNombre("Leche entera 1L");
        request.setIdCategoria(1);
        request.setUnidadMedida(UnidadMedida.LITRO);
        request.setPrecioVenta(new BigDecimal("3200"));

        when(productoRepository.buscarPorNombreYMarca(any(), any())).thenReturn(Optional.empty());
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria()));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setIdProducto(99);
            return p;
        });
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResumenDTO resultado = service.registrarProducto(request);

        assertThat(resultado.getNombre()).isEqualTo("Leche entera 1L");
        assertThat(resultado.getStockActual()).isEqualByComparingTo("0");
        verify(loteProductoRepository, never()).save(any());
        verify(movimientoInventarioRepository, never()).save(any());
    }

    @Test
    void agregarStock_creaLoteYSumaAlInventario() {
        Producto producto = productoExistente();
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setCantidadActual(new BigDecimal("5.000"));

        when(productoRepository.findByIdProductoAndActivoTrue(10)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByProducto_IdProducto(10)).thenReturn(Optional.of(inventario));
        when(loteProductoRepository.save(any(LoteProducto.class))).thenAnswer(inv -> inv.getArgument(0));

        AgregarStockRequestDTO request = new AgregarStockRequestDTO();
        request.setCantidad(new BigDecimal("10"));
        request.setPrecioCompra(new BigDecimal("1800"));

        ProductoResumenDTO resultado = service.agregarStock(10, request);

        assertThat(resultado.getStockActual()).isEqualByComparingTo("15.000");

        ArgumentCaptor<LoteProducto> loteCaptor = ArgumentCaptor.forClass(LoteProducto.class);
        verify(loteProductoRepository).save(loteCaptor.capture());
        assertThat(loteCaptor.getValue().getCantidadDisponible()).isEqualByComparingTo("10.000");

        verify(movimientoInventarioRepository).save(any(MovimientoInventario.class));
    }
}
