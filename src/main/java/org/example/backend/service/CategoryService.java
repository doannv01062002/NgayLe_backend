package org.example.backend.service;

import org.example.backend.dto.CategoryDTO;
import org.example.backend.model.entity.Category;
import org.example.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategories() {
        // Fetch all top-level categories and let Hibernate fetch children lazily or
        // eagerly depending on config
        // Actually, fetching only roots is better for hierarchical display
        List<Category> roots = categoryRepository.findByParentIsNull();
        return roots.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public CategoryDTO getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug);
        if (category == null)
            throw new RuntimeException("Category not found");
        return convertToDTO(category);
    }

    // Recursive converter
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getCategoryId());
        }
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setLevel(category.getLevel());
        dto.setIconUrl(category.getIconUrl());
        dto.setIsHolidaySpecific(category.getIsHolidaySpecific());
        dto.setDisplayOrder(category.getDisplayOrder());

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            dto.setChildren(category.getChildren().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
