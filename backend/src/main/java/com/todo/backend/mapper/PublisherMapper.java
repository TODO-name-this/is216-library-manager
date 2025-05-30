package com.todo.backend.mapper;

import com.todo.backend.dto.publisher.PublisherDto;
import com.todo.backend.dto.publisher.ResponsePublisherDto;
import com.todo.backend.entity.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PublisherMapper {
    Publisher toEntity(PublisherDto publisherDto);
    PublisherDto toDto(Publisher publisher);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(PublisherDto publisherDto, @MappingTarget Publisher publisher);

    ResponsePublisherDto toResponseDto(Publisher publisher);
    List<ResponsePublisherDto> toResponseDtoList(List<Publisher> publishers);
}
