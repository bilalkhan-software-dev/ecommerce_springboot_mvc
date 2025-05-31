package com.ecommerce.Helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class getVerificationLink {

    @Value("${server.baseUrl}")
    private String baseUrl;


    public String forEmailVerify(String token){
        return this.baseUrl+"/verifyEmail?token="+token;
    }
}
