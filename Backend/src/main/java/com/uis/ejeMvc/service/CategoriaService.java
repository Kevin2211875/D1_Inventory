package com.uis.ejeMvc.service;

import com.uis.ejeMvc.dto.categoria.CategoriaDTO;

import java.util.List;

/** Consulta de categorías para poblar selectores del frontend. */
public interface CategoriaService {
    List<CategoriaDTO> listarCategorias();
}
