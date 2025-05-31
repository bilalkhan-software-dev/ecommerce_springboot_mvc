package com.ecommerce.Services;

import com.ecommerce.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {


    Product saveProduct(Product product);
    Product getProductById(int id);
    void deleteProduct(int id);
    List<Product> getAllProduct();
    Product updatedProduct(Product product, MultipartFile file);
    List<Product> getAllActiveProducts(String category) ;

    // pagination
    Page<Product> getAllProductWithPagination(int pageNo, int pageSize,String sortBy, String direction) ;

    Page<Product> getAllActiveProductWithPagination(int pageNo, int pageSize, String sortBy, String direction,String category);


    Page<Product> searchProducts(String keyword, String category, int pageNo, int pageSize, String sort, String direction);

}
