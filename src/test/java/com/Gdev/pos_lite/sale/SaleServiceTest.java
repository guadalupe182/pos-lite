package com.Gdev.pos_lite.sale;

import com.Gdev.pos_lite.cash.CashService;
import com.Gdev.pos_lite.cash.CashSession;
import com.Gdev.pos_lite.cash.CashSessionService;
import com.Gdev.pos_lite.loyalty.LoyaltyAccount;
import com.Gdev.pos_lite.loyalty.LoyaltyAccountRepository;
import com.Gdev.pos_lite.notification.NotificationService;
import com.Gdev.pos_lite.product.Product;
import com.Gdev.pos_lite.product.ProductRepository;
import com.Gdev.pos_lite.sale.dto.SaleItemRequest;
import com.Gdev.pos_lite.sale.dto.SaleRequest;
import com.Gdev.pos_lite.user.User;
import com.Gdev.pos_lite.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    private static final String SELLER_EMAIL = "seller@example.com";
    private static final String CUSTOMER_EMAIL = "customer@example.com";

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private SaleDetailRepository saleDetailRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CashService cashService;
    @Mock
    private CashSessionService cashSessionService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private LoyaltyAccountRepository loyaltyAccountRepository;

    private SaleService saleService;
    private Product product;

    @BeforeEach
    void setUp() {
        saleService = new SaleService(
                saleRepository,
                saleDetailRepository,
                productRepository,
                userRepository,
                cashService,
                cashSessionService,
                notificationService,
                loyaltyAccountRepository);

        User seller = new User();
        seller.setEmail(SELLER_EMAIL);
        product = Product.builder()
                .id(1L)
                .name("Producto")
                .stock(100)
                .minStock(0)
                .build();

        when(cashSessionService.getCurrentOpenSessionForUser(SELLER_EMAIL))
                .thenReturn(new CashSession(0.0, SELLER_EMAIL));
        when(userRepository.findByEmailIgnoreCase(SELLER_EMAIL)).thenReturn(Optional.of(seller));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
    }

    @Test
    void acumulaUnPuntoPorCadaDiezPesosConRedondeoHaciaAbajo() {
        product.setPrice(new BigDecimal("29.99"));
        SaleRequest request = request("  CUSTOMER@EXAMPLE.COM ", null);
        when(loyaltyAccountRepository.findByCustomerEmailIgnoreCase(CUSTOMER_EMAIL))
                .thenReturn(Optional.empty());
        when(loyaltyAccountRepository.save(any(LoyaltyAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        stubSaleSave();

        Sale sale = saleService.registerSale(request, SELLER_EMAIL, true);

        assertThat(sale.getPointsEarned()).isEqualTo(2);
        assertThat(sale.getCustomerEmail()).isEqualTo(CUSTOMER_EMAIL);
        ArgumentCaptor<LoyaltyAccount> accountCaptor = ArgumentCaptor.forClass(LoyaltyAccount.class);
        verify(loyaltyAccountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue().getCustomerEmail()).isEqualTo(CUSTOMER_EMAIL);
        assertThat(accountCaptor.getValue().getPointsBalance()).isEqualTo(2);
    }

    @Test
    void noAcumulaPuntosSinEmailDelCliente() {
        product.setPrice(new BigDecimal("100.00"));
        stubSaleSave();

        Sale sale = saleService.registerSale(request(null, null), SELLER_EMAIL, true);

        assertThat(sale.getPointsEarned()).isZero();
        assertThat(sale.getCustomerEmail()).isNull();
        verifyNoInteractions(loyaltyAccountRepository);
    }

    @Test
    void noAcumulaPuntosSinFlagLoyalty() {
        product.setPrice(new BigDecimal("100.00"));
        stubSaleSave();

        Sale sale = saleService.registerSale(request(CUSTOMER_EMAIL, null), SELLER_EMAIL, false);

        assertThat(sale.getPointsEarned()).isZero();
        verifyNoInteractions(loyaltyAccountRepository);
    }

    @Test
    void canjeaUnPesoPorPuntoYAcumulaSobreElTotalFinal() {
        product.setPrice(new BigDecimal("100.00"));
        LoyaltyAccount account = account(30);
        SaleRequest request = request(CUSTOMER_EMAIL, 20L);
        request.setPaymentMethod("CASH");
        request.setCashReceived(80.0);
        when(loyaltyAccountRepository.findByCustomerEmailIgnoreCase(CUSTOMER_EMAIL))
                .thenReturn(Optional.of(account));
        when(loyaltyAccountRepository.save(account)).thenReturn(account);
        stubSaleSave();

        Sale sale = saleService.registerSale(request, SELLER_EMAIL, true);

        assertThat(sale.getLoyaltyDiscount()).isEqualTo(20.0);
        assertThat(sale.getTotal()).isEqualTo(80.0);
        assertThat(sale.getPointsRedeemed()).isEqualTo(20);
        assertThat(sale.getPointsEarned()).isEqualTo(8);
        assertThat(sale.getCashChange()).isZero();
        assertThat(account.getPointsBalance()).isEqualTo(18);
        verify(loyaltyAccountRepository).save(account);
    }

    @Test
    void rechazaUnCanjeMayorQueElSaldoDisponible() {
        product.setPrice(new BigDecimal("100.00"));
        LoyaltyAccount account = account(5);
        when(loyaltyAccountRepository.findByCustomerEmailIgnoreCase(CUSTOMER_EMAIL))
                .thenReturn(Optional.of(account));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> saleService.registerSale(request(CUSTOMER_EMAIL, 6L), SELLER_EMAIL, true))
                .withMessage("Saldo de puntos insuficiente");

        assertThat(account.getPointsBalance()).isEqualTo(5);
        verify(loyaltyAccountRepository, never()).save(any(LoyaltyAccount.class));
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    void rechazaUnDescuentoMayorQueElTotalDeLaVenta() {
        product.setPrice(new BigDecimal("50.00"));
        LoyaltyAccount account = account(100);
        when(loyaltyAccountRepository.findByCustomerEmailIgnoreCase(CUSTOMER_EMAIL))
                .thenReturn(Optional.of(account));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> saleService.registerSale(request(CUSTOMER_EMAIL, 51L), SELLER_EMAIL, true))
                .withMessage("El descuento no puede ser mayor al total de la venta");

        assertThat(account.getPointsBalance()).isEqualTo(100);
        verify(loyaltyAccountRepository, never()).save(any(LoyaltyAccount.class));
        verify(saleRepository, never()).save(any(Sale.class));
    }

    private SaleRequest request(String customerEmail, Long pointsToRedeem) {
        SaleItemRequest item = new SaleItemRequest();
        item.setProductId(1L);
        item.setQuantity(1);

        SaleRequest request = new SaleRequest();
        request.setItems(List.of(item));
        request.setCustomerEmail(customerEmail);
        request.setPointsToRedeem(pointsToRedeem);
        request.setPaymentMethod("CARD");
        return request;
    }

    private LoyaltyAccount account(long balance) {
        LoyaltyAccount account = new LoyaltyAccount();
        account.setCustomerEmail(CUSTOMER_EMAIL);
        account.earn(balance);
        return account;
    }

    private void stubSaleSave() {
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }
}
