package com.uis.ejeMvc.dto.categoria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Categoría para poblar el selector del formulario de producto.
 */
@Getter
@Builder
@AllArgsConstructor
public class CategoriaDTO {
    private final Integer idCategoria;
    private final String nombre;
    private final Integer idCategoriaPadre;
}
