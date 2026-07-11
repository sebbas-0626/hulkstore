package com.example.hulkstore.modules.categories.service;

import com.example.hulkstore.modules.categories.dto.CategoryDTO;
import com.example.hulkstore.modules.categories.model.Category;
import com.example.hulkstore.modules.categories.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository repository;
    private final ModelMapper mapper;

    @Override
    public CategoryDTO create(CategoryDTO dto) {
        Category entity = mapper.map(dto, Category.class);
        Category saved = repository.save(entity);
        return mapper.map(saved, CategoryDTO.class);
    }

    @Override
    public CategoryDTO update(Long id, CategoryDTO dto) {
        System.out.println("➡️ ID recibido: " + id);
        System.out.println("➡️ DTO recibido: " + dto);
        Category entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        mapper.map(dto, entity);

        Category updated = repository.save(entity);

        return mapper.map(updated, CategoryDTO.class);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
        repository.deleteById(id);
    }

    @Override
    public CategoryDTO findById(Long id) {
        Category entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return mapper.map(entity, CategoryDTO.class);
    }

    @Override
    public List<CategoryDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(c -> mapper.map(c, CategoryDTO.class))
                .collect(Collectors.toList());
    }
}
