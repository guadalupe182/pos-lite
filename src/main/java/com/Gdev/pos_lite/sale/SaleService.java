package com.Gdev.pos_lite.sale;

import com.Gdev.pos_lite.cash.CashService;
import com.Gdev.pos_lite.cash.CashSessionService;
import com.Gdev.pos_lite.notification.NotificationService;
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

    //Pending use
    private final CashService cashService;

    private final CashSessionService cashSessionService;

    private final NotificationService notificationService;

    public SaleService(SaleRepository saleRepository, SaleDetailRepository saleDetailRepository, ProductRepository productRepository, UserRepository userRepository, CashService cashService, CashSessionService cashSessionService, NotificationService notificationService) {
        this.saleRepository = saleRepository;
        this.saleDetailRepository = saleDetailRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cashService = cashService;
        this.cashSessionService = cashSessionService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Sale registerSale(SaleRequest request, String userEmail) {
        // Validar caja cerrada
        if (!cashSessionService.isOpen()) {
            throw new IllegalStateException("No hay una sesión de caja abierta. Debe abrir caja antes de vender.");
        }
        User user = userRepository.findByEmailIgnoreCase(userEmail).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Sale sale = new Sale();
        sale.setUser(user);
        sale.setSaleDate(Instant.now());
        double total = 0.0;
        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId()).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + itemReq.getProductId()));
            if (product.getStock() < itemReq.getQuantity()) {
                throw new IllegalArgumentException("Stock insuficiente para producto: " + product.getName());
            }
            // Descontar stock
            if (product.getStock() < product.getMinStock()) {
                String message = "Producto '" + product.getName() + "' tiene stock bajo." + product.getStock() + "unidades";
                notificationService.createNotification("STOCK_LOW", message);
            }
            productRepository.save(product);
            // Obtener precio como BigDecimal y convertirlo a double para operar
            // asumiendo que getPrice() devuelve BigDecimal
            BigDecimal price = product.getPrice();
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
        // Asignar método de pago
        String paymentMethod = request.getPaymentMethod();
        if (paymentMethod == null || paymentMethod.isBlank()) {
            paymentMethod = "MERCADO_PAGO";
        }
        System.out.println("=== paymentMethod recibido: " + paymentMethod);
        sale.setPaymentMethod(paymentMethod);
        if ("CASH".equals(paymentMethod)) {
            Double received = request.getCashReceived();
            if (received == null) {
                throw new IllegalArgumentException("Para pago en efectivo debe enviar cashReceived");
            }
            if (received < total) {
                throw new IllegalArgumentException("Efectivo insuficiente. Total: " + total + ", Recibido: " + received);
            }
            double change = received - total;
            sale.setCashReceived(received);
            sale.setCashChange(change);
        } else {
            // Para otros métodos de pago, estos campos se dejan en null
            sale.setCashReceived(null);
            sale.setCashChange(null);
        }
        return saleRepository.save(sale);
    }

    public List<Sale> getSalesBetween(Instant from, Instant to) {
        return saleRepository.findBySaleDateBetween(from, to);
    }

    public List<InventoryReportDto> getInventoryReport() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(p -> new InventoryReportDto(p.getId(), // ← añadir barcode
        p.getBarcode(), p.getName(), p.getStock(), p.getMinStock(), p.getStock() < p.getMinStock())).collect(Collectors.toList());
    }
}
