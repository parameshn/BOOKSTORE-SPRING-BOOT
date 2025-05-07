package com.alexcoder.bookstore.service;

// Imports the BookDTO class, used for transferring book data between layers
import com.alexcoder.bookstore.dto.BookDTO;

// Imports a custom exception class for handling "resource not found" cases
import com.alexcoder.bookstore.exception.ResourceNotFoundException;

// Imports the Book entity, representing the 'books' table in the database
import com.alexcoder.bookstore.model.Book;

// Imports the BookRepository interface for database operations (CRUD) on Book entity
import com.alexcoder.bookstore.repository.BookRepository;

// Imports Spring's @Autowired annotation for automatic dependency injection
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
// Imports Spring's @Service annotation to mark the class as a service-layer component
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;



@Service
@Primary // This marks this implementation as the preferred one when multiple
         // implementations are present
public class BookServiceImpl implements BookService {


    private final BookRepository bookRepository;
    /* Spring injects the proxy object into your service class. */

     @Autowired
     public BookServiceImpl(BookRepository bookRepository) {
         this.bookRepository = bookRepository;
     }

     @Override
     public BookDTO createBook(BookDTO bookDTO) {
         if (bookDTO.getIsbn() != null && bookRepository.existsByIsbn(bookDTO.getIsbn())) {
             throw new IllegalArgumentException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
         }

         Book book = mapToEntity(bookDTO); // BookDTO to a Book entity.
         Book savedBook = bookRepository.save(book); // saves the Book entity to the database

         return mapToDTO(savedBook); // converts the savedBook entity back into a BookDTO
     }
     

     @Override
     public BookDTO getBookById(Long id) {
         Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
         return mapToDTO(book);
     }

     @Override
     public List<BookDTO> getAllBooks() {
         List<Book> books = bookRepository.findAll();
         return books.stream().map(this::mapToDTO).collect(Collectors.toList());

     }

     @Override
     public BookDTO updateBook(Long id, BookDTO bookDTO) {
         Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
         if (bookDTO.getIsbn() != null && !bookDTO.getIsbn().equals(book.getIsbn())
                 && bookRepository.existsByIsbn(bookDTO.getIsbn())) {
             throw new IllegalArgumentException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
         }

         book.setTitle(bookDTO.getTitle());
         book.setAuthor(bookDTO.getAuthor());
         book.setDescription(bookDTO.getDescription());
         book.setPrice(bookDTO.getPrice());
         book.setIsbn(bookDTO.getIsbn());
         book.setPublicationYear(bookDTO.getPublicationYear());

         Book updatedBook = bookRepository.save(book);

         return mapToDTO(updatedBook);
     }
     /*
      * bookDTO.getIsbn() != null: Ensures the new ISBN is not null.
      * 
      * !bookDTO.getIsbn().equals(book.getIsbn()): Checks that the new ISBN is
      * different from the existing ISBN of the current book.
      * 
      * bookRepository.existsByIsbn(bookDTO.getIsbn()): Checks if another book
      * already exists in the database with the same ISBN
      */

     @Override
     public void deleteBook(Long id) {
         Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
         bookRepository.delete(book);
         /*
          * Spring Data JPA automatically generates the implementation of that interface
          * at runtime
          */
     }
      
     @Override
     public List<BookDTO> searchBooksByTitle(String title) {
         List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
         return books.stream()
                 .map(this::mapToDTO)
                 .collect(Collectors.toList());
     }

     @Override
     public List<BookDTO> searchBooksByAuthor(String author) {
         List<Book> books = bookRepository.findByAuthorContainingIgnoreCase(author);
         return books.stream()
                 .map(this::mapToDTO)
                 .collect(Collectors.toList());
     }

     @Override
     public BookDTO getBookByIsbn(String isbn) {
         Book book = bookRepository.findByIsbn(isbn)
                 .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));
         return mapToDTO(book);
     }

         

     private BookDTO mapToDTO(Book book) {
         return new BookDTO(
                 book.getId(), // Mapping id from Book to BookDTO
                 book.getTitle(), // Mapping title from Book to BookDTO
                 book.getAuthor(), // Mapping author from Book to BookDTO
                 book.getDescription(), // Mapping description from Book to BookDTO
                 book.getPrice(), // Mapping price from Book to BookDTO
                 book.getIsbn(), // Mapping ISBN from Book to BookDTO
                 book.getPublicationYear() // Mapping publicationYear from Book to BookDTO
         );
     }

     private Book mapToEntity(BookDTO bookDTO) {
         return new Book(
                 bookDTO.getId(), // Mapping id from BookDTO to Book entity
                 bookDTO.getTitle(), // Mapping title from BookDTO to Book entity
                 bookDTO.getAuthor(), // Mapping author from BookDTO to Book entity
                 bookDTO.getDescription(), // Mapping description from BookDTO to Book entity
                 bookDTO.getPrice(), // Mapping price from BookDTO to Book entity
                 bookDTO.getIsbn(), // Mapping ISBN from BookDTO to Book entity
                 bookDTO.getPublicationYear(), // Mapping publicationYear from BookDTO to Book entity
                 null, // Null placeholders for createdAt and updatedAt fields
                 null);

                 /*
                  *  passing null for the createdAt and updatedAt fields. This might be
                  * fine if  handling these fields automatically (e.g., through @PrePersist
                  * and @PreUpdate annotations in your Book entity), but if not, those
                  * fields will remain null when saved.
                  */
     }



}



/*
 * In Spring, a bean is an object managed by the Spring IoC (Inversion of
 * Control) container. Beans are created, configured, and managed by Spring,
 * allowing for dependency injection and lifecycle management.
 * 
 * Why BookRepository Is a Bean
 * In your application, BookRepository is an interface that extends
 * JpaRepository. Spring Data JPA automatically detects such interfaces and
 * provides implementations at runtime. These implementations are registered as
 * beans in the Spring context, making them available for dependency injection.
 * 
 * This automatic detection and registration are facilitated by Spring Boot's
 * auto-configuration and component scanning mechanisms. As per the Spring Data
 * JPA documentation:
 * 
 * "Each bean is registered under a bean name that is derived from the interface name, so an interface of 
 * UserRepository would be registered under userRepository."
 * 
 *  How Spring Creates the Bean
 * When your application starts, Spring Boot performs the following steps:
 * 
 * Component Scanning: It scans the specified base packages for components,
 * including repositories.
 * 
 * Repository Interface Detection: It identifies interfaces that extend
 * JpaRepository or other Spring Data interfaces.
 * 
 * Proxy Creation: For each detected repository interface, Spring Data JPA
 * creates a proxy implementation that handles the data access logic.
 * 
 * Bean Registration: These proxy implementations are registered as beans in the
 * Spring context, allowing them to be injected into other components.
 */