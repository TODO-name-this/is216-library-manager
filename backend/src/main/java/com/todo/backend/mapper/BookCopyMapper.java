package com.todo.backend.mapper;

import com.todo.backend.dto.bookcopy.BookCopyDto;
import com.todo.backend.dto.bookcopy.ResponseBookCopyDto;
import com.todo.backend.dto.bookcopy.UpdateBookCopyDto;
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
    
    // New method for updating BookCopy with UpdateBookCopyDto
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookTitleId", ignore = true)
    @Mapping(target = "bookTitle", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    void updateEntityFromUpdateDto(UpdateBookCopyDto updateBookCopyDto, @MappingTarget BookCopy bookCopy);

    @Mapping(target = "bookTitle", ignore = true)
    @Mapping(target = "bookPhotoUrl", ignore = true)
    @Mapping(target = "bookPrice", ignore = true)
    @Mapping(target = "borrowerCccd", ignore = true)
    @Mapping(target = "borrowerName", ignore = true)
    @Mapping(target = "borrowerId", ignore = true)
    ResponseBookCopyDto toResponseDto(BookCopy bookCopy);
    List<ResponseBookCopyDto> toResponseDtoList(List<BookCopy> bookCopies);
}
