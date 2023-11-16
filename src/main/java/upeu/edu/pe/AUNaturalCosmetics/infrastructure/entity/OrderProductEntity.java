/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package upeu.edu.pe.AUNaturalCosmetics.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author alejandromacedop
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "ordersproducts")
public class OrderProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity orderEntity;

    public OrderProductEntity(ProductEntity productEntity, Integer quantity, OrderEntity orderEntity) {
        this.productEntity = productEntity;
        this.quantity = quantity;
        this.orderEntity = orderEntity;
    }

    public BigDecimal getTotalPrice() {
        return this.productEntity.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
