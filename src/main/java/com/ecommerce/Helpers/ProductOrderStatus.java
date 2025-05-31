package com.ecommerce.Helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ProductOrderStatus {

    IN_PROGRESS(1,"In Progress")
    , ORDER_RECEIVED(2,"Order Received")
    , PRODUCT_PACKED(3,"Product Packed")
    , OUT_FOR_DELIVERY(4,"Out For Delivery")
    , DELIVERED(5,"Delivered")
    , CANCELLED(6,"Cancelled")
    ,SUCCESS(7,"Success");

    private Integer id ;
    private String name ;


}