package com.example.hulkstore.modules.categories.service.impl;

import com.example.hulkstore.modules.categories.dto.CategoryDTO;
import com.example.hulkstore.modules.categories.exception.CategoryAlreadyExistsException;
import com.example.hulkstore.modules.categories.model.Category;
import com.example.hulkstore.modules.categories.repository.CategoryRepository;
import com.example.hulkstore.modules.categories.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService implements ICategoryService {

    private final CategoryRepository repository;
    private final ModelMapper mapper;

    @Override
    public CategoryDTO create(CategoryDTO dto) {
        log.info("Creating category: {}", dto.getName());

//        validar para evitar nombre duplicados
        if (repository.existsByName(dto.getName())) {
            throw new CategoryAlreadyExistsException("Category with name '" + dto.getName() + "' already exists");
        }
//valida para eeviatr sla duplicacion de id
        if (dto.getId() != null && repository.existsById(dto.getId())) {
            throw new CategoryAlreadyExistsException("Category with id '" + dto.getId() + "' already exists");
        }

//        Generar slug automático si no viene
        if (dto.getSlug() == null || dto.getSlug().isEmpty()) {
            dto.setSlug(dto.getName().toLowerCase().replaceAll("\\s+", "-"));
        }
//        mapeaar y guardar
        Category entity = mapper.map(dto, Category.class);

//manejar su la categoria padres existe y si no existe lanzar una excepcion
        if (dto.getParentId() != null) {
            if (!repository.existsById(dto.getParentId())) {
                throw new RuntimeException("Parent category not found");
            }
        }

        Category saved = repository.save(entity);
        log.info("✅ Category created successfully with id: {}", saved.getId());

//        Category entity = mapper.map(dto, Category.class);
//        Category saved = repository.save(entity);
        return mapper.map(saved, CategoryDTO.class);
    }

//    esta funcion es para actualizar categoria, si no existe lanzar una excepcion
    @Override
    public CategoryDTO update(Long id, CategoryDTO dto) {
        log.info("Updating category with id: {}", id);
//        verifica que exista la categoria a actualizar
        if (!repository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
//        System.out.println("➡️ ID recibido: " + id);
//        System.out.println("➡️ DTO recibido: " + dto);

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
