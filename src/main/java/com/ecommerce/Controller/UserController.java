package com.ecommerce.Controller;


import com.ecommerce.Entity.Cart;
import com.ecommerce.Entity.OrderProduct;
import com.ecommerce.Entity.ShippingInformation;
import com.ecommerce.Entity.User;
import com.ecommerce.Helpers.Fetch;
import com.ecommerce.Helpers.Message;
import com.ecommerce.Helpers.MessageType;
import com.ecommerce.Helpers.ProductOrderStatus;
import com.ecommerce.Repository.OrderProductRepo;
import com.ecommerce.Services.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderProductService orderProductService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final Fetch fetch;


    @Autowired
    public UserController(UserService userService,CategoryService categoryService,CartService cartService,ProductService productService,OrderProductService orderProductService,EmailService emailService, PasswordEncoder passwordEncoder,Fetch fetch){
        this.userService = userService;
        this.categoryService = categoryService;
        this.cartService = cartService;
        this.productService = productService;
        this.orderProductService = orderProductService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.fetch = fetch;
    }


    @GetMapping("/profile")
    public String userProfilePage(){
        return "user/user_profile";
    }



    @GetMapping("/addCart")
    public String addCartPage(@RequestParam("pid")Integer productId, @RequestParam("uid")Integer userId, HttpSession session ){

        Cart addedCart = cartService.addCart(productId, userId);
        if (!ObjectUtils.isEmpty(addedCart)) {
            session.setAttribute("message", Message.builder()
                    .content("Product Added to Cart Successfully!")
                    .messageColorType(com.ecommerce.Helpers.MessageType.green)
                    .build());
        }else {
            session.setAttribute("message", Message.builder()
                    .content("Something Wrong on Server! Product Not Added to Cart")
                    .messageColorType(com.ecommerce.Helpers.MessageType.red)
                    .build());
        }

        return "redirect:/productDetails/"+productId;
    }




    @GetMapping("/viewCart")
    public String viewCart(Authentication p, Model model, HttpSession session){

        User loggedInUserDetails = getLoggedInUserDetails(p);
        List<Cart> cartByUser = cartService.getCartByUser(loggedInUserDetails.getId());
        model.addAttribute("carts",cartByUser);

        // Calculate total price only if cart is not empty
        if (!cartByUser.isEmpty()) {
            model.addAttribute("totalAmountPrice", cartByUser.getLast().getTotalOrderPrice());
        } else {
            model.addAttribute("totalAmountPrice", 0.0); // or any default value
            session.setAttribute("message", Message.builder()
                    .content("Your cart is empty! . Please add products to cart before checkout.")
                    .messageColorType(MessageType.red)
                    .build());
        }
        return "user/view_cart";
    }


    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam("sy")String value, @RequestParam("cid")Integer cartId){

        cartService.updateCartQuantityAndDeleteIfQuantityIsZero(value,cartId);
        return "redirect:/user/viewCart";
    }

    private User getLoggedInUserDetails (Authentication p){

        String name = fetch.LoggedInUserEmail(p);
        return userService.getUserByEmail(name);

    }

    // order module starts here
    @GetMapping("/placeOrder")
    public String placeOrder(Authentication authentication,Model model){

        User loggedInUserDetails = getLoggedInUserDetails(authentication);

        List<Cart> cartByUser = cartService.getCartByUser(loggedInUserDetails.getId());

        double subTotal = cartByUser.getLast().getTotalOrderPrice();


        int delieveryCharges = 250 ;
        int tax = 150;
        double totalPrice = subTotal + delieveryCharges + tax ;

        model.addAttribute("subTotal",cartByUser.getLast().getTotalOrderPrice());
        model.addAttribute("totalPrice",totalPrice);
        return "user/orderProduct";
    }

    @GetMapping("/orderSuccess")
    public String loadOrderSuccessPage(){
        return "user/order_success";
    }

    @PostMapping("/saveOrder")
    public String saveOrder(@ModelAttribute ShippingInformation shippingInformation, Authentication p, HttpSession session){
        User loggedInUserDetails = getLoggedInUserDetails(p);

        boolean b = orderProductService.saveOrder(loggedInUserDetails.getId(), shippingInformation);


        if (b){
            return "redirect:/user/orderSuccess";
        }else {
            session.setAttribute("message", Message.builder()
                    .content("Something Wrong on Server! Order Not Placed")
                    .messageColorType(MessageType.red)
                    .build());
            return "redirect:/user/placeOrder";
        }
    }

    @GetMapping("/viewOrders")
    public String orders(Authentication p, Model model){

        User loggedInUserDetails = getLoggedInUserDetails(p);
        List<OrderProduct> orderDetails = orderProductService.getOrderDetailsByUser(loggedInUserDetails.getId());
        model.addAttribute("orders",orderDetails);

        return "user/view_orders" ;
    }

    @GetMapping("/updateOrderStatus")
    public String updateOrder(@RequestParam("oid") Integer orderId, @RequestParam("status") Integer status,HttpSession session){

        // the URL fetches find enum values
        ProductOrderStatus[] values = ProductOrderStatus.values();
        String Orderstatus = null ;

        for (ProductOrderStatus productOrderStatus : values){
            if (productOrderStatus.getId().equals(status)){
                Orderstatus = productOrderStatus.getName();
            }
        }
        OrderProduct orderProduct = orderProductService.updateOrderStatus(orderId, Orderstatus);
        emailService.sendMailForProductOrder(orderProduct,Orderstatus);
        if (!ObjectUtils.isEmpty(orderProduct)){
            session.setAttribute("message", Message.builder()
                    .content("Order Cancellled Successfully!")
                    .messageColorType(MessageType.green)
                    .build());
        }else {
            session.setAttribute("message", Message.builder()
                    .content("Something Wrong on Server! Order Status Not Updated")
                    .messageColorType(MessageType.red)
                    .build());
        }
        return "redirect:/user/viewOrders" ;
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute User user, @RequestParam("file")MultipartFile file, HttpSession session){

        String image = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();


        User updatedUserProfile = userService.updateUserProfile(user, image);
        if (!ObjectUtils.isEmpty(updatedUserProfile)){
            if (!file.isEmpty()) {
                try {
                    File saveDir = new ClassPathResource("static/img").getFile();

                    // Create the user_img subdirectory if it doesn't exist
                    File adminImgDir = new File(saveDir, "user_img");
                    if (!adminImgDir.exists()) {
                        adminImgDir.mkdirs(); // Creates all necessary parent directories
                        logger.info("Created directory: {}", adminImgDir.getAbsolutePath());
                    }

                    Path path = Paths.get(adminImgDir.getAbsolutePath() + File.separator + file.getOriginalFilename());
                    logger.info("File location: {}", path);

                    // saving ...
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    logger.error("Error saving file: {}", e.toString());
                    session.setAttribute("message", Message.builder()
                            .content("Error saving image file!")
                            .messageColorType(MessageType.red)
                            .build());
                    return "redirect:/user/profile";
                }
            }
            session.setAttribute("message", Message.builder()
                    .content("Profile Updated Successfully!")
                    .messageColorType(MessageType.green)
                    .build());
        }else {
            session.setAttribute("message", Message.builder()
                    .content("Something Wrong on Server! Profile Not Updated")
                    .messageColorType(MessageType.red)
                    .build());
        }


        return "redirect:/user/profile";
    }


    @PostMapping("/changePassword")
    public String changePassword(Authentication p, @RequestParam("currentPassword") String currentPassword, @RequestParam("newPassword") String newPassword, HttpSession session){

        User loggedInUserDetails = getLoggedInUserDetails(p);


        if (passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword())){


            loggedInUserDetails.setPassword(passwordEncoder.encode(newPassword));
            User updateUserPassword = userService.updateUserPassword(loggedInUserDetails);
            if (!ObjectUtils.isEmpty(updateUserPassword)){
                session.setAttribute("message", Message.builder()
                        .content("Password Updated Successfully!")
                        .messageColorType(MessageType.green)
                        .build());
            }else {
                session.setAttribute("message", Message.builder()
                        .content("Something Wrong on Server! Password Not Updated")
                        .messageColorType(MessageType.red)
                        .build());
            }
        }else {
            session.setAttribute("message", Message.builder()
                    .content("Your Current Password is Incorrect! You can't change your password. Please try again with correct password.")
                    .messageColorType(MessageType.red)
                    .build());
        }
        return "redirect:/user/profile";
    }

}
