package com.ecommerce.Services.Impl;

import com.ecommerce.Entity.Cart;
import com.ecommerce.Entity.OrderAddress;
import com.ecommerce.Entity.OrderProduct;
import com.ecommerce.Entity.ShippingInformation;
import com.ecommerce.Helpers.ProductOrderStatus;
import com.ecommerce.Repository.CartRepo;
import com.ecommerce.Repository.OrderProductRepo;
import com.ecommerce.Services.EmailService;
import com.ecommerce.Services.OrderProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.*;

@Service
public class OrderProductServiceImpl implements OrderProductService {

    private final OrderProductRepo orderProductRepo ;
    private final CartRepo cartRepo ;
    private final EmailService emailService ;

    @Autowired
    public OrderProductServiceImpl(OrderProductRepo orderProductRepo, CartRepo cartRepo,EmailService emailService) {
        this.orderProductRepo = orderProductRepo;
        this.cartRepo = cartRepo;
        this.emailService = emailService;
    }

    @Override
    public boolean saveOrder(Integer userId, ShippingInformation shippingInformation) {

        List<Cart> carts = cartRepo.findByUserId(userId);

        if (!carts.isEmpty()){
            for (Cart cart : carts) {
                OrderProduct productOrder = new OrderProduct();
                productOrder.setOrderId(UUID.randomUUID().toString());
                productOrder.setOrderDate(LocalDate.now());

                productOrder.setUser(cart.getUser());
                productOrder.setProduct(cart.getProduct());
                productOrder.setQuantity(cart.getQuantity());
                productOrder.setPrice(cart.getProduct().getDiscountPrice());

                productOrder.setStatus(ProductOrderStatus.IN_PROGRESS.getName());

                productOrder.setPaymentMethod(shippingInformation.getPaymentMethod());

                OrderAddress address = getOrderAddress(shippingInformation);

                productOrder.setOrderAddress(address);


                orderProductRepo.save(productOrder);
                emailService.sendMailForProductOrder(productOrder, ProductOrderStatus.SUCCESS.getName());
            }
            return true ;
        }
        return false ;
    }

    private static OrderAddress getOrderAddress(ShippingInformation shippingInformation) {
        OrderAddress address = new OrderAddress();
        address.setAddress(shippingInformation.getAddress());
        address.setCity(shippingInformation.getCity());
        address.setEmail(shippingInformation.getEmail());
        address.setFirstName(shippingInformation.getFirstName());
        address.setLastName(shippingInformation.getLastName());
        address.setPinCode(shippingInformation.getPinCode());
        address.setState(shippingInformation.getState());
        address.setMobileNumber(shippingInformation.getMobileNumber());
        return address;
    }

    @Override
    public List<OrderProduct> getOrderDetailsByUser(Integer userId) {
        return orderProductRepo.findByUserId(userId);
    }

    @Override
    public OrderProduct updateOrderStatus(Integer orderId, String status) {

        Optional<OrderProduct> id = orderProductRepo.findById(orderId);
        if (id.isPresent()){
    OrderProduct orderProduct = id.get();
            orderProduct.setStatus(status);
            return orderProductRepo.save(orderProduct);
        }
        return null;
    }

    @Override
    public List<OrderProduct> getAllOrders() {
        return orderProductRepo.findAll();
    }

    @Override
    public Page<OrderProduct> getAllOrdersWithPagination(int pageNo, int pageSize, String sortBy, String direction) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending() ;
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return orderProductRepo.findAll(pageable);
    }

    @Override
    public Page<OrderProduct> getOrderByStatus(String status,int pageNo, int pageSize, String sortBy, String direction) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        if (!ObjectUtils.isEmpty(status)){
            return orderProductRepo.findByStatus(status,pageable);
        }
        return orderProductRepo.findAll(pageable);
    }
}
