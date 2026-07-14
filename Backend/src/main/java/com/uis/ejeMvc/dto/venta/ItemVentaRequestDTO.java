package com.uis.ejeMvc.dto.venta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ItemVentaRequestDTO {

    /** Opcional si se envía codigoBarras. */
    private Integer idProducto;

    /** Opcional si se envía idProducto. Útil para ventas por escáner. */
    private String codigoBarras;

    @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor a cero.")
    @Digits(integer = 12, fraction = 3)
    private BigDecimal cantidad;

    /** Descuento monetario aplicado a esta línea completa, no porcentaje. */
    @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo.")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
}
