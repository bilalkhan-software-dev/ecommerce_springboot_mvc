package com.ecommerce.Controller;

import com.ecommerce.Entity.User;
import com.ecommerce.Helpers.Fetch;
import com.ecommerce.Services.UserService;
import com.ecommerce.Services.CartService;
import com.ecommerce.Services.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class AccessibleToAllController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final Fetch fetch;

    Logger logger = LoggerFactory.getLogger(AccessibleToAllController.class);

    @Autowired
    public AccessibleToAllController(UserService userService, CategoryService categoryService, CartService cartService,Fetch fetch) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.cartService = cartService;
        this.fetch = fetch;
    }

    @ModelAttribute
    public void getUserDetailsWhoLoggedIn(Authentication authentication, Model model) {



        if (authentication != null){

            String username = fetch.LoggedInUserEmail(authentication);
            User userDetailsByEmail = userService.getUserByEmail(username);

            if (userDetailsByEmail != null) {
                logger.info("Logged in User: {}", username);
                model.addAttribute("user", userDetailsByEmail);
                model.addAttribute("cartCount", cartService.getCartCount(userDetailsByEmail.getId()));
            } else {
                logger.warn("No user found for email: {}", username);
                model.addAttribute("cartCount", 0);
            }
        }
        model.addAttribute("categories", categoryService.getAllActiveCategory());
    }
}