package com.Gdev.pos_lite.branch;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByNameIgnoreCase(String name);
    List<Branch> findByActiveTrue();
}
