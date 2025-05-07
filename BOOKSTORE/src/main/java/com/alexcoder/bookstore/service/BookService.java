package com.alexcoder.bookstore.service;

import com.alexcoder.bookstore.dto.BookDTO;
import java.util.List;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO);

    BookDTO getBookById(Long id);

    List<BookDTO> getAllBooks();

    BookDTO updateBook(Long id, BookDTO bookDTO);

    void deleteBook(Long id);

    List<BookDTO> searchBooksByTitle(String title);

    List<BookDTO> searchBooksByAuthor(String author);

    BookDTO getBookByIsbn(String isbn);


}
