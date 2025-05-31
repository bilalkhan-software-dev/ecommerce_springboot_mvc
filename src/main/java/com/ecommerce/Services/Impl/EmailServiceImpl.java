package com.ecommerce.Services.Impl;

import com.ecommerce.Entity.OrderProduct;
import com.ecommerce.Helpers.ProductOrderStatus;
import com.ecommerce.Services.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender javaMailSender;


    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    @Value("${spring.mail.properties.mail.domain-name}")
    private String domainName ;

    @Override
    public boolean sendEmail(String to, String subject, String htmlContent) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom(this.domainName, "E-Commerce");
            mimeMessageHelper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            return true ;
        }catch (Exception e){
            e.printStackTrace();
            return false ;
        }
    }

    @Override
    public boolean sendMailForProductOrder(OrderProduct orderProduct,String statusValue) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();


//        ProductOrderStatus[] values = ProductOrderStatus.values();
//        String setStatus = null;
//
//        for (ProductOrderStatus productOrderStatus : values) {
//            if (productOrderStatus.getId().equals(statusValue)) {
//                    setStatus = productOrderStatus.getName();
//            }
//        }

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(orderProduct.getOrderAddress().getEmail());
            mimeMessageHelper.setSubject("Product Order");
            mimeMessageHelper.setFrom(this.domainName, "E-Commerce");
            mimeMessageHelper.setText(templateForProductOrder(orderProduct, statusValue), true);
            javaMailSender.send(mimeMessage);
            return true ;
        }catch (Exception e){
            e.printStackTrace();
            return false ;
        }
    }
    
    
    public String templateForProductOrder(OrderProduct orderProduct,String status){
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        .container { max-width: 600px; margin: auto; font-family: Arial, sans-serif; }
                        .header { background: #4a90e2; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; }
                        .order-details { margin: 20px 0; border: 1px solid #ddd; padding: 15px; }
                        .footer { background: #f5f5f5; padding: 15px; text-align: center; }
                        .status { color: #4a90e2; font-weight: bold; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Order Confirmation</h1>
                        </div>
                        <div class="content">
                            <p>Dear %s,</p>
                            <p>Thank you for your order. Here are your order details:</p>
                
                            <div class="order-details">
                                <p><strong>Order ID:</strong> %s</p>
                                <p><strong>Order Date:</strong> %s</p>
                                <p><strong>Product:</strong> %s</p>
                                <p><strong>Category:</strong> %s</p>
                                <p><strong>Quantity:</strong> %d</p>
                                <p><strong>Total Price:</strong> $%f</p>
                                <p><strong>Your Order Status :</strong> <span class="status">%s</span></p>
                                <p><strong>Payment Method:</strong> %s</p>
                
                                <h3>Shipping Address:</h3>
                                <p>Address: %s</p>
                                <p>State : %s</p>
                                <p>City : %s</p>
                                <p>PinCode : %s</p>
                                <p>Phone: %s</p>
                            </div>
                
                            <p>If you have any questions, please don't hesitate to contact us.</p>
                        </div>
                        <div class="footer">
                            <p>© 2025 BK_QA Shopping Center. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                orderProduct.getOrderAddress().getFirstName() + " " + orderProduct.getOrderAddress().getLastName(),
                orderProduct.getOrderId(),
                orderProduct.getOrderDate(),
                orderProduct.getProduct().getTitle(),
                orderProduct.getProduct().getCategory(),
                orderProduct.getQuantity(),
                orderProduct.getPrice(),
                status,
                orderProduct.getPaymentMethod(),
                orderProduct.getOrderAddress().getAddress(),
                orderProduct.getOrderAddress().getCity(),
                orderProduct.getOrderAddress().getState(),
                orderProduct.getOrderAddress().getPinCode(),
                orderProduct.getOrderAddress().getMobileNumber()
        );
    }
}
