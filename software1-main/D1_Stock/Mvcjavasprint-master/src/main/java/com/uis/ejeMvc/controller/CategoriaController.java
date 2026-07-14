package com.uis.ejeMvc.controller;

import com.uis.ejeMvc.dto.categoria.CategoriaDTO;
import com.uis.ejeMvc.security.roles.SecureRoles;
import com.uis.ejeMvc.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Consulta de categorías para el formulario de productos. Solo lectura, disponible para todo el personal.
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@PreAuthorize(SecureRoles.STAFF_TIENDA)
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listar() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }
}
