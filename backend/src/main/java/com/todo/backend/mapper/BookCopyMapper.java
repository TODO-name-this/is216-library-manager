package com.todo.backend.mapper;

import com.todo.backend.dto.bookcopy.BookCopyDto;
import com.todo.backend.dto.bookcopy.ResponseBookCopyDto;
import com.todo.backend.entity.BookCopy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {
    BookCopy toEntity(BookCopyDto bookCopyDto);
    BookCopyDto toDto(BookCopy bookCopy);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(BookCopyDto bookCopyDto, @MappingTarget BookCopy bookCopy);

    @Mapping(target = "bookCopyIds", ignore = true)
    ResponseBookCopyDto toResponseDto(BookCopy bookCopy);
    List<ResponseBookCopyDto> toResponseDtoList(List<BookCopy> bookCopies);
}
