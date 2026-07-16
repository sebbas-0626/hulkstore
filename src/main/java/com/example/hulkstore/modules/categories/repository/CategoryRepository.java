package com.example.hulkstore.modules.categories.repository;

import com.example.hulkstore.modules.categories.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    //    validaciones de negocio
    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    // Para actualizaciones (evitar duplicados excluyendo el actual)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.name = :name AND c.id <> :id")
    boolean existsByNameAndIdNot(String name, Long id);

    @Query
("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.slug = :slug AND c.id <> :id")
    boolean existsBySlugAndIdNot(String slug, Long id);

    List<Category> findByIsActiveTrue();
    List<Category> findByParentId(Long parentId);

    // 🔍 Verificar si tiene productos (para evitar eliminar categorías con productos)
//    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.category.id = :categoryId")
//    boolean hasProducts(Long categoryId);
}

