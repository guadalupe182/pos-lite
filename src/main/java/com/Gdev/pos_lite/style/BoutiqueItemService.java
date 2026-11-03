package com.Gdev.pos_lite.style;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BoutiqueItemService {

    private final BoutiqueItemRepository boutiqueItemRepository;

    public BoutiqueItemService(BoutiqueItemRepository boutiqueItemRepository) {
        this.boutiqueItemRepository = boutiqueItemRepository;
    }

    @Transactional(readOnly = true)
    public List<BoutiqueItem> findAll(String tenantId) {
        return boutiqueItemRepository.findByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public BoutiqueItem findById(Long id, String tenantId) {
        return boutiqueItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado: " + id));
    }

    @Transactional
    public BoutiqueItem create(BoutiqueItem item, String tenantId) {
        item.setTenantId(tenantId);
        return boutiqueItemRepository.save(item);
    }

    @Transactional
    public BoutiqueItem update(Long id, BoutiqueItem input, String tenantId) {
        BoutiqueItem db = findById(id, tenantId);
        db.setName(input.getName());
        db.setSize(input.getSize());
        db.setColor(input.getColor());
        db.setPrice(input.getPrice());
        db.setStock(input.getStock());
        return boutiqueItemRepository.save(db);
    }

    @Transactional
    public void delete(Long id, String tenantId) {
        boutiqueItemRepository.delete(findById(id, tenantId));
    }
}