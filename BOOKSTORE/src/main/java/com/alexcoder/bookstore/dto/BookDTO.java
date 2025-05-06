package com.alexcoder.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;


//Data Transfer Object.
/*It's a plain Java object used to transfer data between layers 
(like from your frontend → controller → service → repository), without exposing the 
entire entity model. */

@Data
@NoArgsConstructor
@AllArgsConstructor

/*
 * The class is opened but not properly completed. Several fields are missing,
 * and the class is not closed with a closing brace. As a result, the compiler
 * expects additional content after the last annotation, leading to syntax
 * errors
 */

public class BookDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")

    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 100, message = "Author name cannot exceed 100 characters")
    private String author;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Pattern(regexp = "^\\d{10,13}$", message = "ISBN must be 10 or 13 digits")
    private String isbn;

    @Min(value = 1000, message = "Publication year must be after 1000")
    @Max(value = 9999, message = "Publication year must be a valid year")
    private Integer publicationYear;

}
// The BookDTO class is used for transferring book data between the client and
// server.


/*
 * Security – You can avoid exposing internal fields (like passwords, database
 * IDs).
 * 
 * Validation – You can apply validation rules (@NotBlank, @Size, etc.) before
 * saving to the database.
 * 
 * Separation of concerns – Keeps your entity (Book) focused on persistence, and
 * the DTO (BookDTO) focused on client input/output.
 * 
 * Flexibility – You can customize the shape of data sent/received without
 * modifying your entity.
 */

 /*
  * You convert from BookDTO → Book when saving, and back from Book → BookDTO
  * when sending a response
  */

  /*
   * Why Use a DTO Instead
   * Only expose what the client needs
   * 
   * Customize your API layer without touching your database
   * 
   * Simplify request/response formats
   * 
   * Add validation with jakarta.validation
   * 
   * Prevent accidental data manipulation (like user setting id or createdAt)
   */