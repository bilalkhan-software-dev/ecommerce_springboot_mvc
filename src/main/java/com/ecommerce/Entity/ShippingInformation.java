package com.ecommerce.Entity;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ShippingInformation {

    private String firstName ;
    private String lastName ;
    private String mobileNumber ;
    private String email ;
    private String address ;
    private String city ;
    private String state ;
    private String pinCode ;
    private String paymentMethod ;

}
