package com.todo.backend.mapper;

import com.todo.backend.dto.user.ResponseUserDto;
import com.todo.backend.dto.user.UserDto;
import com.todo.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto userDto);
    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UserDto userDto, @MappingTarget User user);

    ResponseUserDto toResponseDto(User user);
}
