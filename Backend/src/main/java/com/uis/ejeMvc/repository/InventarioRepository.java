package com.uis.ejeMvc.repository;

import com.uis.ejeMvc.model.Inventario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventario> findByProducto_IdProducto(Integer idProducto);

    @Query("SELECT i FROM Inventario i WHERE i.producto.idProducto = :idProducto")
    Optional<Inventario> findConsultaByProductoId(@Param("idProducto") Integer idProducto);
}

