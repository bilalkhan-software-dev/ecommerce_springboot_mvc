package com.ecommerce.Services.Impl;

import com.ecommerce.Entity.Cart;
import com.ecommerce.Entity.Product;
import com.ecommerce.Entity.User;
import com.ecommerce.Repository.CartRepo;
import com.ecommerce.Repository.ProductRepo;
import com.ecommerce.Repository.UserRepo;
import com.ecommerce.Services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepo cartRepo ;
    private final ProductRepo productRepo ;
    private final UserRepo userRepo ;


    @Autowired
    public CartServiceImpl(CartRepo cartRepo, ProductRepo productRepo, UserRepo userRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Cart addCart(Integer productId, Integer userId) {

        Product product = productRepo.findById(productId).get();
        User user = userRepo.findById(userId).get();

        Cart cartStatus = cartRepo.findByProductIdAndUserId(productId,userId);

        Cart cart = null;

        // logic if user not added cart before
        if (ObjectUtils.isEmpty(cartStatus)){
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountPrice());
        }else {
         // logic if user already cart added before
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * 1 * cart.getProduct().getDiscountPrice());
        }
        return cartRepo.save(cart);
    }

    @Override
    public List<Cart> getCartByUser(Integer userId) {

        List<Cart> carts = cartRepo.findByUserId(userId);
        double totalOrderPrice = 0.0;
        List<Cart> updatedCart = new ArrayList<>();
        for (Cart cart :carts ) {
            double totalPrice = cart.getQuantity() * cart.getProduct().getDiscountPrice() ;
            cart.setTotalPrice(totalPrice);
            totalOrderPrice += totalPrice ;
            cart.setTotalOrderPrice(totalOrderPrice);
            updatedCart.add(cart);
        }
        return updatedCart;
    }

    @Override
    public int getCartCount(Integer userId) {
        return cartRepo.countByUserId(userId);
    }

    @Override
    public void updateCartQuantityAndDeleteIfQuantityIsZero(String quantity, Integer cartId) {

        Cart cart = cartRepo.findById(cartId).get();

        int updateQuantity ;

        if (quantity.equalsIgnoreCase("decrease")){
            updateQuantity =  cart.getQuantity()-1 ;

            if (updateQuantity <= 0){
                cartRepo.delete(cart);
            }else {
                cart.setQuantity(updateQuantity);
                cartRepo.save(cart);
            }
        }else {
            updateQuantity = cart.getQuantity() + 1 ;
            cart.setQuantity(updateQuantity);
            cartRepo.save(cart);
        }
    }
}
