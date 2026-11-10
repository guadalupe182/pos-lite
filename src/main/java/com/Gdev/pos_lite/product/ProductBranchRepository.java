package com.Gdev.pos_lite.product;

import com.Gdev.pos_lite.branch.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductBranchRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByBarcodeAndBranch(String barcode, Branch branch);
    
    @Query("SELECT p FROM Product p WHERE p.branch = :branch AND p.active = true")
    List<Product> findAllActiveByBranch(@Param("branch") Branch branch);
    
    @Query("SELECT p FROM Product p WHERE p.branch = :branch AND p.stock < p.minStock")
    List<Product> findLowStockByBranch(@Param("branch") Branch branch);
}
