package com.todo.backend.service;

import com.todo.backend.dao.PublisherRepository;
import com.todo.backend.entity.Publisher;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public Publisher createPublisher(Publisher publisher) {
        if (publisherRepository.existsById(publisher.getId())) {
            throw new IllegalArgumentException("Publisher with this ID already exists");
        }

        return publisherRepository.save(publisher);
    }

    public Publisher updatePublisher(Publisher publisher) {
        if (!publisherRepository.existsById(publisher.getId())) {
            throw new IllegalArgumentException("Publisher with this ID does not exist");
        }

        return publisherRepository.save(publisher);
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
