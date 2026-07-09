package com.uis.ejeMvc.dto.venta;

import com.uis.ejeMvc.enums.MetodoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RegistrarVentaRequestDTO {

    private Integer idCliente;

    private MetodoPago metodoPago;

    private String observacion;

    @Valid
    @NotEmpty(message = "La venta debe contener al menos un producto.")
    private List<ItemVentaRequestDTO> productos;
}
