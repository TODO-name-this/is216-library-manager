package com.todo.backend.dto.booktitle;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class BookTitleDto {
    @URL(message = "Invalid URL format")
    private String imageUrl;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    private boolean canBorrow;

    @NotNull(message = "Price is required")
    private Integer price;

    @Past(message = "Date of publication must be in the past")
    private LocalDate publishedDate;

    @NotBlank(message = "Publisher ID is required")
    private String publisherId;

    @NotNull(message = "Missing author ids")
    private List<String> authorIds;

    @NotNull(message = "Missing category ids")
    private List<String> categoryIds;

    @NotNull(message = "Total copies is required")
    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;

    @NotNull(message = "Max online reservations is required")
    @Min(value = 0, message = "Max online reservations cannot be negative")
    private Integer maxOnlineReservations;
}
