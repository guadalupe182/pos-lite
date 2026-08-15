package com.Gdev.pos_lite.pack;

import com.Gdev.pos_lite.pack.dto.PackCreateRequest;
import com.Gdev.pos_lite.pack.dto.PackDetailResponse;
import com.Gdev.pos_lite.pack.dto.PackItemRequest;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackService {

    private final PackRepository packRepository;

    private final ProductRepository productRepository;

    @Transactional
    public PackSellResponse sellPack(PackSellRequest request) {
        if (request.qty() == null || request.qty() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qty debe ser > 0");
        }
        var pack = packRepository.findByBarcode(request.barcode()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pack no encontrado con barcode " + request.barcode()));
        int qty = request.qty();
        // 1) Validar stock suficiente de TODOS los productos
        for (PackItem item : pack.getItems()) {
            Product product = item.getProduct();
            int needed = item.getQuantity() * qty;
            if (product.getStock() < needed) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para producto " + product.getName() + " (requiere " + needed + ", disponible " + product.getStock() + ")");
            }
        }
        // 2) Si todo ok, descontar stock y armar respuesta
        List<PackSellResponse.ItemDetail> details = new ArrayList<>();
        for (PackItem item : pack.getItems()) {
            Product product = item.getProduct();
            int needed = item.getQuantity() * qty;
            product.setStock(product.getStock() - needed);
            productRepository.save(product);
            details.add(new PackSellResponse.ItemDetail(product.getId(), product.getName(), // piezas por pack
            item.getQuantity(), // piezas totales descontadas
            needed, // stock restante
            product.getStock()));
        }
        return new PackSellResponse(pack.getId(), pack.getName(), pack.getBarcode(), qty, details);
    }

    @Transactional
    public PackDetailResponse createPack(PackCreateRequest request) {
        // normalizar barcode (puede venir null o vacío)
        String rawBarcode = request.barcode();
        String barcode = (rawBarcode == null || rawBarcode.isBlank()) ? null : rawBarcode.trim();
        // Validar que no exista otro pack con el mismo barcode (si viene)
        if (barcode != null && packRepository.findByBarcode(barcode).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un pack con barcode " + barcode);
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pack debe tener al menos 1 producto");
        }
        // Si no viene barcode, lo generamos a partir del primer producto del pack
        if (barcode == null) {
            PackItemRequest firstItem = request.items().get(0);
            var baseProduct = productRepository.findById(firstItem.productId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto base no encontrado con id " + firstItem.productId()));
            String baseCode = baseProduct.getBarcode();
            String tail = baseCode.length() > 6 ? baseCode.substring(baseCode.length() - 6) : baseCode;
            // ejemplo: PCK-<últimos 6 del producto>-X<cantidadPorPack>
            barcode = "PCK-" + tail + "-X" + firstItem.quantity();
        }
        // Construir Pack + PackItems
        Pack pack = Pack.builder().barcode(barcode).name(request.name()).price(request.price()).build();
        var items = request.items().stream().map(itemReq -> {
            var product = productRepository.findById(itemReq.productId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id " + itemReq.productId()));
            return PackItem.builder().pack(pack).product(product).quantity(itemReq.quantity()).build();
        }).collect(Collectors.toList());
        pack.setItems(items);
        Pack saved = packRepository.save(pack);
        return toDetailResponse(saved);
    }

    @Transactional(readOnly = true)
    public PackDetailResponse getByBarcode(String barcode) {
        var pack = packRepository.findByBarcode(barcode).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pack no encontrado con barcode " + barcode));
        return toDetailResponse(pack);
    }

    private PackDetailResponse toDetailResponse(Pack pack) {
        var itemDetails = pack.getItems().stream().map(pi -> new PackDetailResponse.ItemDetail(pi.getProduct().getId(), pi.getProduct().getName(), pi.getQuantity())).collect(Collectors.toList());
        return new PackDetailResponse(pack.getId(), pack.getName(), pack.getBarcode(), pack.getPrice(), itemDetails);
    }
}
