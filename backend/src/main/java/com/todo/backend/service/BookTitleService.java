package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dao.ReservationRepository;
import com.todo.backend.dto.booktitle.BookTitleDto;
import com.todo.backend.dto.booktitle.ResponseBookTitleDto;
import com.todo.backend.dto.review.ResponseReviewDto;
import com.todo.backend.entity.BookAuthor;
import com.todo.backend.entity.BookCategory;
import com.todo.backend.entity.BookCopy;
import com.todo.backend.entity.BookTitle;
import com.todo.backend.entity.Reservation;
import com.todo.backend.mapper.BookTitleMapper;
import com.todo.backend.mapper.ReviewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BookTitleService {
    private final BookTitleRepository bookTitleRepository;
    private final BookCopyRepository bookCopyRepository;
    private final ReservationRepository reservationRepository;
    private final BookTitleMapper bookTitleMapper;
    private final ReviewMapper reviewMapper;

    public BookTitleService(BookTitleRepository bookTitleRepository, 
                          BookCopyRepository bookCopyRepository,
                          ReservationRepository reservationRepository,
                          BookTitleMapper bookTitleMapper, 
                          ReviewMapper reviewMapper) {
        this.bookTitleRepository = bookTitleRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.reservationRepository = reservationRepository;
        this.bookTitleMapper = bookTitleMapper;
        this.reviewMapper = reviewMapper;
    }

    public List<ResponseBookTitleDto> getAllBookTitles() {
        List<BookTitle> bookTitles = bookTitleRepository.findAll();
        List<ResponseBookTitleDto> responseBookTitleDtos = new ArrayList<>();

        for (BookTitle bookTitle : bookTitles) {
            ResponseBookTitleDto responseBookTitleDto = bookTitleMapper.toResponseDto(bookTitle);
            List<String> authorIds = new ArrayList<>(bookTitle.getBookAuthors().stream().map(BookAuthor::getAuthorId).toList());
            List<String> categoryIds = new ArrayList<>(bookTitle.getBookCategories().stream().map(BookCategory::getCategoryId).toList());

            responseBookTitleDto.setAuthorIds(authorIds);
            responseBookTitleDto.setCategoryIds(categoryIds);
            responseBookTitleDtos.add(responseBookTitleDto);
        }        return responseBookTitleDtos;
    }

    public List<ResponseBookTitleDto> getAllBookTitlesWithCategoryAndAuthorName() {
        List<BookTitle> bookTitles = bookTitleRepository.findAll();
        List<ResponseBookTitleDto> responseBookTitleDtos = new ArrayList<>();

        for (BookTitle bookTitle : bookTitles) {
            ResponseBookTitleDto responseBookTitleDto = bookTitleMapper.toResponseDto(bookTitle);
            List<String> authorNames = new ArrayList<>(bookTitle.getBookAuthors().stream()
                    .map(bookAuthor -> bookAuthor.getAuthor().getName()).toList());
            List<String> categoryNames = new ArrayList<>(bookTitle.getBookCategories().stream()
                    .map(bookCategory -> bookCategory.getCategory().getName()).toList());

            responseBookTitleDto.setAuthorNames(authorNames);
            responseBookTitleDto.setCategoryNames(categoryNames);
            responseBookTitleDtos.add(responseBookTitleDto);
        }

        return responseBookTitleDtos;
    }

    public ResponseBookTitleDto getBookTitle(String id) {
        BookTitle bookTitle = bookTitleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book title ID does not exist"));

        ResponseBookTitleDto responseBookTitleDto = bookTitleMapper.toResponseDto(bookTitle);
        
        // Set author and category IDs
        List<String> authorIds = new ArrayList<>(bookTitle.getBookAuthors().stream().map(BookAuthor::getAuthorId).toList());
        List<String> categoryIds = new ArrayList<>(bookTitle.getBookCategories().stream().map(BookCategory::getCategoryId).toList());
        responseBookTitleDto.setAuthorIds(authorIds);
        responseBookTitleDto.setCategoryIds(categoryIds);
        
        // Set author and category names
        List<String> authorNames = new ArrayList<>(bookTitle.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getName()).toList());
        List<String> categoryNames = new ArrayList<>(bookTitle.getBookCategories().stream()
                .map(bookCategory -> bookCategory.getCategory().getName()).toList());
        responseBookTitleDto.setAuthorNames(authorNames);
        responseBookTitleDto.setCategoryNames(categoryNames);
        
        // Set reviews
        List<ResponseReviewDto> reviews = bookTitle.getReviews().stream()
                .map(reviewMapper::toResponseDto)
                .toList();
        responseBookTitleDto.setReviews(reviews);

        return responseBookTitleDto;
    }

    public ResponseBookTitleDto getBookTitleWithAvailability(String id, String currentUserId, boolean isUserRole) {
        BookTitle bookTitle = bookTitleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book title ID does not exist"));

        ResponseBookTitleDto responseBookTitleDto = bookTitleMapper.toResponseDto(bookTitle);
        
        // Set author and category IDs
        List<String> authorIds = new ArrayList<>(bookTitle.getBookAuthors().stream().map(BookAuthor::getAuthorId).toList());
        List<String> categoryIds = new ArrayList<>(bookTitle.getBookCategories().stream().map(BookCategory::getCategoryId).toList());
        responseBookTitleDto.setAuthorIds(authorIds);
        responseBookTitleDto.setCategoryIds(categoryIds);
        
        // Set author and category names
        List<String> authorNames = new ArrayList<>(bookTitle.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getName()).toList());
        List<String> categoryNames = new ArrayList<>(bookTitle.getBookCategories().stream()
                .map(bookCategory -> bookCategory.getCategory().getName()).toList());
        responseBookTitleDto.setAuthorNames(authorNames);
        responseBookTitleDto.setCategoryNames(categoryNames);
        
        // Set reviews
        List<ResponseReviewDto> reviews = bookTitle.getReviews().stream()
                .map(reviewMapper::toResponseDto)
                .toList();
        responseBookTitleDto.setReviews(reviews);

        // Calculate availability information using hybrid inventory system
        int totalCopies = bookTitle.getTotalCopies();
        int maxOnlineReservations = bookTitle.getMaxOnlineReservations();

        // Get active reservations for this book title (these are the ones that count as online reservations)
        LocalDate today = LocalDate.now();
        List<Reservation> activeReservations = reservationRepository.findActiveReservationsByBookTitleId(id, today);
        int onlineReservations = activeReservations.size();
        
        // Available copies for online reservation = max online reservations - current online reservations
        int availableForOnlineReservation = Math.max(0, maxOnlineReservations - onlineReservations);
        
        // Physical copies available = total copies - pending reservations
        int physicalCopiesAvailable = Math.max(0, totalCopies - onlineReservations);
        
        // Set availability information (visible to everyone)
        responseBookTitleDto.setTotalCopies(totalCopies);
        responseBookTitleDto.setAvailableCopies(availableForOnlineReservation); // For UI display of reservation availability
        responseBookTitleDto.setOnlineReservations(onlineReservations);
        responseBookTitleDto.setMaxOnlineReservations(maxOnlineReservations);        // Set user-specific information (only for authenticated users with USER role)
        if (currentUserId != null && isUserRole) {
            // Get user's total active reservations across all books (this shows global user reservation count)
            List<Reservation> userTotalReservations = reservationRepository.findActiveReservationsByUserId(currentUserId, today);
            responseBookTitleDto.setUserReservationsForThisBook(userTotalReservations.size()); // Note: despite field name, this is total user reservations
            responseBookTitleDto.setMaxUserReservations(5); // Business rule: max 5 reservations per user
        }

        return responseBookTitleDto;
    }

    public ResponseBookTitleDto createBookTitle(BookTitleDto bookTitleDto) {
        if (bookTitleDto.getAuthorIds().size() != bookTitleDto.getAuthorIds().stream().distinct().count()) {
            throw new RuntimeException("Duplicate author IDs found in the request");
        }

        if (bookTitleDto.getCategoryIds().size() != bookTitleDto.getCategoryIds().stream().distinct().count()) {
            throw new RuntimeException("Duplicate category IDs found in the request");
        }

        BookTitle bookTitle = bookTitleMapper.toEntity(bookTitleDto);
        if (bookTitleRepository.existsByIsbn(bookTitle.getIsbn())) {
            throw new RuntimeException("Book title with this ISBN already exists");
        }

        bookTitleRepository.saveAndFlush(bookTitle);

        List<BookAuthor> bookAuthors = new ArrayList<>();
        for (String authorId : bookTitleDto.getAuthorIds()) {
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBookTitleId(bookTitle.getId());
            bookAuthor.setAuthorId(authorId);
            bookAuthors.add(bookAuthor);
        }

        List<BookCategory> bookCategories = new ArrayList<>();
        for (String categoryId : bookTitleDto.getCategoryIds()) {
            BookCategory bookCategory = new BookCategory();
            bookCategory.setBookTitleId(bookTitle.getId());
            bookCategory.setCategoryId(categoryId);
            bookCategories.add(bookCategory);
        }

        bookTitle.setBookAuthors(bookAuthors);
        bookTitle.setBookCategories(bookCategories);

        bookTitleRepository.save(bookTitle);

        // Auto-generate BookCopy records based on totalCopies
        generateBookCopiesForTitle(bookTitle.getId(), bookTitleDto.getTotalCopies());

        ResponseBookTitleDto responseBookTitleDto = bookTitleMapper.toResponseDto(bookTitle);
        responseBookTitleDto.setAuthorIds(bookTitleDto.getAuthorIds());
        responseBookTitleDto.setCategoryIds(bookTitleDto.getCategoryIds());

        return responseBookTitleDto;
    }

    public ResponseBookTitleDto updateBookTitle(String id, BookTitleDto bookTitleDto) {
        if (bookTitleDto.getAuthorIds().size() != bookTitleDto.getAuthorIds().stream().distinct().count()) {
            throw new RuntimeException("Duplicate author IDs found in the request");
        }

        if (bookTitleDto.getCategoryIds().size() != bookTitleDto.getCategoryIds().stream().distinct().count()) {
            throw new RuntimeException("Duplicate category IDs found in the request");
        }

        BookTitle existingBookTitle = bookTitleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book title ID does not exist"));
        
        // Store old totalCopies for inventory reconciliation
        int oldTotalCopies = existingBookTitle.getTotalCopies();
        
        existingBookTitle.getBookAuthors().clear();
        existingBookTitle.getBookCategories().clear();

        bookTitleMapper.updateEntityFromDto(bookTitleDto, existingBookTitle);

        for (String authorId : bookTitleDto.getAuthorIds()) {
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBookTitleId(existingBookTitle.getId());
            bookAuthor.setAuthorId(authorId);
            existingBookTitle.getBookAuthors().add(bookAuthor);
        }

        for (String categoryId : bookTitleDto.getCategoryIds()) {
            BookCategory bookCategory = new BookCategory();
            bookCategory.setBookTitleId(existingBookTitle.getId());
            bookCategory.setCategoryId(categoryId);
            existingBookTitle.getBookCategories().add(bookCategory);
        }

        bookTitleRepository.save(existingBookTitle);

        // Reconcile inventory if totalCopies changed
        if (oldTotalCopies != bookTitleDto.getTotalCopies()) {
            reconcileInventory(existingBookTitle.getId());
        }

        ResponseBookTitleDto responseBookTitleDto = bookTitleMapper.toResponseDto(existingBookTitle);
        responseBookTitleDto.setAuthorIds(bookTitleDto.getAuthorIds());
        responseBookTitleDto.setCategoryIds(bookTitleDto.getCategoryIds());

        return responseBookTitleDto;
    }

    public void deleteBookTitle(String id) {
        BookTitle existingBookTitle = bookTitleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book title ID does not exist"));

        List<BookCopy> bookCopies = existingBookTitle.getBookCopies();
        for (BookCopy bookCopy : bookCopies) {
            if (bookCopy.getStatus().equals("BORROWED") || bookCopy.getStatus().equals("RESERVED")) {
                throw new IllegalArgumentException("Cannot delete book title with borrowed or reserved copies");
            }
        }        bookTitleRepository.delete(existingBookTitle);
    }

    /**
     * Auto-generate BookCopy records for a BookTitle with human-readable IDs
     */
    private void generateBookCopiesForTitle(String bookTitleId, int totalCopies) {
        BookTitle bookTitle = bookTitleRepository.findById(bookTitleId)
                .orElseThrow(() -> new IllegalArgumentException("BookTitle not found"));
        
        // Get the book title for creating readable copy IDs
        String titlePrefix = bookTitle.getTitle().replaceAll("[^a-zA-Z0-9]", "").substring(0, Math.min(bookTitle.getTitle().length(), 10)).toUpperCase();
        
        for (int i = 1; i <= totalCopies; i++) {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setBookTitleId(bookTitleId);
            bookCopy.setStatus("AVAILABLE");
            bookCopy.setCondition("NEW"); // Set default condition for auto-generated copies
            
            // Generate human-readable ID like "HARRYPOTTER001", "HARRYPOTTER002", etc.
            String copyNumber = String.format("%03d", i);
            // Let the database auto-generate the actual ID, but we could add a displayId field if needed
            
            bookCopyRepository.save(bookCopy);
        }
    }

    /**
     * Reconcile inventory between BookTitle counters and actual BookCopy records
     */
    public void reconcileInventory(String bookTitleId) {
        BookTitle bookTitle = bookTitleRepository.findById(bookTitleId)
                .orElseThrow(() -> new IllegalArgumentException("BookTitle not found"));
                
        List<BookCopy> actualCopies = bookCopyRepository.findByBookTitleId(bookTitleId);
        int actualCopyCount = actualCopies.size();
        int expectedCopyCount = bookTitle.getTotalCopies();
        
        if (actualCopyCount < expectedCopyCount) {
            // Generate missing copies
            int missingCopies = expectedCopyCount - actualCopyCount;
            generateBookCopiesForTitle(bookTitleId, missingCopies);
        } else if (actualCopyCount > expectedCopyCount) {
            // Remove excess available copies (only if they're available)
            int excessCopies = actualCopyCount - expectedCopyCount;
            List<BookCopy> availableCopies = actualCopies.stream()
                    .filter(copy -> "AVAILABLE".equals(copy.getStatus()))
                    .limit(excessCopies)
                    .toList();
                    
            for (BookCopy copy : availableCopies) {
                bookCopyRepository.delete(copy);
            }
        }
    }

    /**
     * Check if a user can make a reservation for this book title
     */
    public boolean canUserReserveBook(String bookTitleId, String userId) {
        BookTitle bookTitle = bookTitleRepository.findById(bookTitleId)
                .orElseThrow(() -> new IllegalArgumentException("BookTitle not found"));
                
        // Check if there are available slots for online reservations
        LocalDate today = LocalDate.now();
        List<Reservation> activeReservations = reservationRepository.findActiveReservationsByBookTitleId(bookTitleId, today);
        int currentOnlineReservations = activeReservations.size();
        
        if (currentOnlineReservations >= bookTitle.getMaxOnlineReservations()) {
            return false; // No more online reservation slots available
        }
        
        // Check if user already has an active reservation for this book
        List<Reservation> userReservations = reservationRepository.findByUserIdAndBookTitleId(userId, bookTitleId);
        for (Reservation reservation : userReservations) {
            if (reservation.getExpirationDate().isAfter(today) || reservation.getExpirationDate().isEqual(today)) {
                return false; // User already has an active reservation for this book
            }
        }
        
        return true;
    }
}
