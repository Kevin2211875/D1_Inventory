package com.uis.ejeMvc.exception;

import com.uis.ejeMvc.dto.producto.RespuestaProductoDuplicadoDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Traduce cualquier excepción que escape de un controlador a una respuesta {@link ApiError} consistente.
 *
 * <p>Cubre los casos habituales de un backend de tienda: datos de entrada inválidos, reglas de negocio
 * (expresadas con {@link ResponseStatusException} en los servicios), conflictos de concurrencia sobre el
 * inventario, problemas de integridad de datos, autorización y, como último recurso, cualquier error no
 * previsto (que se registra en el log pero nunca se filtra al cliente).</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Validación de {@code @Valid} sobre el cuerpo de la petición: devuelve el detalle por campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleBodyValidation(MethodArgumentNotValidException ex,
                                                         HttpServletRequest request) {
        List<ApiError.CampoInvalido> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toCampoInvalido)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Hay campos inválidos en la petición.", request, errores);
    }

    /** Validación de parámetros anotados ({@code @Validated}) en query params o variables de ruta. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
                                                             HttpServletRequest request) {
        List<ApiError.CampoInvalido> errores = ex.getConstraintViolations().stream()
                .map(v -> ApiError.CampoInvalido.builder()
                        .campo(String.valueOf(v.getPropertyPath()))
                        .mensaje(v.getMessage())
                        .build())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Hay parámetros inválidos en la petición.", request, errores);
    }

    /** Cuerpo JSON ilegible o con un valor de enum inválido (p. ej. método de pago inexistente). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException ex,
                                                        HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no se pudo leer o tiene un valor inválido.",
                request, null);
    }

    /** Cabecera obligatoria, parámetro ausente o con tipo incorrecto. */
    @ExceptionHandler({
            MissingRequestHeaderException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiError> handleBadRequestParams(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    /** Reglas de negocio y validaciones de los servicios (lanzadas como {@link ResponseStatusException}). */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String mensaje = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return build(status, mensaje, request, null);
    }

    /**
     * Producto ya existente: se responde {@code 409} con el producto encontrado, para que el cliente pueda
     * ofrecer "agregar stock" en vez de crear un duplicado.
     */
    @ExceptionHandler(ProductoDuplicadoException.class)
    public ResponseEntity<RespuestaProductoDuplicadoDTO> handleProductoDuplicado(ProductoDuplicadoException ex) {
        RespuestaProductoDuplicadoDTO body = RespuestaProductoDuplicadoDTO.builder()
                .codigo("PRODUCTO_DUPLICADO")
                .message(ex.getMessage())
                .productoExistente(ex.getProductoExistente())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /** Conflictos de concurrencia sobre inventario/lotes (bloqueos): el cliente puede reintentar. */
    @ExceptionHandler({
            OptimisticLockingFailureException.class,
            PessimisticLockingFailureException.class,
            CannotAcquireLockException.class
    })
    public ResponseEntity<ApiError> handleConcurrency(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT,
                "El inventario está siendo modificado por otra operación. Intente nuevamente.", request, null);
    }

    /** Violación de restricciones de la base de datos (unicidad, claves foráneas, no nulos). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex,
                                                       HttpServletRequest request) {
        return build(HttpStatus.CONFLICT,
                "La operación viola una restricción de integridad de los datos.", request, null);
    }

    /**
     * Acceso denegado por seguridad de método ({@code @PreAuthorize}). Distingue entre no autenticado (401)
     * y autenticado sin permisos (403) para que el cliente sepa si debe iniciar sesión o si simplemente no
     * tiene el rol necesario.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean anonimo = auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated();

        HttpStatus status = anonimo ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
        String mensaje = anonimo
                ? "Debe autenticarse para realizar esta operación."
                : "No tiene permisos para realizar esta operación.";
        return build(status, mensaje, request, null);
    }

    /** Último recurso: cualquier error no previsto se registra y se responde de forma genérica. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado. Intente más tarde.", request, null);
    }

    private ApiError.CampoInvalido toCampoInvalido(FieldError fieldError) {
        return ApiError.CampoInvalido.builder()
                .campo(fieldError.getField())
                .mensaje(fieldError.getDefaultMessage())
                .build();
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request,
                                          List<ApiError.CampoInvalido> errores) {
        ApiError body = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .errores(errores == null || errores.isEmpty() ? null : errores)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
