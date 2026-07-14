package com.uis.ejeMvc.repository;

import com.uis.ejeMvc.model.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Integer> {
    Optional<UsuarioSistema> findByKeycloakSub(UUID keycloakSub);
}
