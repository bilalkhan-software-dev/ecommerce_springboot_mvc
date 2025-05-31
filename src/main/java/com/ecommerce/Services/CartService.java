package com.ecommerce.Services;

import com.ecommerce.Entity.Cart;
import com.ecommerce.Entity.Product;
import com.ecommerce.Entity.User;

import java.util.List;

public interface CartService {

    Cart addCart(Integer productId, Integer userId);
    List<Cart> getCartByUser(Integer userId) ;

    int getCartCount(Integer userId);


    void updateCartQuantityAndDeleteIfQuantityIsZero(String quantity,Integer cartId);



}
