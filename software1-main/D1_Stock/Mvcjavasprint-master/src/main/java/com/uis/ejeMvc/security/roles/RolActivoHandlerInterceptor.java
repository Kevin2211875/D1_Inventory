package com.uis.ejeMvc.security.roles;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Resuelve el rol activo antes de ejecutar los handlers anotados con {@link RequiereRolActivo}
 * (en el método o en la clase del controlador). Para el resto de peticiones no hace nada.
 */
@Component
@RequiredArgsConstructor
public class RolActivoHandlerInterceptor implements HandlerInterceptor {

    private final RolActivoContextResolver rolActivoContextResolver;

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request,
                             jakarta.servlet.http.HttpServletResponse response,
                             Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequiereRolActivo anotacion = AnnotatedElementUtils.findMergedAnnotation(
                handlerMethod.getMethod(),
                RequiereRolActivo.class
        );
        if (anotacion == null) {
            anotacion = AnnotatedElementUtils.findMergedAnnotation(
                    handlerMethod.getBeanType(),
                    RequiereRolActivo.class
            );
        }
        if (anotacion == null) {
            return true;
        }

        rolActivoContextResolver.resolverParaRequestActual();
        return true;
    }
}
