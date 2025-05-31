package com.ecommerce.Entity;

import com.ecommerce.Helpers.ProductOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;

    private String orderId ;

    private LocalDate orderDate ;

    @ManyToOne
    private User user ;

    @ManyToOne
    private Product product ;

    private Integer quantity ;

    private Double price ;



    private String  status ;

    private String paymentMethod ;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress ;


}
