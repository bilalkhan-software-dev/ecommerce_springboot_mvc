package com.ecommerce.Repository;

import com.ecommerce.Entity.OrderProduct;
import org.hibernate.query.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepo extends JpaRepository<OrderProduct,Integer> {


    List<OrderProduct> findByUserId(Integer userId);
    Page<OrderProduct> findAll(Pageable pageable);

    Page<OrderProduct> findByStatus(String status, Pageable pageable);
}
