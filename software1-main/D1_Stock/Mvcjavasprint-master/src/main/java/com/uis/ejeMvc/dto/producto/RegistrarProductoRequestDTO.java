package com.uis.ejeMvc.dto.producto;

import com.uis.ejeMvc.enums.UnidadMedida;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Alta de un producto nuevo. Pensado para que el formulario del vendedor/gerente sea corto: solo lo esencial
 * es obligatorio y, opcionalmente, se puede cargar el primer lote de stock en el mismo paso ({@link #stockInicial}).
 */
@Getter
@Setter
@NoArgsConstructor
public class RegistrarProductoRequestDTO {

    @Size(max = 50, message = "El código de barras no puede superar 50 caracteres.")
    private String codigoBarras;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres.")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La categoría es obligatoria.")
    private Integer idCategoria;

    @Size(max = 100, message = "La marca no puede superar 100 caracteres.")
    private String marca;

    @NotNull(message = "La unidad de medida es obligatoria.")
    private UnidadMedida unidadMedida;

    @NotNull(message = "El precio de venta es obligatorio.")
    @Positive(message = "El precio de venta debe ser mayor a cero.")
    private BigDecimal precioVenta;

    /** IVA aplicado al producto (0 si no aplica). */
    @PositiveOrZero(message = "El porcentaje de IVA no puede ser negativo.")
    private BigDecimal porcentajeIva;

    private Boolean esPerecedero;

    /** Stock mínimo para alertas de inventario. */
    @PositiveOrZero(message = "La cantidad mínima no puede ser negativa.")
    private BigDecimal cantidadMinima;

    private String ubicacion;

    /** Primer lote de stock (opcional): si se envía, se registra junto con el producto. */
    @Valid
    private AgregarStockRequestDTO stockInicial;
}
