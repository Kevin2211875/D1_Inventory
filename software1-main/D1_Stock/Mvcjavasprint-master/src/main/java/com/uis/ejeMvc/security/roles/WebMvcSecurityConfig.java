package com.uis.ejeMvc.security.roles;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registra el {@link RolActivoHandlerInterceptor} para todas las rutas.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcSecurityConfig implements WebMvcConfigurer {

    private final RolActivoHandlerInterceptor rolActivoHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rolActivoHandlerInterceptor).addPathPatterns("/**");
    }
}
