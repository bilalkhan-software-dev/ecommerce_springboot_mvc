package com.ecommerce.Services;

import com.ecommerce.Entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {


    Category saveCategory(Category category);
    List<Category> getAllCategory();
    Category getCategoryById(int id);
    void deleteCategory(int id);
//    Category updateCategory(Category category);

    Boolean isCategoryExists(String name);
    List<Category> getAllActiveCategory() ;


    // pagination Category
    Page<Category> getAllCategoryWithPagination(int pageNo, int pageSize, String sortBy, String direction) ;
    Page<Category> getAllActiveCategoryWithPagination(int pageNo, int pageSize, String sortBy, String direction);

}

