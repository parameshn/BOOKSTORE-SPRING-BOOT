package com.alexcoder.bookstore.model;

// Imports annotations and classes from the Jakarta Persistence API (JPA),
// used for defining entity classes and mapping them to database tables.
import jakarta.persistence.*;

// Lombok annotation that generates a constructor with one parameter for each
// field in the class.
import lombok.AllArgsConstructor;

// Lombok annotation that generates getters, setters, toString, equals, and
// hashCode methods.
import lombok.Data;

// Lombok annotation that generates a no-argument constructor.
import lombok.NoArgsConstructor;

// Provides immutable, arbitrary-precision signed decimal numbers, commonly used
// for currency values.
import java.math.BigDecimal;

// Represents a date-time without a time-zone in the ISO-8601 calendar system,
// useful for timestamps.
import java.time.LocalDateTime;

// Marks this class as a JPA entity, meaning it will be mapped to a database table.
@Entity
// Specifies the table name in the database that this entity maps to.
@Table(name = "books")
// Lombok annotation to automatically generate getters, setters, toString,
// equals, and hashCode methods.
@Data
// Lombok annotation to generate a no-argument constructor.
@NoArgsConstructor
// Lombok annotation to generate a constructor with all fields as arguments.
@AllArgsConstructor

public class Book {
    @Id // Marks the primary key field of the entity.

    // Specifies how the primary key should be generated (auto-increment strategy).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    // Fields that will be mapped to columns in the "books" table.
    
    private String title;
    private String author;
    private String description;
    private BigDecimal price;
    private String isbn;
    private Integer publicationYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // This method is automatically called before the entity is inserted into the
    // database.
    // It sets createdAt and updatedAt timestamps.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    // This method is automatically called before the entity is updated in the
    // database.
    // It updates the updatedAt timestamp.
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }




}



