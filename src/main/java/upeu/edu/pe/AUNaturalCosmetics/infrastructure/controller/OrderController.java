/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package upeu.edu.pe.AUNaturalCosmetics.infrastructure.controller;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upeu.edu.pe.AUNaturalCosmetics.app.domain.ItemCart;
import upeu.edu.pe.AUNaturalCosmetics.app.service.CartService;
import upeu.edu.pe.AUNaturalCosmetics.app.service.OrderProductService;
import upeu.edu.pe.AUNaturalCosmetics.app.service.OrderService;
import upeu.edu.pe.AUNaturalCosmetics.app.service.ProductService;
import upeu.edu.pe.AUNaturalCosmetics.app.service.StockService;
import upeu.edu.pe.AUNaturalCosmetics.app.service.UserService;
import upeu.edu.pe.AUNaturalCosmetics.app.service.ValidateStock;
import upeu.edu.pe.AUNaturalCosmetics.infrastructure.entity.OrderEntity;
import upeu.edu.pe.AUNaturalCosmetics.infrastructure.entity.OrderProductEntity;
import upeu.edu.pe.AUNaturalCosmetics.infrastructure.entity.StockEntity;
import upeu.edu.pe.AUNaturalCosmetics.infrastructure.entity.UserEntity;

/**
 *
 * @author alejandromacedop
 */
@Controller
@RequestMapping("/user/order")
public class OrderController {

    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final OrderProductService orderProductService;
    private final Integer entradas = 0;
    private final StockService stockService;
    private final ValidateStock validateStock;
    private final JavaMailSender javaMailSender;

    private final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(CartService cartService, UserService userService, OrderService orderService, ProductService productService, OrderProductService orderProductService, StockService stockService, ValidateStock validateStock, JavaMailSender javaMailSender) {
        this.cartService = cartService;
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
        this.orderProductService = orderProductService;
        this.stockService = stockService;
        this.validateStock = validateStock;
        this.javaMailSender = javaMailSender;
    }

    @GetMapping("/sumary-order")
    public String showSumaryOrder(Model model, HttpSession httpSession) {
        UserEntity user = userService.findById(Integer.parseInt(httpSession.getAttribute("iduser").toString()));
        model.addAttribute("cart", cartService.getItemCarts());
        model.addAttribute("total", cartService.getTotalCart());
        model.addAttribute("user", user);
        model.addAttribute("id", httpSession.getAttribute("iduser").toString());
        model.addAttribute("nombre", httpSession.getAttribute("name").toString());

        return "user/sumaryorder";

    }

    @GetMapping("/create-order")
    public String createOrder(RedirectAttributes attributes, HttpSession httpSession) {
        UserEntity user = userService.findById(Integer.parseInt(httpSession.getAttribute("iduser").toString()));
        OrderEntity order = new OrderEntity();
        order.setDateCreated(LocalDateTime.now());
        order.setStatus("Proceso");
        order.setUser(user);
        log.info("order", order);
        //guardar Order
        order = orderService.createOrder(order);

        List<OrderProductEntity> orderProduct = new ArrayList<>();
        for (ItemCart itemCart : cartService.getItemCarts()) {
            orderProduct.add(new OrderProductEntity(productService.getProductById(itemCart.getIdProduct()),
                    itemCart.getQuantity(),
                    order));
        }

        orderProduct.forEach(
                op -> {
                    orderProductService.create(op);
                    StockEntity stock = new StockEntity();
                    stock.setDescripcion("salida");
                    stock.setEntradas(entradas);
                    stock.setProductEntity(op.getProductEntity());
                    stock.setSalidas(op.getQuantity());
                    stockService.saveStock(validateStock.calculateBalance(stock));
                }
        );

        // Envía los detalles de la compra por correo electrónico
        enviarDetallesCompraPorCorreo(user.getEmail(), orderProduct);

        // Limpia el carrito después de la compra
        cartService.removeAllItemCart();
        attributes.addFlashAttribute("id", httpSession.getAttribute("iduser").toString());
        attributes.addFlashAttribute("nombre", httpSession.getAttribute("name").toString());
        return "redirect:/home";
    }
    private void enviarDetallesCompraPorCorreo(String email, List<OrderProductEntity> orderProducts) {
        String subject = "Detalles de tu compra";
        String body = construirCuerpoCorreo(orderProducts);

        sendEmail(email, subject, body);
    }
    private String construirCuerpoCorreo(List<OrderProductEntity> orderProducts) {
    // Construir el cuerpo del correo con una tabla HTML y estilos
    StringBuilder bodyBuilder = new StringBuilder();
    bodyBuilder.append("<p style='font-size: 16px;'><strong>Gracias por tu compra. A continuación se detallan los productos adquiridos:</strong></p>");
    bodyBuilder.append("<table style='border-collapse: collapse; width: 100%;' border='1'>");
    bodyBuilder.append("<tr style='background-color: #f2f2f2;'><th style='padding: 10px;'>Producto</th><th style='padding: 10px;'>Cantidad</th><th style='padding: 10px;'>Precio Unitario</th><th style='padding: 10px;'>Subtotal</th></tr>");

    BigDecimal total = BigDecimal.ZERO;  // Inicializa el total en cero

    for (OrderProductEntity op : orderProducts) {
        bodyBuilder.append("<tr>");
        bodyBuilder.append("<td style='padding: 10px;'>").append(op.getProductEntity().getName()).append("</td>");
        bodyBuilder.append("<td style='padding: 10px;'>").append(op.getQuantity()).append("</td>");
        bodyBuilder.append("<td style='padding: 10px;'>").append(op.getProductEntity().getPrice()).append("</td>");
        BigDecimal subtotal = op.getProductEntity().getPrice().multiply(BigDecimal.valueOf(op.getQuantity()));
        bodyBuilder.append("<td style='padding: 10px;'>").append(subtotal).append("</td>");
        bodyBuilder.append("</tr>");

        // Actualiza el total acumulado
        total = total.add(subtotal);
    }

    // Agrega una fila para mostrar el total
    bodyBuilder.append("<tr style='background-color: #f2f2f2;'>");
    bodyBuilder.append("<td colspan='3' style='padding: 10px; text-align: right;'><strong>Total</strong></td>");
    bodyBuilder.append("<td style='padding: 10px;'>").append(total).append("</td>");
    bodyBuilder.append("</tr>");

    bodyBuilder.append("</table>");

    return bodyBuilder.toString();
}

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar el error de envío de correo electrónico según tus necesidades
        }
    }
}
