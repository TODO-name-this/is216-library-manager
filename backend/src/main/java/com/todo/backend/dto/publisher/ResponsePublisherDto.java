package com.todo.backend.dto.publisher;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponsePublisherDto {
    private String id;
    private String logoUrl;
    private String name;
    private String address;
    private String email;
    private String phone;
}
