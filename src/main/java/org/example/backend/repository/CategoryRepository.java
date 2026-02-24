package org.example.backend.repository;

import org.example.backend.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull(); // Root categories

    List<Category> findByParent_CategoryId(Long parentId);

    Category findBySlug(String slug);
}
