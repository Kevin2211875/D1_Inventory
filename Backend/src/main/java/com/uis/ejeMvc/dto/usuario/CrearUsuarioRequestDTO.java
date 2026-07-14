package com.uis.ejeMvc.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Alta de un usuario en Keycloak. El rol admitido es GERENTE o VENDEDOR (se valida en el servicio); la
 * contraseña se crea como temporal para que el usuario la cambie en su primer ingreso.
 */
@Getter
@Setter
@NoArgsConstructor
public class CrearUsuarioRequestDTO {

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no es válido.")
    private String email;

    /** Si no se envía, se usa el correo como nombre de usuario. */
    private String username;

    private String nombre;

    private String apellido;

    @NotBlank(message = "La contraseña temporal es obligatoria.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password;

    @NotBlank(message = "El rol es obligatorio.")
    private String rol;
}
