package com.uis.ejeMvc.dto.usuario;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Habilita o deshabilita el acceso de un usuario. */
@Getter
@Setter
@NoArgsConstructor
public class CambiarEstadoRequestDTO {

    @NotNull(message = "Debe indicar si el usuario queda habilitado.")
    private Boolean enabled;
}
