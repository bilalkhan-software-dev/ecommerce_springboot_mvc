package com.ecommerce.Controller;


import com.ecommerce.Entity.Product;
import com.ecommerce.Entity.User;
import com.ecommerce.Helpers.AppConstant;
import com.ecommerce.Helpers.Message;
import com.ecommerce.Helpers.MessageType;
import com.ecommerce.Repository.UserRepo;
import com.ecommerce.Services.CategoryService;
import com.ecommerce.Services.EmailService;
import com.ecommerce.Services.ProductService;
import com.ecommerce.Services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;

@Controller
public class PageController {

    private final UserRepo userRepo;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductService productService ;
    private final CategoryService categoryService ;
    private final UserService userService ;


    @Autowired
    public PageController(ProductService productService, CategoryService categoryService, UserService userService, UserRepo userRepo) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.userRepo = userRepo;
    }


    @GetMapping("/home")
    public String homePage(@RequestParam(value = "category", defaultValue = "",required = false) String category,
            Model model
    ){
        model.addAttribute("categories", categoryService.getAllActiveCategory()
                .stream()
                .sorted((c1,c2)
                        ->
                        c2.getId()
                                .compareTo(c1.getId()))
                .limit(6).toList());

        // logic for latest products means the product added in last it will be the latest product
        model.addAttribute("products",productService.getAllActiveProducts(category)
                .stream()
                .sorted((p1,p2)
                        ->
                            p2.getId()
                                    .compareTo(p1.getId()))
                .limit(8).toList());

        return "home";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }


    @GetMapping("/products")
    public String productPage(@RequestParam (value = "category", defaultValue = "",required = false)String category,
                              @RequestParam(value = "pageNo",defaultValue = "0")int pageNo,
                              @RequestParam(value = "pageSize",defaultValue = AppConstant.PAGE_SIZE_FOR_PRODUCTS_AND_CATEGORY+"")int pageSize,
                              @RequestParam(value = "sort",defaultValue = "id")String sort,
                              @RequestParam(value = "direction",defaultValue = "desc")String direction,
                              Model model
                             ) {


        // without pagination
        model.addAttribute("categories", categoryService.getAllActiveCategory()) ;
//        model.addAttribute("products", productService.getAllActiveProducts(category)) ;

        // with pagination
//        model.addAttribute("categories", categoryService.getAllActiveCategoryWithPagination(pageNo,pageSize,sort,direction)) ;
        model.addAttribute("products",productService.getAllActiveProductWithPagination(pageNo,pageSize,sort,direction,category));
        model.addAttribute("pageSize", AppConstant.PAGE_SIZE_FOR_PRODUCTS_AND_CATEGORY);
        model.addAttribute("paramValue", category);
        model.addAttribute("direction",direction);
        return "products";
    }



    @GetMapping("/categories")
    public String categoryPage(@RequestParam (value = "category", defaultValue = "",required = false) String category,
                               @RequestParam(value = "pageNo",defaultValue = "0")int pageNo,
                               @RequestParam(value = "pageSize",defaultValue = AppConstant.PAGE_SIZE_FOR_PRODUCTS_AND_CATEGORY+"")int pageSize,
                               @RequestParam(value = "sort",defaultValue = "id")String sort,
                               @RequestParam(value = "direction",defaultValue = "asc")String direction,
                               Model model) {
        // without pagination
//        model.addAttribute("categories", categoryService.getAllActiveCategory()) ;
//        model.addAttribute("products", productService.getAllActiveProducts(category)) ;

        // with pagination
        model.addAttribute("categories", categoryService.getAllActiveCategoryWithPagination(pageNo,pageSize,sort,direction)) ;
        model.addAttribute("products",productService.getAllActiveProductWithPagination(pageNo,pageSize,sort,direction,category));
        model.addAttribute("pageSize", AppConstant.PAGE_SIZE_FOR_PRODUCTS_AND_CATEGORY);
        model.addAttribute("paramValue", category);

        return "products";
    }
    @GetMapping("/productDetails/{id}")
    public String viewProductDetailsPage(@PathVariable int id, Model model){
        Product productById = productService.getProductById(id);
        model.addAttribute("product", productById);
        return "view_productById";
    }


    @PostMapping("/saveUser")
    public String saveUser(@Valid @ModelAttribute User user, BindingResult bindingResult, @RequestParam("file")MultipartFile file, HttpSession session) {

        if (bindingResult.hasErrors()){

            session.setAttribute("message",Message
                    .builder()
                    .content("Fill the form and Solve the following error that occurs to use our service")
                    .messageColorType(MessageType.red)
                    .build());
            return "redirect:/register";
        }


        if (userService.isUserExists(user.getEmail())){
            session.setAttribute("message", Message.builder()
                    .content("Email is already registered. Try another email")
                    .messageColorType(MessageType.red).build());
            return "redirect:/register";
        }



        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setImageName(imageName);
        User saveUser = userService.saveUser(user);
        if (!ObjectUtils.isEmpty(saveUser)) {
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
                    return "redirect:/register";
                }
            }

                session.setAttribute("message", Message.builder()
                        .content("User Registered Successfully! Email with Verification Link sent to your email address.")
                        .messageColorType(MessageType.green).build());
        }else {
            session.setAttribute("message", Message.builder()
                    .content("Something Wrong on Server!")
                    .messageColorType(MessageType.red).build());
            logger.error("Something Wrong on Server!");
        }
        return "redirect:/register";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmailPage(@RequestParam("token") String token,HttpSession session){


        User byEmailVerificationCode = userRepo.findByEmailVerificationCode(token).orElse(null);
        if (byEmailVerificationCode != null){
            if (byEmailVerificationCode.getEmailVerificationCode().equals(token)){
                byEmailVerificationCode.setIsEnable(true);
                byEmailVerificationCode.setEmailVerificationCode(null);
                userRepo.save(byEmailVerificationCode);
                session.setAttribute("message", Message.builder()
                        .content("Email Verified Successfully!")
                        .messageColorType(MessageType.green)
                        .build());
                logger.info("Email Verified Successfully!");
            }
        }
        else {
            session.setAttribute("message", Message.builder()
                    .content("Email Verification Link is Invalid or Expired!")
                    .messageColorType(MessageType.red)
                    .build());
            logger.info("Email Verification Link is Invalid!");
        }
        return "redirect:/login";
    }


    @GetMapping("/search-products")
    public String searchProducts(
        @RequestParam(value = "search",required = false) String keyword,
        @RequestParam(value = "category", defaultValue = "", required = false) String category,
        @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
        @RequestParam(value = "pageSize", defaultValue = AppConstant.PAGE_SIZE_FOR_PRODUCTS_AND_CATEGORY + "") int pageSize,
        @RequestParam(value = "sort", defaultValue = "id") String sort,
        @RequestParam(value = "direction", defaultValue = "asc") String direction,
        Model model) {

    model.addAttribute("categories", categoryService.getAllActiveCategoryWithPagination(0, 100, "id", direction));
    
    // Get search results with pagination
    Page<Product> searchResults = productService.searchProducts(keyword, category, pageNo, pageSize, sort, direction);
    model.addAttribute("products", searchResults);
    model.addAttribute("pageSize", AppConstant.PAGE_SIZE_FOR_PRODUCTS_AND_CATEGORY);
    model.addAttribute("keyword", keyword);
    model.addAttribute("paramValue", category);
    model.addAttribute("direction",direction);
    model.addAttribute("searchValue",keyword);

    return "user/searchProducts";
}



}