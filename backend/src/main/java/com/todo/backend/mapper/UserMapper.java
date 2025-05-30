package com.todo.backend.mapper;

import com.todo.backend.dto.user.PartialUpdateUserDto;
import com.todo.backend.dto.user.ResponseUserDto;
import com.todo.backend.dto.user.CreateUserDto;
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

    ResponseUserDto toResponseDto(User user);
    List<ResponseUserDto> toResponseDtoList(List<User> users);
}
