package com.ecommerce.Services;

import com.ecommerce.Entity.OrderProduct;
import com.ecommerce.Entity.ShippingInformation;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderProductService {

    boolean saveOrder(Integer userId, ShippingInformation shippingInformation);

    List<OrderProduct> getOrderDetailsByUser(Integer userId) ;
    OrderProduct updateOrderStatus(Integer orderId,String status) ;

    List<OrderProduct> getAllOrders() ;

    // all orders with pagination
    Page<OrderProduct> getAllOrdersWithPagination(int pageNo, int pageSize, String sortBy, String direction) ;

    Page<OrderProduct> getOrderByStatus(String status,int pageNo, int pageSize, String sortBy, String direction);
}
