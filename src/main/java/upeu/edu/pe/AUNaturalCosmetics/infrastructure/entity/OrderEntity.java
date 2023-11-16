/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package upeu.edu.pe.AUNaturalCosmetics.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author alejandromacedop
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity {
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDateTime dateCreated;
    private String status;
    
    @ManyToOne
    private UserEntity user;
    
    @Transient
    private List<OrderProductEntity> orderProducts;  
    
    
    /*agregar productos a arreglo*/
     public void addOrdersProduct(List<OrderProductEntity> orderProducts){
        this.setOrderProducts(orderProducts);
    }
    
    public BigDecimal getTotalOrderPrice(){
        return getOrderProducts().stream().map(
                p->p.getTotalPrice()
        ).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
