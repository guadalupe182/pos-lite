package com.Gdev.pos_lite.email;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.Gdev.pos_lite.sale.Sale;
import com.Gdev.pos_lite.sale.SaleDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSaleReceipt(Sale sale, String customerEmail, String customerName, String paymentMethod) {
        if (sale == null || customerEmail == null || customerEmail.isBlank()) {
            System.err.println("No se puede enviar email: datos inválidos");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(customerEmail);
            helper.setSubject("Comprobante de compra - POS-lite #" + sale.getId());
            helper.setFrom("no-reply@pos-lite.com");
            String htmlContent = buildHtmlEmail(sale, customerName, paymentMethod);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("✅ Email enviado a: " + customerEmail);
        } catch (MessagingException e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildHtmlEmail(Sale sale, String customerName, String paymentMethod) {
        StringBuilder itemsHtml = new StringBuilder();
        List<SaleDetail> details = sale.getDetails();
        if (details != null && !details.isEmpty()) {
            for (SaleDetail detail : details) {
                String productName = detail.getProduct() != null ? detail.getProduct().getName() : "Producto";
                Integer quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;
                Double unitPrice = detail.getUnitPrice() != null ? detail.getUnitPrice() : 0.0;
                Double subtotal = detail.getSubtotal() != null ? detail.getSubtotal() : 0.0;
                itemsHtml.append(String.format("""
                    <tr>
                        <td style='border:1px solid #ddd; padding:8px;'>%s</td>
                        <td style='border:1px solid #ddd; padding:8px; text-align:center;'>%d</td>
                        <td style='border:1px solid #ddd; padding:8px; text-align:right;'>$%.2f</td>
                        <td style='border:1px solid #ddd; padding:8px; text-align:right;'>$%.2f</td>
                    </tr>
                    """, productName, quantity, unitPrice, subtotal));
            }
        } else {
            itemsHtml.append("""
                    <tr>
                        <td colspan='4' style='border:1px solid #ddd; padding:8px; text-align:center;'>
                            No hay productos disponibles
                        </td>
                    </tr>
                """);
        }
        Double total = sale.getTotal() != null ? sale.getTotal() : 0.0;
        String paymentMethodText = paymentMethod != null ? paymentMethod : "No especificado";
        String customerDisplayName = customerName != null && !customerName.isBlank() ? customerName : "Cliente";
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head><meta charset='UTF-8'><title>Comprobante de compra</title></head>
            <body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>
                <div style='max-width: 600px; margin: 0 auto; background-color: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>
                    <div style='background-color: #4CAF50; padding: 20px; text-align: center; color: white;'>
                        <h2 style='margin: 0;'>¡Gracias por tu compra!</h2>
                    </div>
                    <div style='padding: 20px;'>
                        <p>Hola <strong>%s</strong>,</p>
                        <p>Tu venta <strong>#%d</strong> ha sido registrada exitosamente.</p>
                        <table style='width: 100%%; border-collapse: collapse; margin: 20px 0;'>
                            <thead>
                                <tr style='background-color: #f2f2f2;'>
                                    <th style='border:1px solid #ddd; padding:8px;'>Producto</th>
                                    <th style='border:1px solid #ddd; padding:8px;'>Cantidad</th>
                                    <th style='border:1px solid #ddd; padding:8px;'>Precio</th>
                                    <th style='border:1px solid #ddd; padding:8px;'>Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>%s</tbody>
                            <tfoot>
                                <tr style='background-color: #f9f9f9;'>
                                    <td colspan='3' style='border:1px solid #ddd; padding:8px; text-align:right;'><strong>TOTAL</strong></td>
                                    <td style='border:1px solid #ddd; padding:8px; text-align:right;'><strong>$%.2f</strong></td>
                                </tr>
                            </tfoot>
                        </table>
                        <p><strong>Método de pago:</strong> %s</p>
                        <p style='color: #666; font-size: 12px;'>Este es un comprobante electrónico de tu compra.</p>
                    </div>
                    <div style='background-color: #f4f4f4; padding: 15px; text-align: center; font-size: 11px; color: #888;'>
                        POS-lite - Sistema de punto de venta
                    </div>
                </div>
            </body>
            </html>
            """, customerDisplayName, sale.getId(), itemsHtml.toString(), total, paymentMethodText);
    }
}
