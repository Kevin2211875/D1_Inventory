package com.uis.ejeMvc.repository;

import com.uis.ejeMvc.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
