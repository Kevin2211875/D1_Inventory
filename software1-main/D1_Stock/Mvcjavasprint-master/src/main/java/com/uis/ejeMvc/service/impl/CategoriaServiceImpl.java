package com.uis.ejeMvc.service.impl;

import com.uis.ejeMvc.dto.categoria.CategoriaDTO;
import com.uis.ejeMvc.model.Categoria;
import com.uis.ejeMvc.repository.CategoriaRepository;
import com.uis.ejeMvc.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    public List<CategoriaDTO> listarCategorias() {
        return categoriaRepository.findAll(Sort.by(Sort.Direction.ASC, "nombre")).stream()
                .map(this::toDTO)
                .toList();
    }

    private CategoriaDTO toDTO(Categoria categoria) {
        return CategoriaDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .idCategoriaPadre(categoria.getCategoriaPadre() == null ? null : categoria.getCategoriaPadre().getIdCategoria())
                .build();
    }
}
