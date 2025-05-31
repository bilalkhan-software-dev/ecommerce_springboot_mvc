package com.ecommerce.Services.Impl;

import com.ecommerce.Entity.Category;
import com.ecommerce.Helpers.NotFoundException;
import com.ecommerce.Repository.CategoryRepo;
import com.ecommerce.Services.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class CategoryServiceImpl implements CategoryService {

    @Override
    public List<Category> getAllActiveCategory() {
        return categoryRepo.findByIsActiveTrue();
    }

    private final CategoryRepo categoryRepo;

    public CategoryServiceImpl(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryRepo.save(category);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepo.findAll();
    }


    @Override
    public Category getCategoryById(int id) {
        return categoryRepo.findById(id).orElse(null);
    }

    @Override
    public void deleteCategory(int id) {
        categoryRepo.deleteById(id);
    }


    @Override
    public Boolean isCategoryExists(String name) {
        return categoryRepo.existsByName(name);
    }


    // pagination Category

    @Override
    public Page<Category> getAllCategoryWithPagination(int pageNo, int pageSize, String sortBy, String direction) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending() ;

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return categoryRepo.findAll(pageable);
    }

    @Override
    public Page<Category> getAllActiveCategoryWithPagination(int pageNo, int pageSize, String sortBy, String direction) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending() ;
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return categoryRepo.findByIsActiveTrue(pageable);
    }
}
