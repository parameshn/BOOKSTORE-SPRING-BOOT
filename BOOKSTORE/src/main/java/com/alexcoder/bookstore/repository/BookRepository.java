package com.alexcoder.bookstore.repository;

// Imports the JpaRepository interface, which provides CRUD operations and more for JPA entities.
import org.springframework.data.jpa.repository.JpaRepository;
// Marks an interface as a Spring Data repository so it can be automatically detected and managed by Spring.
import org.springframework.stereotype.Repository;

import com.alexcoder.bookstore.model.Book;
import java.util.List;
import java.util.Optional;


/*built-in:
 * Optional<Book> findById(Long id);
List<Book> findAll();
Book save(Book book);
void deleteById(Long id);
boolean existsById(Long id);
long count();

extra adding :
List<Book> findByAuthorContainingIgnoreCase(String author);
List<Book> findByTitleContainingIgnoreCase(String title);
Optional<Book> findByIsbn(String isbn);
List<Book> findByPublicationYear(Integer year);
boolean existsByIsbn(String isbn);
 */

 //defining additional methods—on top of the existing ones already provided by JpaRepository

 @Repository
 public interface BookRepository extends JpaRepository<Book, Long> {
     // Custom query methods can go here

     // Spring Data JPA parses the method name and automatically builds the
     // appropriate SQL/JPQL query.

     List<Book> findByAuthorContainingIgnoreCase(String author);

     List<Book> findByTitleContainingIgnoreCase(String title);

     Optional<Book> findByIsbn(String isbn);

     List<Book> findByPublicationYear(Integer year);

     boolean existsByIsbn(String isbn);

 }
/*
 * JpaRepository is a Spring Data interface that comes with many built-in
 * methods like:
 * 
 * save()
 * 
 * findById()
 * 
 * delete()
 * 
 * existsById()
 * 
 * and many more
 * 
 * Spring Boot, during application startup, uses a proxy pattern to dynamically
 * generate an actual implementation of BookRepository and registers it as a
 * Spring bean.
 */
 
