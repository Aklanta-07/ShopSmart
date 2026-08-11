package com.shopsmart.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.shopsmart.dto.request.CategoryRequest;
import com.shopsmart.dto.response.CategoryResponse;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse getById(Long id);
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
    Page<CategoryResponse> getAll(int page, int size, String sortBy, String sortDir);
    Page<CategoryResponse> search(String name, int page, int size, String sortBy, String sortDir);
    List<CategoryResponse> getActiveCategories();
}