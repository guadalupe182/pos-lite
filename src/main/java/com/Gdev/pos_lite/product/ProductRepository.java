package com.Gdev.pos_lite.product;

import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarcode(String barcode);

    @Query("select p from Product p join fetch p.category")
    List<Product> findAllWithCategory();
}
