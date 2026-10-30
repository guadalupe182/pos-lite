package com.Gdev.pos_lite.sale;

import com.Gdev.pos_lite.cash.CashService;
import com.Gdev.pos_lite.cash.CashSession;
import com.Gdev.pos_lite.cash.CashSessionService;
import com.Gdev.pos_lite.loyalty.LoyaltyAccount;
import com.Gdev.pos_lite.loyalty.LoyaltyAccountRepository;
import com.Gdev.pos_lite.loyalty.dto.RedeemRequest;
import com.Gdev.pos_lite.loyalty.dto.RedeemResponse;
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
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private static final BigDecimal POINT_VALUE_MXN = BigDecimal.ONE;
    private static final BigDecimal MXN_PER_EARNED_POINT = BigDecimal.TEN;

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CashService cashService;
    private final CashSessionService cashSessionService;
    private final NotificationService notificationService;
    private final LoyaltyAccountRepository loyaltyAccountRepository;

    public SaleService(SaleRepository saleRepository,
                       SaleDetailRepository saleDetailRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       CashService cashService,
                       CashSessionService cashSessionService,
                       NotificationService notificationService,
                       LoyaltyAccountRepository loyaltyAccountRepository) {
        this.saleRepository = saleRepository;
        this.saleDetailRepository = saleDetailRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cashService = cashService;
        this.cashSessionService = cashSessionService;
        this.notificationService = notificationService;
        this.loyaltyAccountRepository = loyaltyAccountRepository;
    }

    @Transactional
    public Sale registerSale(SaleRequest request, String userEmail) {
        return registerSale(request, userEmail, false);
    }

    @Transactional
    public Sale registerSale(SaleRequest request, String userEmail, boolean loyaltyEnabled) {
        long pointsToRedeem = request.getPointsToRedeem() == null ? 0 : request.getPointsToRedeem();
        String customerEmail = normalizeEmail(request.getCustomerEmail());
        validateRedemptionRequest(pointsToRedeem, customerEmail, loyaltyEnabled);

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
        sale.setCustomerEmail(customerEmail);

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

        double finalTotal = total;
        LoyaltyAccount loyaltyAccount = null;

        if (pointsToRedeem > 0) {
            loyaltyAccount = loyaltyAccountRepository.findByCustomerEmailIgnoreCase(customerEmail)
                    .orElseThrow(() -> new IllegalArgumentException("El cliente no tiene una cuenta de puntos"));

            BigDecimal discount = BigDecimal.valueOf(pointsToRedeem).multiply(POINT_VALUE_MXN);
            if (discount.compareTo(BigDecimal.valueOf(total)) > 0) {
                throw new IllegalArgumentException("El descuento no puede ser mayor al total de la venta");
            }

            loyaltyAccount.redeem(pointsToRedeem);
            finalTotal = BigDecimal.valueOf(total).subtract(discount).doubleValue();
            sale.setPointsRedeemed(pointsToRedeem);
            sale.setLoyaltyDiscount(discount.doubleValue());
        }

        sale.setTotal(finalTotal);

        if (loyaltyEnabled && customerEmail != null) {
            if (loyaltyAccount == null) {
                loyaltyAccount = findOrCreateLoyaltyAccount(customerEmail);
            }
            long pointsEarned = BigDecimal.valueOf(finalTotal)
                    .divide(MXN_PER_EARNED_POINT, 0, RoundingMode.FLOOR)
                    .longValue();
            loyaltyAccount.earn(pointsEarned);
            sale.setPointsEarned(pointsEarned);
            loyaltyAccountRepository.save(loyaltyAccount);
        }

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
            if (received < finalTotal) {
                throw new IllegalArgumentException("Efectivo insuficiente. Total: " + finalTotal + ", Recibido: " + received);
            }
            double change = received - finalTotal;
            sale.setCashReceived(received);
            sale.setCashChange(change);
        } else {
            sale.setCashReceived(null);
            sale.setCashChange(null);
        }

        return saleRepository.save(sale);
    }

    public RedeemResponse previewRedemption(RedeemRequest request) {
        if (request.getPoints() == null || request.getPoints() <= 0) {
            throw new IllegalArgumentException("Los puntos a canjear deben ser mayores que cero");
        }
        if (request.getSaleTotal() == null || !Double.isFinite(request.getSaleTotal())
                || request.getSaleTotal() < 0) {
            throw new IllegalArgumentException("El total de la venta debe ser un importe válido");
        }

        String customerEmail = normalizeEmail(request.getCustomerEmail());
        if (customerEmail == null) {
            throw new IllegalArgumentException("El email del cliente es requerido para canjear puntos");
        }

        long pointsToRedeem = request.getPoints();
        BigDecimal saleTotal = BigDecimal.valueOf(request.getSaleTotal());
        BigDecimal discount = BigDecimal.valueOf(pointsToRedeem).multiply(POINT_VALUE_MXN);
        if (discount.compareTo(saleTotal) > 0) {
            throw new IllegalArgumentException("El descuento no puede ser mayor al total de la venta");
        }

        LoyaltyAccount account = loyaltyAccountRepository.findByCustomerEmailIgnoreCase(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("El cliente no tiene una cuenta de puntos"));
        if (pointsToRedeem > account.getPointsBalance()) {
            throw new IllegalArgumentException("Saldo de puntos insuficiente");
        }

        return new RedeemResponse(
                customerEmail,
                pointsToRedeem,
                discount.doubleValue(),
                saleTotal.subtract(discount).doubleValue(),
                account.getPointsBalance() - pointsToRedeem);
    }

    public long getPointsBalance(String customerEmail) {
        String normalizedEmail = normalizeEmail(customerEmail);
        if (normalizedEmail == null) {
            return 0;
        }
        return loyaltyAccountRepository.findByCustomerEmailIgnoreCase(normalizedEmail)
                .map(LoyaltyAccount::getPointsBalance)
                .orElse(0L);
    }

    private void validateRedemptionRequest(long pointsToRedeem, String customerEmail, boolean loyaltyEnabled) {
        if (pointsToRedeem < 0) {
            throw new IllegalArgumentException("Los puntos a canjear no pueden ser negativos");
        }
        if (pointsToRedeem > 0 && !loyaltyEnabled) {
            throw new IllegalArgumentException("El módulo de fidelización no está habilitado");
        }
        if (pointsToRedeem > 0 && customerEmail == null) {
            throw new IllegalArgumentException("El email del cliente es requerido para canjear puntos");
        }
    }

    private LoyaltyAccount findOrCreateLoyaltyAccount(String customerEmail) {
        return loyaltyAccountRepository.findByCustomerEmailIgnoreCase(customerEmail)
                .orElseGet(() -> {
                    LoyaltyAccount account = new LoyaltyAccount();
                    account.setCustomerEmail(customerEmail);
                    return account;
                });
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase();
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