package com.example.hulkstore.modules.categories.repository;

import com.example.hulkstore.modules.categories.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
