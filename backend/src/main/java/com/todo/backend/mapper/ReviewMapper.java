package com.todo.backend.mapper;

import com.todo.backend.dto.review.ResponseReviewDto;
import com.todo.backend.dto.review.ReviewDto;
import com.todo.backend.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    Review toEntity(ReviewDto dto);
    ReviewDto toDto(Review review);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ReviewDto dto, @MappingTarget Review review);

    ResponseReviewDto toResponseDto(Review review);
    List<ResponseReviewDto> toResponseDtoList(List<Review> reviews);
}
