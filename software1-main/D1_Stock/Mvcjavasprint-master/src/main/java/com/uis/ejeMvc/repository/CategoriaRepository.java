package com.uis.ejeMvc.repository;

import com.uis.ejeMvc.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
