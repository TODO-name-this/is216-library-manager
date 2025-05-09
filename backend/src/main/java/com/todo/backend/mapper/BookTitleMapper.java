package com.todo.backend.mapper;

import com.todo.backend.dto.booktitle.BookTitleDto;
import com.todo.backend.dto.booktitle.ResponseBookTitleDto;
import com.todo.backend.entity.BookTitle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookTitleMapper {
    BookTitle toEntity(BookTitleDto bookTitleDto);
    BookTitleDto toBookTitleDto(BookTitle bookTitle);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(BookTitleDto bookTitleDto, @MappingTarget BookTitle bookTitle);

    ResponseBookTitleDto toResponseBookTitleDto(BookTitle bookTitle);
}
