package com.todo.backend.mapper;

import com.todo.backend.dto.category.CategoryDto;
import com.todo.backend.dto.category.ResponseCategoryDto;
import com.todo.backend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryDto categoryDto);
    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    void updateCategoryFromDto(CategoryDto categoryDto, @MappingTarget Category category);

    ResponseCategoryDto toResponseDto(Category category);
    List<ResponseCategoryDto> toResponseDtoList(List<Category> categories);
}
