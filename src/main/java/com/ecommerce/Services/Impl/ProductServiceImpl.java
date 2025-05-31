package com.ecommerce.Services.Impl;

import com.ecommerce.Entity.Product;
import com.ecommerce.Repository.ProductRepo;
import com.ecommerce.Services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;

    @Autowired
    public ProductServiceImpl(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }


    @Override
    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    @Override
    public Product getProductById(int id) {
        return productRepo.findById(id).orElse(null);
    }

    @Override
    public List<Product> getAllActiveProducts(String category) {

        List<Product> products = null ;

        if (ObjectUtils.isEmpty(category)){
           products =  productRepo.findByIsActiveTrue();
        }else {
            products = productRepo.findByCategory(category) ;
        }
        return products;
    }

    @Override
    public void deleteProduct(int id) {
        productRepo.deleteById(id);
    }


    @Override
    public Product updatedProduct(Product product, MultipartFile file) {


        Product oldProduct = productRepo.findById(product.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(oldProduct)) {
            String imageName = file.isEmpty() ? oldProduct.getImageName() : file.getOriginalFilename();
            oldProduct.setTitle(product.getTitle());
            oldProduct.setDescription(product.getDescription());
            oldProduct.setPrice(product.getPrice());
            oldProduct.setImageName(imageName);
            oldProduct.setCategory(product.getCategory());
            oldProduct.setStocks(product.getStocks());
            oldProduct.setIsActive(product.getIsActive());
            oldProduct.setDiscount(product.getDiscount());


            // set discount and discount price
            // 5=100*(5/100) => 100-5 =95
            Double discount = product.getPrice() * (product.getDiscount() / 100.0);
            Double discountPrice = product.getPrice() - discount;

            oldProduct.setDiscountPrice(discountPrice);
            Product updateProduct = productRepo.save(oldProduct);

            if (!ObjectUtils.isEmpty(updateProduct)) {
                if (!file.isEmpty()) {
                    try {
                        File saveFile = new ClassPathResource("static/img").getFile();
                        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + file.getOriginalFilename());
                        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        log.error("Error while saving file: {}", e.getMessage());
                    }
                }
                return updateProduct;
            }
        }
        return null;
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepo.findAll();
    }


// pagination


    @Override
    public Page<Product> getAllProductWithPagination(int pageNo, int pageSize, String sortBy, String direction) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending() ;
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return productRepo.findAll(pageable);
    }

    @Override
    public Page<Product> getAllActiveProductWithPagination(int pageNo, int pageSize, String sortBy, String direction,String category) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending() ;
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        if (category == null || category.isEmpty() || category.equalsIgnoreCase("All")) {
            // Return all active products when no category is specified or "All" is selected
            return productRepo.findByIsActiveTrue(pageable);
        } else {
            // Return products by category
            return productRepo.findByCategory(category, pageable);
        }
    }

    @Override
    public Page<Product> searchProducts(String keyword, String category, int pageNo, int pageSize, String sort, String direction) {

        Sort sort1 = direction.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending() ;
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort1);
        return productRepo.searchProducts(keyword,category,pageable);
    }

}
