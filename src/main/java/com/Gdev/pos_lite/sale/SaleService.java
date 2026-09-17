package com.Gdev.pos_lite.sale;

import com.Gdev.pos_lite.cash.CashService;
import com.Gdev.pos_lite.cash.CashSession;
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
    private final CashService cashService;
    private final CashSessionService cashSessionService;
    private final NotificationService notificationService;

    public SaleService(SaleRepository saleRepository,
                       SaleDetailRepository saleDetailRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       CashService cashService,
                       CashSessionService cashSessionService,
                       NotificationService notificationService) {
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
        // 1. Validar y obtener la sesión de caja activa del cajero
        CashSession currentSession = cashSessionService.getCurrentOpenSessionForUser(userEmail);

        // 2. Buscar usuario que registra la venta
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 3. Inicializar la entidad Sale y asociarla a la caja
        Sale sale = new Sale();
        sale.setUser(user);
        sale.setCashSession(currentSession);
        sale.setSaleDate(Instant.now());

        double total = 0.0;
        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + itemReq.getProductId()));

            if (product.getStock() < itemReq.getQuantity()) {
                throw new IllegalArgumentException("Stock insuficiente para producto: " + product.getName());
            }

            // Descontar stock y notificar si está bajo el mínimo
            if (product.getStock() < product.getMinStock()) {
                String message = "Producto '" + product.getName() + "' tiene stock bajo. " + product.getStock() + " unidades";
                notificationService.createNotification("STOCK_LOW", message);
            }
            productRepository.save(product);

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

        // 4. Asignar método de pago y procesar cambio si es en efectivo
        String paymentMethod = request.getPaymentMethod();
        if (paymentMethod == null || paymentMethod.isBlank()) {
            paymentMethod = "MERCADO_PAGO";
        }

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
        return products.stream()
                .map(p -> new InventoryReportDto(
                        p.getId(),
                        p.getBarcode(),
                        p.getName(),
                        p.getStock(),
                        p.getMinStock(),
                        p.getStock() < p.getMinStock()))
                .collect(Collectors.toList());
    }
}