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
    public List<BoutiqueItem> findAll() {
        return boutiqueItemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public BoutiqueItem findById(Long id) {
        return boutiqueItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado: " + id));
    }

    @Transactional
    public BoutiqueItem create(BoutiqueItem item) {
        return boutiqueItemRepository.save(item);
    }

    @Transactional
    public BoutiqueItem update(Long id, BoutiqueItem input) {
        BoutiqueItem db = findById(id);
        db.setName(input.getName());
        db.setSize(input.getSize());
        db.setColor(input.getColor());
        db.setPrice(input.getPrice());
        db.setStock(input.getStock());
        db.setTenantId(input.getTenantId());
        return boutiqueItemRepository.save(db);
    }

    @Transactional
    public void delete(Long id) {
        boutiqueItemRepository.deleteById(id);
    }
}