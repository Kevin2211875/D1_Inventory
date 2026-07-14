package com.uis.ejeMvc.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cuerpo de error uniforme para todas las respuestas de la API. Lo produce
 * {@link GlobalExceptionHandler} para que el frontend siempre reciba la misma estructura
 * ante cualquier fallo (validación, negocio, seguridad o error inesperado).
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    /** Momento en que se generó la respuesta de error. */
    private final LocalDateTime timestamp;

    /** Código HTTP (p. ej. 400, 404, 409). */
    private final int status;

    /** Frase estándar del código HTTP (p. ej. "Bad Request"). */
    private final String error;

    /** Mensaje legible orientado a quien consume la API. */
    private final String message;

    /** Ruta de la petición que provocó el error. */
    private final String path;

    /** Detalle campo a campo cuando el error proviene de la validación de la petición. */
    private final List<CampoInvalido> errores;

    /** Error de validación asociado a un campo concreto de la petición. */
    @Getter
    @Builder
    public static class CampoInvalido {
        private final String campo;
        private final String mensaje;
    }
}
