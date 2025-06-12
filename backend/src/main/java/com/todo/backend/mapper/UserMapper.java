package com.todo.backend.mapper;

import com.todo.backend.dto.user.*;
import com.todo.backend.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toEntity(CreateUserDto createUserDto);
    CreateUserDto toCreateDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(PartialUpdateUserDto partialUpdateUserDto, @MappingTarget User user);

    // New mapping methods for different update flows
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cccd", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromSelfUpdateDto(SelfUpdateUserDto selfUpdateUserDto, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromLibrarianUpdateDto(LibrarianUpdateUserDto librarianUpdateUserDto, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromAdminUpdateDto(AdminUpdateUserDto adminUpdateUserDto, @MappingTarget User user);

    ResponseUserDto toResponseDto(User user);
    List<ResponseUserDto> toResponseDtoList(List<User> users);
}
