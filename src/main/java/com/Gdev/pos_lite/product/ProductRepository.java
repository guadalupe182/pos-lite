package com.Gdev.pos_lite.product;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarcode(String barcode);

    // Para listar con categoría cargada (evita N+1 y problemas de proxy)
    @Query("select p from Product p join fetch p.category")
    List<Product> findAllWithCategory();

    // Low stock: si threshold es NULL usa minStock; si viene valor usa threshold
    @Query("""
           select p from Product p join fetch p.category
           where (:t is null and p.stock < p.minStock)
              or (:t is not null and p.stock < :t)
           """)
    List<Product> findLowStock(@Param("t") Integer threshold);

    // Para ajustes concurrentes por código
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findWithLockByBarcode(String barcode);
}
