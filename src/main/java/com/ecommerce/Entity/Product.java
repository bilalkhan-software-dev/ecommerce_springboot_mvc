package com.ecommerce.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.processing.Exclude;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;

    @Column(length = 1000)
    private String title ;

    private String category ;

    @Column(length = 1000)
    private String description ;

    private Double price ;

    @Column(length = 1000)
    private String imageName ;

    private int stocks ;

    private int discount ;

    private Double discountPrice ;



    private Boolean isActive ;

}
