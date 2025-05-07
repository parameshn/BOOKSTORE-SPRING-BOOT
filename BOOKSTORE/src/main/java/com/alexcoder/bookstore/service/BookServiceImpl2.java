package com.alexcoder.bookstore.service;

import com.alexcoder.bookstore.dto.BookDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class BookServiceImpl2 implements BookService {

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        // Simple logic to create a book
        return bookDTO;
    }

    @Override
    public BookDTO getBookById(Long id) {
        // Return a dummy book for the given ID
        return new BookDTO();
    }

    @Override
    public List<BookDTO> getAllBooks() {
        List<BookDTO> books = new ArrayList<>();
        return books;
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        // Simple logic to update a book
        return bookDTO;
    }

    @Override
    public void deleteBook(Long id) {
        // Simple logic to delete a book
    }

    @Override
    public List<BookDTO> searchBooksByTitle(String title) {
        return new ArrayList<>();
    }

    @Override
    public List<BookDTO> searchBooksByAuthor(String author) {
        return new ArrayList<>();
    }

    @Override
    public BookDTO getBookByIsbn(String isbn) {
        return new BookDTO();
    }
}
