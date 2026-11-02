package com.Gdev.pos_lite.style;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoutiqueItemRepository extends JpaRepository<BoutiqueItem, Long> {

    List<BoutiqueItem> findByTenantId(String tenantId);
}