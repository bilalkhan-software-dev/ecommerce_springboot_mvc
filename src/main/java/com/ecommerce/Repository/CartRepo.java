package com.ecommerce.Repository;

import com.ecommerce.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CartRepo extends JpaRepository<Cart,Integer> {


    public Cart findByProductIdAndUserId(Integer pid , Integer uid);

   int  countByUserId(Integer uid);

   List<Cart> findByUserId(Integer userId) ;



}
