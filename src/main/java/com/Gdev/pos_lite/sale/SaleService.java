package com.Gdev.pos_lite.sale;

import com.Gdev.pos_lite.product.Product;
import com.Gdev.pos_lite.product.ProductRepository;
import com.Gdev.pos_lite.sale.dto.InventoryReportDto;
import com.Gdev.pos_lite.sale.dto.SaleItemRequest;
import com.Gdev.pos_lite.sale.dto.SaleRequest;
import com.Gdev.pos_lite.user.User;
import com.Gdev.pos_lite.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public SaleService(SaleRepository saleRepository,
                       SaleDetailRepository saleDetailRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.saleRepository = saleRepository;
        this.saleDetailRepository = saleDetailRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Sale registerSale(SaleRequest request, String userEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Sale sale = new Sale();
        sale.setUser(user);
        sale.setSaleDate(Instant.now());

        double total = 0.0;

        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + itemReq.getProductId()));

            if (product.getStock() < itemReq.getQuantity()) {
                throw new IllegalArgumentException("Stock insuficiente para producto: " + product.getName());
            }

            // Descontar stock
            product.setStock(product.getStock() - itemReq.getQuantity());
            productRepository.save(product);

            // Obtener precio como BigDecimal y convertirlo a double para operar
            BigDecimal price = product.getPrice(); // asumiendo que getPrice() devuelve BigDecimal
            double unitPrice = price.doubleValue();
            double subtotal = unitPrice * itemReq.getQuantity();
            total += subtotal;

            SaleDetail detail = new SaleDetail();
            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(itemReq.getQuantity());
            detail.setUnitPrice(unitPrice);
            detail.setSubtotal(subtotal);

            sale.getDetails().add(detail);
        }

        sale.setTotal(total);
        return saleRepository.save(sale);
    }

    public List<Sale> getSalesBetween(Instant from, Instant to) {
        return saleRepository.findBySaleDateBetween(from, to);
    }

    public List<InventoryReportDto> getInventoryReport() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(p -> new InventoryReportDto(
                        p.getId(),
                        p.getBarcode(),   // ← añadir barcode
                        p.getName(),
                        p.getStock(),
                        p.getMinStock(),
                        p.getStock() < p.getMinStock()
                ))
                .collect(Collectors.toList());
    }
}