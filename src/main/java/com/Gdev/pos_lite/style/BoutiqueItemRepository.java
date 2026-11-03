package com.Gdev.pos_lite.style;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BoutiqueItemRepository extends JpaRepository<BoutiqueItem, Long> {

    List<BoutiqueItem> findByTenantId(String tenantId);

    Optional<BoutiqueItem> findByIdAndTenantId(Long id, String tenantId);
}