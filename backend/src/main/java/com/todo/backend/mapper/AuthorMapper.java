package com.todo.backend.mapper;

import com.todo.backend.dto.author.AuthorDto;
import com.todo.backend.dto.author.ResponseAuthorDto;
import com.todo.backend.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    Author toEntity(AuthorDto authorDto);
    AuthorDto toAuthorDto(Author author);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(AuthorDto authorDto, @MappingTarget Author author);

    ResponseAuthorDto toResponseDto(Author author);
}
