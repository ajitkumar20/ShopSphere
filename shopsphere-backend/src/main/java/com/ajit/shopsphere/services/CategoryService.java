package com.ajit.shopsphere.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajit.shopsphere.dtos.CategoryDto;
import com.ajit.shopsphere.dtos.CategoryTypeDto;
import com.ajit.shopsphere.entities.Category;
import com.ajit.shopsphere.entities.CategoryType;
import com.ajit.shopsphere.exceptions.ResourceNotFoundException;
import com.ajit.shopsphere.repositories.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category getCategory(UUID categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        return category.orElse(null);
    }

    public Category createCategory(CategoryDto categoryDto) {
        Category category = mapToEntity(categoryDto);
        return categoryRepository.save(category);
    }

    private Category mapToEntity(CategoryDto categoryDto) {
        Category category = Category.builder()
                .code(categoryDto.getCode())
                .name(categoryDto.getName())
                .description(categoryDto.getDescription())
                .build();

        if (categoryDto.getCategoryTypeList() != null) {
            List<CategoryType> categoryTypes = mapToCategoryType(categoryDto.getCategoryTypeList(), category);
            category.setCategoryTypes(categoryTypes);
        }

        return category;
    }

    private List<CategoryType> mapToCategoryType(List<CategoryTypeDto> categoryTypeList, Category category) {
        return categoryTypeList.stream().map(categoryTypeDto -> {
            CategoryType categoryType = new CategoryType();
            categoryType.setCode(categoryTypeDto.getCode());
            categoryType.setName(categoryTypeDto.getName());
            categoryType.setDescription(categoryTypeDto.getDescription());
            categoryType.setCategory(category);
            return categoryType;
        }).collect(Collectors.toList());
    }

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(CategoryDto categoryDto, UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with Id: " + categoryId));

        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
        if (categoryDto.getCode() != null) {
            category.setCode(categoryDto.getCode());
        }
        if (categoryDto.getDescription() != null) {
            category.setDescription(categoryDto.getDescription());
        }

        List<CategoryType> existing = category.getCategoryTypes();
        List<CategoryType> list = new ArrayList<>();

        if (categoryDto.getCategoryTypeList() != null) {
            categoryDto.getCategoryTypeList().forEach(categoryTypeDto -> {
                if (null != categoryTypeDto.getId()) {
                    Optional<CategoryType> categoryType = existing.stream()
                            .filter(t -> t.getId().equals(categoryTypeDto.getId())).findFirst();
                    CategoryType categoryType1 = categoryType.get();
                    categoryType1.setCode(categoryTypeDto.getCode());
                    categoryType1.setName(categoryTypeDto.getName());
                    categoryType1.setDescription(categoryTypeDto.getDescription());
                    list.add(categoryType1);
                } else {
                    CategoryType categoryType = new CategoryType();
                    categoryType.setCode(categoryTypeDto.getCode());
                    categoryType.setName(categoryTypeDto.getName());
                    categoryType.setDescription(categoryTypeDto.getDescription());
                    categoryType.setCategory(category);
                    list.add(categoryType);
                }
            });
        }
        category.setCategoryTypes(list);
        return categoryRepository.save(category);
    }

    public void deleteCategory(UUID categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with Id: " + categoryId));
        categoryRepository.deleteById(categoryId);
    }
}
