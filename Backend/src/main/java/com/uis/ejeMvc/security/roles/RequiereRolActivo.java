package com.uis.ejeMvc.security.roles;

import com.uis.ejeMvc.enums.rolesSecurity.RolActivo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un controlador o método que necesita el <em>rol activo</em> resuelto para esta petición.
 * <p>No sustituye a {@code @PreAuthorize}: solo resuelve la cabecera
 * {@link RolActivoContextResolver#HEADER_ROL_ACTIVO} (si aplica) y su coherencia con los roles del JWT.
 * Quién puede entrar al endpoint lo define Spring Security; qué hacer con el rol activo lo define la lógica
 * del servicio o del método.</p>
 * <p>Si el JWT implica más de un {@linkplain RolActivo rol activo} posible (p. ej. gerente y vendedor a la vez),
 * la cabecera {@link RolActivoContextResolver#HEADER_ROL_ACTIVO} es obligatoria; con un solo rol posible no hace
 * falta.</p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RequiereRolActivo {
}
