package com.Gdev.pos_lite.pack;

import com.Gdev.pos_lite.pack.dto.PackSellRequest;
import com.Gdev.pos_lite.pack.dto.PackSellResponse;
import com.Gdev.pos_lite.product.Product;
import com.Gdev.pos_lite.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PackService {

    private final PackRepository packRepository;
    private final ProductRepository productRepository;

    @Transactional
    public PackSellResponse sellPack(PackSellRequest request) {

        if (request.qty() == null || request.qty() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "qty debe ser > 0");
        }

        var pack = packRepository.findByBarcode(request.barcode())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pack no encontrado con barcode " + request.barcode()
                ));

        int qty = request.qty();

        // 1) Validar stock suficiente de TODOS los productos
        for (PackItem item : pack.getItems()) {
            Product product = item.getProduct();
            int needed = item.getQuantity() * qty;

            if (product.getStock() < needed) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Stock insuficiente para producto " + product.getName() +
                                " (requiere " + needed + ", disponible " + product.getStock() + ")"
                );
            }
        }

        // 2) Si todo ok, descontar stock y armar respuesta
        List<PackSellResponse.ItemDetail> details = new ArrayList<>();

        for (PackItem item : pack.getItems()) {
            Product product = item.getProduct();
            int needed = item.getQuantity() * qty;

            product.setStock(product.getStock() - needed);
            productRepository.save(product);

            details.add(new PackSellResponse.ItemDetail(
                    product.getId(),
                    product.getName(),
                    item.getQuantity(),
                    needed,
                    product.getStock()
            ));
        }

        return new PackSellResponse(
                pack.getId(),
                pack.getName(),
                pack.getBarcode(),
                qty,
                details
        );
    }
}
