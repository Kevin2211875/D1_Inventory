package com.uis.ejeMvc.dto.producto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entrada de stock de un producto: se registra siempre como un lote (coherente con la venta FEFO existente).
 * <p>La {@code fechaVencimiento} es obligatoria solo si el producto es perecedero; esa regla se valida en el
 * servicio porque depende del producto, no del propio request.</p>
 */
@Getter
@Setter
@NoArgsConstructor
public class AgregarStockRequestDTO {

    @NotNull(message = "La cantidad es obligatoria.")
    @Positive(message = "La cantidad debe ser mayor a cero.")
    private BigDecimal cantidad;

    @NotNull(message = "El precio de compra es obligatorio.")
    @Positive(message = "El precio de compra debe ser mayor a cero.")
    private BigDecimal precioCompra;

    /** Identificador del lote del proveedor (opcional). */
    private String numeroLote;

    /** Fecha de vencimiento del lote; obligatoria para productos perecederos. */
    private LocalDate fechaVencimiento;
}
