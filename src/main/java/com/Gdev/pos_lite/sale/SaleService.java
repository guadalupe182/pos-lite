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
import java.math.RoundingMode;
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
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userEmail));

        Sale sale = new Sale();
        sale.setUser(user);
        sale.setSaleDate(Instant.now());

        BigDecimal totalBD = BigDecimal.ZERO;

        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + itemReq.getProductId()));

            if (product.getStock() < itemReq.getQuantity()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getName());
            }

            // Descontar inventario
            product.setStock(product.getStock() - itemReq.getQuantity());

            // Multiplicación en BigDecimal
            // product.getPrice() ya es un BigDecimal
            BigDecimal unitPrice = product.getPrice().setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity())).setScale(2, RoundingMode.HALF_UP);
            totalBD = totalBD.add(subtotal);

            SaleDetail detail = new SaleDetail();
            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(itemReq.getQuantity());
            detail.setUnitPrice(unitPrice.doubleValue());
            detail.setSubtotal(subtotal.doubleValue());

            sale.getDetails().add(detail);
        }

        BigDecimal totalFinal = totalBD.setScale(2, RoundingMode.HALF_UP);

        // Procesamiento del efectivo recibido y cambio
        if (request.getCashReceived() != null && request.getCashReceived() > 0) {
            BigDecimal cash = BigDecimal.valueOf(request.getCashReceived()).setScale(2, RoundingMode.HALF_UP);

            if (cash.compareTo(totalFinal) < 0) {
                throw new IllegalArgumentException("El efectivo recibido ($" + cash + ") es menor al total de la venta ($" + totalFinal + ")");
            }

            BigDecimal change = cash.subtract(totalFinal).setScale(2, RoundingMode.HALF_UP);

            sale.setCashReceived(cash.doubleValue());
            sale.setChange(change.doubleValue());
        } else {
            sale.setCashReceived(totalFinal.doubleValue());
            sale.setChange(0.00);
        }

        sale.setTotal(totalFinal.doubleValue());
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
                        p.getBarcode(),
                        p.getName(),
                        p.getStock(),
                        p.getMinStock(),
                        p.getStock() < p.getMinStock()
                ))
                .collect(Collectors.toList());
    }
}