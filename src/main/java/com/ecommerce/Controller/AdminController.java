package com.ecommerce.Controller;

import com.ecommerce.Entity.Category;
import com.ecommerce.Entity.OrderProduct;
import com.ecommerce.Entity.Product;
import com.ecommerce.Entity.User;
import com.ecommerce.Helpers.*;
import com.ecommerce.Repository.UserRepo;
import com.ecommerce.Services.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
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
import java.security.Principal;
import java.util.Objects;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final UserService userService;
    private final OrderProductService orderProductService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final Fetch fetch;


    @Autowired
    public AdminController(CategoryService categoryService, ProductService productService, UserService userService, OrderProductService orderProductService, EmailService emailService, PasswordEncoder passwordEncoder, UserRepo userRepo,Fetch fetch) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.userService = userService;
        this.orderProductService = orderProductService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.fetch = fetch;
    }

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "admin/admin_dashboard";
    }

    @GetMapping("/addProduct")
    public String addProductPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategory());
        return "admin/products";
    }


    @GetMapping("/addCategory")
    public String addCategoryPage(
            @RequestParam(value = "pageSize", defaultValue = AppConstant.PAGE_SIZE + "") int pageSize,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model) {
        // without pagination
        // List<Category> categories = categoryService.getAllCategory();

        // with pagination
        Page<Category> categories = categoryService.getAllCategoryWithPagination(pageNo, pageSize, sortBy, direction);
        model.addAttribute("pageCategories", categories);
        model.addAttribute("pageSize", AppConstant.PAGE_SIZE);
        return "admin/category";
    }

    @PostMapping(value = "/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) {

        if (categoryService.isCategoryExists(category.getName())) {
            session.setAttribute("message", Message.builder()
                    .content("Category Already Exists !")
                    .messageColorType(MessageType.red)
                    .build());
            return "redirect:/admin/category";
        }
        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        // save image name in a database and file in the local disk
        Category saveCategory = categoryService.saveCategory(category);
        if (!ObjectUtils.isEmpty(saveCategory)) {
            try {
                File saveFile = new ClassPathResource("static/img").getFile();
                assert file != null;
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                logger.info("File saved in path {}", path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                logger.error("{}", e.toString());
            }
            session.setAttribute("message", Message.builder()
                    .content("Category Added Successfully!")
                    .messageColorType(MessageType.green)
                    .build());
        } else {
            session.setAttribute("message", Message.builder().content("Something Wrong on Server !").messageColorType(MessageType.red).build());
        }
        return "redirect:/admin/addCategory";
    }

    @GetMapping("/deleteCategoryById/{id}")
    public String deleteCategoryById(@PathVariable int id, HttpSession session) {
        categoryService.deleteCategory(id);
        session.setAttribute("message", Message.builder()
                .content("Category Deleted Successfully!")
                .messageColorType(MessageType.green)
                .build());
        logger.info("Category Deleted Successfully!");
        return "redirect:/admin/addCategory";
    }

    @GetMapping("/loadUpdateCategoryPage/{id}")
    public String loadUpdateCategoryPage(@PathVariable int id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/update_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category newCategory, @RequestParam("file") MultipartFile file, HttpSession session) {
        logger.info("Id :{}", newCategory.getId());
        Category oldCategory = categoryService.getCategoryById(newCategory.getId());

        if (oldCategory == null) {
            session.setAttribute("message", Message.builder()
                    .content("Category not found!")
                    .messageColorType(MessageType.red)
                    .build());
            return "redirect:/admin/addCategory";
        }

        // Update the category properties
        oldCategory.setName(newCategory.getName());
        oldCategory.setIsActive(newCategory.getIsActive());
        oldCategory.setImageName(file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename());

        Category updatedCategory = categoryService.saveCategory(oldCategory);

        if (updatedCategory != null) {

            if (!file.isEmpty()) {
                try {
                    File saveDir = new ClassPathResource("static/img").getFile();

                    // Create the category_img subdirectory if it doesn't exist
                    File adminImgDir = new File(saveDir, "category_img");
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
                    return "redirect:/admin/addCategory";
                }
            }


            session.setAttribute("message", Message.builder()
                    .content("Category Updated Successfully!")
                    .messageColorType(MessageType.green)
                    .build());
        } else {
            session.setAttribute("message", Message.builder()
                    .content("Something Wrong on Server!")
                    .messageColorType(MessageType.red)
                    .build());
        }
        return "redirect:/admin/addCategory";
    }


    // Product CRUD Operation

    @GetMapping("/viewProducts")
    public String viewProductsPage(
            @RequestParam(value = "pageSize", defaultValue = AppConstant.PAGE_SIZE + "") int pageSize,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model) {


        // Without pagination
        // model.addAttribute("products", productService.getAllProduct());

        // with pagination
        Page<Product> allProductWithPagination = productService.getAllProductWithPagination(pageNo, pageSize, sortBy, direction);

        model.addAttribute("pageProducts", allProductWithPagination);
        model.addAttribute("pageSize", AppConstant.PAGE_SIZE);

        return "admin/view_products";
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file, HttpSession session) {

        if (Objects.equals(product.getCategory(), "")) {
            session.setAttribute("message", Message.builder()
                    .content("Please select a category!")
                    .messageColorType(MessageType.red)
                    .build());
            return "redirect:/admin/addProduct";
        }
        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        product.setImageName(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product saveProduct = productService.saveProduct(product);
        if (!ObjectUtils.isEmpty(saveProduct)) {
            if (!file.isEmpty()) {
                try {
                    File saveDir = new ClassPathResource("static/img").getFile();

                    // Create the admin_img subdirectory if it doesn't exist
                    File adminImgDir = new File(saveDir, "product_img");
                    if (!adminImgDir.exists()) {
                        adminImgDir.mkdirs(); // Creates all necessary parent directories
                        logger.info("Creating directory: {}", adminImgDir.getAbsolutePath());
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
                    return "redirect:/admin/addProduct";
                }
            }
            session.setAttribute("message", Message.builder()
                    .content("Product Added Successfully!")
                    .messageColorType(MessageType.green)
                    .build());
        } else {
            session.setAttribute("message", Message.builder().content("Something Wrong on Server !").messageColorType(MessageType.red).build());
        }
        return "redirect:/admin/addProduct";
    }

    @GetMapping("/deleteProductById/{id}")
    public String deleteProductById(@PathVariable int id, HttpSession session) {
        productService.deleteProduct(id);
        session.setAttribute("message", Message.builder()
                .content("Product Deleted Successfully!")
                .messageColorType(MessageType.green)
                .build());
        logger.info("Product Deleted Successfully!");
        return "redirect:/admin/viewProducts";
    }


    @GetMapping("/loadUpdateProductPage/{id}")
    public String loadUpdateProductPage(@PathVariable int id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategory());
        return "admin/update_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product newProduct, @RequestParam("file") MultipartFile file, HttpSession session) {
        logger.info("Id :{}", newProduct.getId());


        if (newProduct.getDiscount() > 100 || newProduct.getDiscount() < 0) {
            session.setAttribute("message", Message.builder()
                    .content("Discount must be between 0 and 100!")
                    .messageColorType(MessageType.red)
                    .build());
            return "redirect:/admin/loadUpdateProductPage/" + newProduct.getId();
        } else {
            Product updateProduct = productService.updatedProduct(newProduct, file);

            if (!ObjectUtils.isEmpty(updateProduct)) {
                session.setAttribute("message", Message.builder()
                        .content("Product Updated Successfully! Please check the discount price and discount!")
                        .messageColorType(MessageType.green)
                        .build());
            } else {
                session.setAttribute("message", Message.builder().content("Something Wrong on Server!").messageColorType(MessageType.red).build());
            }
        }
        return "redirect:/admin/loadUpdateProductPage/" + newProduct.getId();
    }


    //    view all users modules
    @GetMapping("/users")
    public String viewUsersPage(
            @RequestParam(value = "pageSize", defaultValue = AppConstant.PAGE_SIZE + "") int pageSize,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "sortBy", defaultValue = "email") String sortBy,
            @RequestParam(value = "direction", defaultValue = "desc") String direction,
            Model model) {


        // without pagination
        // model.addAttribute("users", userService.getAllUser(AppConstant.ROLE_USER));

        //with pagination
        Page<User> allUserAndRoleWithPagination = userService.getAllUserAndRoleWithPagination(AppConstant.ROLE_USER, pageNo, pageSize, sortBy, direction);
        model.addAttribute("users", allUserAndRoleWithPagination);
        model.addAttribute("pageSize", AppConstant.PAGE_SIZE);
        return "admin/view_users";
    }


    @GetMapping("/updateAccountStatus")
    public String updateAccountStatus(@RequestParam("id") Integer id, @RequestParam("status") Boolean status, HttpSession session) {

        Boolean b = userService.updateAccountStatus(id, status);
        if (b) {
            session.setAttribute("message", Message.builder()
                    .content("Account Status Updated Successfully!")
                    .messageColorType(MessageType.green)
                    .build());
        } else {
            session.setAttribute("message", Message.builder()
                    .content("Something Wrong on Server!")
                    .messageColorType(MessageType.red)
                    .build());
        }
        return "redirect:/admin/users";
    }


    // orders module of all customers
    @GetMapping("/orders")
    public String loadOrdersPage(
            @RequestParam(value = "pageSize", defaultValue = AppConstant.PAGE_SIZE + "") int pageSize,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "direction", defaultValue = "desc") String direction,
            Model model) {
        // without pagination
        // model.addAttribute("orders", orderProductService.getAllOrders());

        Page<OrderProduct> allOrdersWithPagination = orderProductService.getAllOrdersWithPagination(pageNo, pageSize, sortBy, direction);
        model.addAttribute("orders", allOrdersWithPagination);
        model.addAttribute("pageSize", AppConstant.PAGE_SIZE);
        return "admin/orders";
    }

    @PostMapping("/updateCustomerOrderStatus")
    public String updateCustomerOrderStatus(@RequestParam("id") Integer orderId, @RequestParam(value = "status", defaultValue = 1 + "") Integer status, HttpSession session) {

        ProductOrderStatus[] values = ProductOrderStatus.values();

        String statusOrder = null;
        for (ProductOrderStatus productOrderStatus : values) {
            if (productOrderStatus.getId().equals(status)) {
                statusOrder = productOrderStatus.getName();
            }
        }


        OrderProduct updatedOrderStatus = orderProductService.updateOrderStatus(orderId, statusOrder);

        emailService.sendMailForProductOrder(updatedOrderStatus, statusOrder);
        if (!ObjectUtils.isEmpty(updatedOrderStatus)) {
            session.setAttribute("message", Message.builder()
                    .content("Order Status Updated Successfully !")
                    .messageColorType(MessageType.green)
                    .build());
        } else {
            session.setAttribute("message", Message.builder()
                    .content("Something Wrong on Server !. Status not updated. Please try again later.")
                    .messageColorType(MessageType.red)
                    .build());
        }

        return "redirect:/admin/orders";
    }



    @GetMapping("/profile")
    public String loadAdminProfilePage() {
        return "admin/admin_profile";
    }




    private User getLoggedInUserDetails(Authentication authentication) {

        String name = fetch.LoggedInUserEmail(authentication);
        return userService.getUserByEmail(name);
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute User user, @RequestParam("file") MultipartFile file, HttpSession session) {

        String image = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();


        User updatedUserProfile = userService.updateUserProfile(user, image);
        if (!ObjectUtils.isEmpty(updatedUserProfile)) {
            if (!file.isEmpty()) {
                try {
                    File saveDir = new ClassPathResource("static/img").getFile();

                    // Create the admin_img subdirectory if it doesn't exist
                    File adminImgDir = new File(saveDir, "admin_img");
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
                    return "redirect:/admin/profile";
                }
            }
            session.setAttribute("message", Message.builder()
                    .content("Profile Updated Successfully!")
                    .messageColorType(MessageType.green)
                    .build());
        } else {
            session.setAttribute("message", Message.builder()
                    .content("Something Wrong on Server! Profile Not Updated")
                    .messageColorType(MessageType.red)
                    .build());
        }


        return "redirect:/admin/profile";
    }


    @PostMapping("/changePassword")
    public String changePassword(Authentication p, @RequestParam("currentPassword") String currentPassword, @RequestParam("newPassword") String newPassword, HttpSession session) {

        var loggedInUserDetails = getLoggedInUserDetails(p);

        if (passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword())) {
            loggedInUserDetails.setPassword(passwordEncoder.encode(newPassword));
            User updateUserPassword = userService.updateUserPassword(loggedInUserDetails);
            if (!ObjectUtils.isEmpty(updateUserPassword)) {
                session.setAttribute("message", Message.builder()
                        .content("Password Updated Successfully!")
                        .messageColorType(MessageType.green)
                        .build());
            } else {
                session.setAttribute("message", Message.builder()
                        .content("Something Wrong on Server! Password Not Updated")
                        .messageColorType(MessageType.red)
                        .build());
            }


        } else {
            session.setAttribute("message", Message.builder()
                    .content("Your Current Password is Incorrect! You can't change your password. Please try again with correct password.")
                    .messageColorType(MessageType.red)
                    .build());
        }

        return "redirect:/admin/profile";
    }

    @GetMapping("/show-Order-By-Status")
    public String showOrderByStatus(@RequestParam(value = "status", defaultValue = 0+"") Integer statusValue,
                                    @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                                    @RequestParam(value = "pageSize", defaultValue = AppConstant.PAGE_SIZE_FOR_PRODUCTS_AND_CATEGORY + "") int pageSize,
                                    @RequestParam(value = "sort", defaultValue = "id") String sort,
                                    @RequestParam(value = "direction", defaultValue = "desc") String direction,
                                    Model model){

        ProductOrderStatus[] values = ProductOrderStatus.values();

        String status = null ;



        for (ProductOrderStatus productOrderStatus : values){
            if (productOrderStatus.getId().equals(statusValue))
                status = productOrderStatus.getName();
        }
        if (statusValue == 0){
            status = null ;
        }

        Page<OrderProduct> orderByStatus = orderProductService.getOrderByStatus(status, pageNo, pageSize, sort, direction);
        model.addAttribute("orders",orderByStatus);
        model.addAttribute("pageSize",AppConstant.PAGE_SIZE);
        model.addAttribute("direction",direction);

        return "admin/orders";
    }

}
