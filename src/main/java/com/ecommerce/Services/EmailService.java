package com.ecommerce.Services;

import com.ecommerce.Entity.OrderProduct;

public interface EmailService {

    boolean sendEmail(String to, String subject, String content);

    boolean sendMailForProductOrder(OrderProduct orderProduct,String statusValue);

}