/*
 * Okay, let's break down the distinct roles and provide more details for Spring
 * Data JPA, JPA, and Hibernate, clarifying how they relate to each other and
 * what each specifically contributes.
 * 
 * Here's a detailed look at each component:
 ** 
 * 1. JPA (Java Persistence API)**
 * 
 * **Role:** **Standard Specification/API**. JPA is the *definition* of how to
 * perform object-relational mapping (ORM) in Java. It's like a contract or a
 * set of rules that ORM providers must follow. It's part of the Jakarta EE
 * (formerly Java EE) standard.
 * **Key Characteristics & Details:**
 * **Vendor-Neutral:** It provides a standard API that *any* compliant ORM
 * framework can implement (like Hibernate, EclipseLink, etc.). This means you
 * can potentially switch the underlying implementation without significantly
 * changing your application code that uses the JPA API directly.
 * **Core Concepts:** Defines the fundamental concepts:
 * **Entities:** Plain Old Java Objects (POJOs) that represent database tables,
 * marked with the `@Entity` annotation.
 * **EntityManager:** The primary interface for interacting with the persistence
 * context. It's used for performing CRUD operations (persist, merge, remove,
 * find) and executing queries.
 * **Persistence Context:** A cache that manages the state of entity instances
 * during a transaction. It ensures that within a single transaction, you work
 * with the same instance of a particular entity.
 * **JPQL (Java Persistence Query Language):** An object-oriented query language
 * that operates on entities and their relationships, rather than directly on
 * database tables and columns.
 * **Criteria API:** A type-safe, programmatic way to build queries in Java.
 * **Annotations:** A rich set of annotations (`@Table`, `@Column`, `@Id`,
 * `@GeneratedValue`, `@OneToOne`, `@OneToMany`, etc.) for mapping Java objects
 * to database schemas and defining relationships.
 * **Configuration:** Typically configured via `META-INF/persistence.xml` in
 * Java SE environments or managed by the application server in Java EE/Jakarta
 * EE environments.
 * **What it *doesn't* do:** JPA does *not* provide the actual implementation
 * code for database interaction. It doesn't connect to the database, generate
 * SQL, or manage sessions itself. It just defines the interface and expected
 * behavior.
 ** 
 * 2. Hibernate**
 * 
 * **Role:** **JPA Implementation (and a full-featured ORM framework)**.
 * Hibernate is one of the most popular and mature *implementations* of the JPA
 * specification. It provides the concrete code that performs the ORM tasks
 * defined by JPA. Before JPA existed, Hibernate was already a leading ORM
 * framework, and it later adopted and implemented the JPA standard.
 * **Key Characteristics & Details:**
 * **Concrete Implementation:** Provides the actual code for the
 * `EntityManager`, JPQL processing, entity state management, and database
 * interaction defined by JPA.
 * **Beyond JPA:** While fully implementing JPA, Hibernate also offers its own
 * set of features that go beyond the JPA specification. These include:
 * **Hibernate Query Language (HQL):** Hibernate's native object-oriented query
 * language, which predates JPQL but is very similar.
 * **Session Interface:** Hibernate's native API for interacting with the
 * database, comparable to JPA's `EntityManager` but with some
 * Hibernate-specific capabilities.
 * **Criteria API:** Hibernate's version of the Criteria API, with some
 * extensions.
 * **Advanced Features:** Custom types, filters, interceptors, enhanced caching
 * strategies (second-level cache), richer mapping options, and more control
 * over SQL generation.
 * **Database Interaction:** Handles the low-level details of connecting to the
 * database using JDBC, generating SQL statements (DML and DDL), executing them,
 * and mapping result sets back to Java objects.
 * **Configuration:** Can be configured using JPA's `persistence.xml` or its own
 * configuration files (`hibernate.cfg.xml`) and properties.
 * **Relationship with JPA:** Hibernate can be used *as a JPA provider*, meaning
 * you use the JPA API (`EntityManager`, JPQL, `@Entity` annotations) in your
 * code, and Hibernate is configured as the underlying engine that makes it all
 * work. You can also use Hibernate's native API (Session, HQL), which gives you
 * access to Hibernate-specific features not covered by the JPA standard.
 ** 
 * 3. Spring Data JPA**
 * 
 * **Role:** **Higher-level Abstraction and Repository Pattern Implementation**.
 * Spring Data JPA is part of the larger Spring Data project. It sits *on top
 * of* JPA (and thus, typically, on top of a JPA implementation like Hibernate)
 * to drastically simplify data access layer development. It achieves this by
 * providing interfaces based on the Repository design pattern.
 * **Key Characteristics & Details:**
 * **Reduces Boilerplate:** Its primary goal is to eliminate the need to write
 * repetitive implementation code for common data access operations (CRUD).
 * **Repository Abstraction:** Introduces the concept of repository interfaces
 * (like `CrudRepository`, `PagingAndSortingRepository`, `JpaRepository`). You
 * define interfaces that extend these base interfaces.
 * **Automatic Implementation:** Spring Data JPA automatically generates the
 * concrete implementation classes for your repository interfaces at runtime
 * based on the methods you declare.
 * **Query Derivation:** Infers database queries directly from method names
 * using a specific naming convention (e.g., `findByLastNameAndFirstName`,
 * `findByPriceGreaterThan`).
 * **`@Query` Annotation:** Allows you to define custom JPQL or native SQL
 * queries directly on interface methods for more complex scenarios not covered
 * by query derivation.
 * **Pagination and Sorting:** Built-in support for easily querying data in
 * pages and specifying sort orders.
 * **Integration with Spring:** Seamlessly integrates with the Spring
 * Framework's dependency injection, transaction management, and exception
 * handling.
 * **Simplified Configuration:** Especially with Spring Boot, configuring Spring
 * Data JPA and the underlying JPA provider (like Hibernate) is often as simple
 * as adding dependencies and a few properties.
 * **What it *doesn't* do:** Spring Data JPA does *not* implement the ORM logic
 * itself. It relies on a standard JPA provider (like Hibernate) to perform the
 * actual mapping, SQL generation, and database communication. It acts as a
 * facade or a layer of abstraction over JPA.
 ** 
 * Hierarchy and Interaction**
 * 
 * Think of it as layers:
 * 
 * 1. **Your Application Code:** Your service layer and controllers interact
 * with **Spring Data JPA Repository Interfaces**.
 * 2. **Spring Data JPA:** Interprets your repository interfaces, generates
 * implementations, parses method names/`@Query` annotations, and delegates the
 * actual data operations to the **JPA EntityManager**.
 * 3. **JPA (API):** Provides the standard interfaces (`EntityManager`, JPQL)
 * that Spring Data JPA uses.
 * 4. **Hibernate (or another JPA Implementation):** Provides the concrete
 * implementation for the JPA interfaces. It performs the ORM logic, translates
 * JPQL/Criteria API calls into SQL, interacts with the database via JDBC, and
 * manages the persistence context.
 * 5. **JDBC:** The low-level Java API for connecting to and interacting with
 * relational databases.
 * 6. **Database:** Stores your data.
 * 
 * In summary:
 * 
 * **JPA:** The **standard** defining ORM in Java.
 * **Hibernate:** A popular **implementation** of the JPA standard (and a
 * powerful ORM in its own right).
 * **Spring Data JPA:** A **higher-level abstraction** built on top of JPA to
 * simplify data access using the Repository pattern, reducing boilerplate code.
 */