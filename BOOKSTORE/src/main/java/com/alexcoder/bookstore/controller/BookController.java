package com.alexcoder.bookstore.controller;

// Imports the BookDTO class used for transferring data between layers
import com.alexcoder.bookstore.dto.BookDTO;

// Imports the service interface that contains business logic methods for books
import com.alexcoder.bookstore.service.BookService;

// Imports the @Valid annotation to enable bean validation on method parameters
import jakarta.validation.Valid;

// Imports Spring's @Autowired annotation for automatic dependency injection
import org.springframework.beans.factory.annotation.Autowired;

// Imports Spring's ResponseEntity and HttpStatus for building HTTP responses
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// Imports annotations for REST controller and request mapping
import org.springframework.web.bind.annotation.*;

import java.util.List; // Imports the List interface for handling collections of BookDTO

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Create a new book
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    // Get all books
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // Get a single book by its ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        BookDTO book = bookService.getBookById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    // Update an existing book by ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    // Delete a book by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

/*
 * Spring will refuse to start if you accidentally map two methods to the exact
 * same verb+path+media-type combination, because it wouldn’t know which one to
 * call.
 */
