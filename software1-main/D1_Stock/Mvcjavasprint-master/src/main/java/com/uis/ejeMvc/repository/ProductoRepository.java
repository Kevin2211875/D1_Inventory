package com.uis.ejeMvc.repository;

import com.uis.ejeMvc.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    Optional<Producto> findByCodigoBarrasAndActivoTrue(String codigoBarras);

    Optional<Producto> findByIdProductoAndActivoTrue(Integer idProducto);

    /**
     * Busca por código de barras <em>incluyendo inactivos</em>: la unicidad de {@code codigo_barras} aplica
     * siempre, así que sirve para detectar duplicados antes de insertar.
     */
    Optional<Producto> findByCodigoBarras(String codigoBarras);

    /**
     * Detecta duplicados por nombre + marca (ambos sin distinguir mayúsculas) para productos sin código de
     * barras. Trata la marca nula como comodín simétrico (nulo coincide con nulo).
     */
    @Query("""
            SELECT p FROM Producto p
            WHERE LOWER(p.nombre) = LOWER(:nombre)
              AND ((:marca IS NULL AND p.marca IS NULL) OR LOWER(p.marca) = LOWER(:marca))
            """)
    Optional<Producto> buscarPorNombreYMarca(@Param("nombre") String nombre, @Param("marca") String marca);

    Page<Producto> findByActivoTrueAndNombreContainingIgnoreCaseOrderByNombreAsc(String nombre, Pageable pageable);

    Page<Producto> findByActivoTrueOrderByNombreAsc(Pageable pageable);

    @Query(
            value = """
                    WITH RECURSIVE categorias AS (
                        SELECT c.id_categoria
                        FROM categoria c
                        WHERE c.id_categoria = :idCategoria
                        UNION ALL
                        SELECT h.id_categoria
                        FROM categoria h
                        INNER JOIN categorias p ON h.id_categoria_padre = p.id_categoria
                    )
                    SELECT p.*
                    FROM producto p
                    WHERE p.activo = true
                      AND p.id_categoria IN (SELECT id_categoria FROM categorias)
                      AND (:nombre IS NULL OR LOWER(p.nombre) LIKE CONCAT('%', LOWER(CAST(:nombre AS text)), '%'))
                    ORDER BY p.nombre ASC
                    """,
            countQuery = """
                    WITH RECURSIVE categorias AS (
                        SELECT c.id_categoria
                        FROM categoria c
                        WHERE c.id_categoria = :idCategoria
                        UNION ALL
                        SELECT h.id_categoria
                        FROM categoria h
                        INNER JOIN categorias p ON h.id_categoria_padre = p.id_categoria
                    )
                    SELECT COUNT(*)
                    FROM producto p
                    WHERE p.activo = true
                      AND p.id_categoria IN (SELECT id_categoria FROM categorias)
                      AND (:nombre IS NULL OR LOWER(p.nombre) LIKE CONCAT('%', LOWER(CAST(:nombre AS text)), '%'))
                    """,
            nativeQuery = true
    )
    Page<Producto> buscarActivosPorCategoriaIncluyendoHijas(
            @Param("idCategoria") Integer idCategoria,
            @Param("nombre") String nombre,
            Pageable pageable
    );
}
