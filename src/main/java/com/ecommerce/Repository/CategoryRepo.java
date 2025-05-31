package com.ecommerce.Repository;

import com.ecommerce.Entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryRepo extends JpaRepository<Category,Integer> {


    Optional<Category> findById(int id);

    boolean existsByName(String name);

    List<Category> findByIsActiveTrue();

    Page<Category> findAll(Pageable pageable);


    Page<Category> findByIsActiveTrue(Pageable pageable);


}
