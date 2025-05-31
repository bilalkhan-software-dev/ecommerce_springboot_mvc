package com.ecommerce.Repository;


import com.ecommerce.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {

    Optional<Product> findById(int id);
    List<Product> findByIsActiveTrue();
    List<Product> findByCategory(String category) ;
    Page<Product> findAll(Pageable pageable);

    Page<Product> findByCategory(String category,Pageable pageable);
    Page<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String keyword, String category,Pageable pageable);
    Page<Product> findByIsActiveTrue(Pageable pageable);


    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:category IS NULL OR :category = '' OR LOWER(p.category) = LOWER(:category)) " +
            "AND p.isActive = true")
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 @Param("category") String category,
                                 Pageable pageable);


    List<Product> findByTitleContainingAndIsActiveTrue(String query);
}
