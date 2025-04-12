package com.uis.ejeMvc.repository;

import com.uis.ejeMvc.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepositoryInter extends JpaRepository<Persona, Long> {
    Persona findByUsuarioAndContrasena(String usuario, String contrasena);
}
