package com.uis.ejeMvc.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Restablece la contraseña de un usuario. Por defecto queda como temporal. */
@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordRequestDTO {

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password;

    /** Si es {@code true} (por defecto), el usuario deberá cambiarla en su próximo ingreso. */
    private Boolean temporal;
}
