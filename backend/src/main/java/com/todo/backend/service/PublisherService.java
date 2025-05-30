package com.todo.backend.service;

import com.todo.backend.dao.PublisherRepository;
import com.todo.backend.dto.publisher.PublisherDto;
import com.todo.backend.dto.publisher.ResponsePublisherDto;
import com.todo.backend.entity.Publisher;
import com.todo.backend.mapper.PublisherMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PublisherService {
    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    public PublisherService(PublisherRepository publisherRepository, PublisherMapper publisherMapper) {
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
    }

    public List<ResponsePublisherDto> getAllPublishers() {
        List<Publisher> publishers = publisherRepository.findAll();
        return publishers.stream()
                .map(publisherMapper::toResponseDto)
                .toList();
    }

    public ResponsePublisherDto getPublisher(String id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publisher with this ID does not exist"));

        return publisherMapper.toResponseDto(publisher);
    }

    public ResponsePublisherDto createPublisher(PublisherDto publisherDto) {
        Publisher publisher = publisherMapper.toEntity(publisherDto);

        publisherRepository.save(publisher);

        return publisherMapper.toResponseDto(publisher);
    }

    public ResponsePublisherDto updatePublisher(String id, PublisherDto publisherDto) {
        Publisher existingPublisher = publisherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publisher with this ID does not exist"));

        publisherMapper.updateEntityFromDto(publisherDto, existingPublisher);

        publisherRepository.save(existingPublisher);

        return publisherMapper.toResponseDto(existingPublisher);
    }

    public void deletePublisher(String id) {
        Publisher existingPublisher = publisherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publisher with this ID does not exist"));

        if (existingPublisher.getBookTitles() != null && !existingPublisher.getBookTitles().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete publisher with associated book titles");
        }

        publisherRepository.delete(existingPublisher);
    }
}
