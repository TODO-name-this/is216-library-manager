package com.todo.backend.service;

import com.todo.backend.dao.PublisherRepository;
import com.todo.backend.entity.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public Publisher createPublisher(Publisher publisher) {
        if (publisherRepository.existsById(publisher.getId())) {
            throw new IllegalArgumentException("Publisher with ID already exists: " + publisher.getId());
        }
        return publisherRepository.save(publisher);
    }

    public Publisher updatePublisher(String id, Publisher updatedPublisher) {
        Publisher existingPublisher = publisherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publisher with ID does not exist: " + id));

        if (updatedPublisher.getName() != null) {
            existingPublisher.setName(updatedPublisher.getName());
        }

        if (updatedPublisher.getAddress() != null) {
            existingPublisher.setAddress(updatedPublisher.getAddress());
        }

        if (updatedPublisher.getEmail() != null) {
            existingPublisher.setEmail(updatedPublisher.getEmail());
        }

        if (updatedPublisher.getPhone() != null) {
            existingPublisher.setPhone(updatedPublisher.getPhone());
        }

        if (updatedPublisher.getLogoUrl() != null) {
            existingPublisher.setLogoUrl(updatedPublisher.getLogoUrl());
        }

        return publisherRepository.save(existingPublisher);
    }


    public void deletePublisher(String id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publisher with ID does not exist: " + id));
        publisherRepository.delete(publisher);
    }
}
