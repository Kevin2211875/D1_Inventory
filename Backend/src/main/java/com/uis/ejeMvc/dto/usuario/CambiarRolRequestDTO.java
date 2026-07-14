package com.uis.ejeMvc.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Reasignación de rol de un usuario (GERENTE o VENDEDOR). */
@Getter
@Setter
@NoArgsConstructor
public class CambiarRolRequestDTO {

    @NotBlank(message = "El rol es obligatorio.")
    private String rol;
}
