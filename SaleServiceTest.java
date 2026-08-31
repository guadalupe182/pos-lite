package com.Gdev.pos_lite.sale;

import com.Gdev.pos_lite.cash.CashSessionService;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleDetailRepository saleDetailRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CashSessionService cashSessionService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SaleService saleService;

    @BeforeEach
    public void setUp() {
        // Initialize mocks and dependencies
    }

    @Test
    public void testRegisterSale_Success() {
        // Arrange
        String userEmail = "user@example.com";
        SaleRequest request = new SaleRequest();
        request.setItems(Arrays.asList(new SaleItemRequest(1L, 2)));
        when(userRepository.findByEmailIgnoreCase(userEmail)).thenReturn(Optional.of(new User()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(cashSessionService.isOpen()).thenReturn(true);

        // Act
        Sale sale = saleService.registerSale(request, userEmail);

        // Assert
        assertNotNull(sale);
        assertEquals(2, sale.getDetails().size());
        assertEquals(2, sale.getDetails().get(0).getQuantity());
        assertEquals(2, sale.getDetails().get(1).getQuantity());
    }

    @Test
    public void testRegisterSale_NoActiveSession() {
        // Arrange
        String userEmail = "user@example.com";
        SaleRequest request = new SaleRequest();
        request.setItems(Arrays.asList(new SaleItemRequest(1L, 2)));
        when(userRepository.findByEmailIgnoreCase(userEmail)).thenReturn(Optional.of(new User()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(cashSessionService.isOpen()).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> saleService.registerSale(request, userEmail));
    }

    @Test
    public void testRegisterSale_InsufficientStock() {
        // Arrange
        String userEmail = "user@example.com";
        SaleRequest request = new SaleRequest();
        request.setItems(Arrays.asList(new SaleItemRequest(1L, 100)));
        when(userRepository.findByEmailIgnoreCase(userEmail)).thenReturn(Optional.of(new User()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(cashSessionService.isOpen()).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> saleService.registerSale(request, userEmail));
    }
}
