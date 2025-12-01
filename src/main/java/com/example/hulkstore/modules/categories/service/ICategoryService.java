package com.example.hulkstore.modules.categories.service;

import com.example.hulkstore.modules.categories.dto.CategoryDTO;
import java.util.List;

public interface ICategoryService {

    CategoryDTO create(CategoryDTO dto);

    CategoryDTO update(Long id, CategoryDTO dto);

    void delete(Long id);

    CategoryDTO findById(Long id);

    List<CategoryDTO> findAll();
}

