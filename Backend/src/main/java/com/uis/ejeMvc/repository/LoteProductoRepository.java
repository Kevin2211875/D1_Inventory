package com.uis.ejeMvc.repository;

import com.uis.ejeMvc.model.LoteProducto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoteProductoRepository extends JpaRepository<LoteProducto, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT l
            FROM LoteProducto l
            WHERE l.producto.idProducto = :idProducto
              AND l.cantidadDisponible > 0
              AND (l.fechaVencimiento IS NULL OR l.fechaVencimiento >= CURRENT_DATE)
            ORDER BY
              CASE WHEN l.fechaVencimiento IS NULL THEN 1 ELSE 0 END ASC,
              l.fechaVencimiento ASC,
              l.fechaIngreso ASC,
              l.idLote ASC
            """)
    List<LoteProducto> findLotesDisponiblesParaVentaForUpdate(@Param("idProducto") Integer idProducto);
}
