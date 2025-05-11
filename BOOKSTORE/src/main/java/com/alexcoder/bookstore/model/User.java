package com.alexcoder.bookstore.model;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
/*Jakarta Persistence (JPA):

Successor to the older javax.persistence package (renamed after Oracle transferred Java EE to the Eclipse Foundation).

Provides annotations and interfaces to define how Java objects (entities) are stored, retrieved, and managed in a relational database.

Common Annotations/Classes:

@Entity: Marks a Java class as a persistent entity (maps to a database table).

@Id: Specifies the primary key of an entity.

@GeneratedValue: Configures automatic generation of primary key values (e.g., auto-increment).

@Column: Maps a field to a database column (customize column name, constraints, etc.).

@Table: Specifies the database table name for an entity.

@OneToMany, @ManyToOne, etc.: Define relationships between entities.

EntityManager: Interface for interacting with the persistence context (e.g., CRUD operations). */
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users")
/*
 * By default, the entity maps to a table with the same name as the class.
 * Use @Table to customize the table name:
 */
//@Data
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor



public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String username;


    /*
     * Since @Column(name = "...") is not specified, the column name defaults to the
     * Java field name, adjusted by the JPA provider’s naming strategy.
     */
    @Column(nullable = false)
    private String password;

    @Column(unique = true,nullable = false)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();


    @Column(nullable = false)
    private boolean enabled = true;



}



/*
 * @ElementCollection(fetch = FetchType.EAGER):
 * 
 * @ElementCollection: This annotation signifies that you're mapping a
 * collection of basic or embeddable types. Instead of having a separate entity
 * for each role, these values will be stored in a separate table.
 * fetch = FetchType.EAGER: This specifies the fetching strategy for the roles
 * collection. EAGER means that whenever you load the User entity, the roles
 * collection will also be loaded in the same database query. While convenient,
 * be mindful that excessive eager fetching can lead to performance issues if
 * the collection is large or if you don't always need the roles. Consider
 * FetchType.LAZY if performance becomes a concern; in that case, the roles will
 * only be loaded when you explicitly access them.
 * 
 * @CollectionTable(...):
 * 
 * @CollectionTable: This annotation is used to specify the details of the
 * database table that will store the elements of the collection.
 * name = "User_roles": This sets the name of the database table to
 * "User_roles".
 * joinColums = @JoinColumn(name = "user_id"): This defines the foreign key
 * column in the "User_roles" table that links back to the primary key of the
 * User entity. The column will be named "user_id".
 * 
 * @Column(name = "role"):
 * 
 * @Column: This annotation specifies the details of the column in the
 * "User_roles" table that will store the actual role values (the String
 * elements of the roles set). The column will be named "role".
 * private Set<String> roles = new HashSet<>();:
 * 
 * This declares a private instance variable named roles of type Set<String>.
 * Using a Set ensures that each role for a user will be unique (no duplicate
 * roles). HashSet is a common implementation of the Set interface, known for
 * its performance in adding, removing, and checking for the existence of
 * elements.
 * In essence, this mapping will create a database table named "User_roles" with
 * two columns:
 * 
 * user_id: This will be a foreign key referencing the primary key of your User
 * table. Each user_id can have multiple entries in this table, representing the
 * different roles associated with that user.
 * role: This column will store the actual role (e.g., "ADMIN", "USER",
 * "EDITOR").
 * Example:
 * 
 * Let's say you have a User entity with an ID of 1. If this user has the roles
 * "ADMIN" and "USER", the "User_roles" table might look like this:
 * 
 * user_id role
 * 1 ADMIN
 * 1 USER
 * 
 * Export to Sheets
 * This is a clean and efficient way to handle one-to-many relationships where
 * the "many" side consists of simple values rather than full-fledged entities.
 */



/*
 * have two User entities with the following IDs and roles:
 * 
 * User with ID 1: Roles are "ADMIN" and "EDITOR".
 * User with ID 2: Roles are "USER" and "EDITOR".
 * Following the @CollectionTable and @Column definitions, the "User_roles"
 * table would contain the following data:
 * 
 * user_id role
 * 1 ADMIN
 * 1 EDITOR
 * 2 USER
 * 2 EDITOR
 * 
 * 
 * Explanation:
 * 
 * Each row in the "User_roles" table represents a single role assigned to a
 * specific user.
 * The user_id column clearly identifies which user the role belongs to. Notice
 * how user_id 1 appears twice, once for each of that user's roles. Similarly,
 * user_id 2 also appears twice.
 * The role column stores the specific role assigned to that user.
 * When your JPA provider (like Hibernate) loads a User entity with ID 1, it
 * will query the "User_roles" table for all entries where user_id is 1. The
 * result will be the rows with "ADMIN" and "EDITOR" in the role column. These
 * values will then be populated into the roles Set of the User object. The same
 * process occurs for User with ID 2, retrieving "USER" and "EDITOR" for their
 * respective roles set.
 * 
 * This structure efficiently stores the many-to-many relationship between users
 * and roles using a separate table to link them. Each user can have multiple
 * roles, and the same role (like "EDITOR" in this example) can be assigned to
 * multiple users.
 */

 /*
  * potential equals() and hashCode() issues when using @Data with JPA entities,
  * especially when relationships are involved. Lombok's @Data generates default
  * implementations for these methods that consider all fields, which can lead to
  * problems like infinite recursion or incorrect comparisons in certain JPA
  * scenarios.
  * 
  * Replacing @Data with @Getter and @Setter and then carefully
  * implementing @EqualsAndHashCode is indeed a safer approach. Here's how you
  * can refactor your User entity
  * Key Changes and Why They Matter:
  * 
  * Removed @Data: This annotation has been replaced by @Getter and @Setter,
  * giving you more control over which fields have these methods.
  * 
  * Added @EqualsAndHashCode: This Lombok annotation now explicitly generates the
  * equals() and hashCode() methods.
  * 
  * onlyExplicitlyIncluded = true in @EqualsAndHashCode: This is crucial. It
  * tells Lombok to only include fields annotated with @EqualsAndHashCode.Include
  * when generating the equals() and hashCode() methods.
  * 
  * @EqualsAndHashCode.Include on @Id and @username: In this example, we've
  * chosen to include the id and username fields in the equals() and hashCode()
  * calculations.
  * 
  * id: Including the primary key is a common and often safe approach for entity
  * comparison, especially after the entity has been persisted and has a
  * database-generated ID.
  * username: Since username is also unique and a business identifier, including
  * it in equals() and hashCode() can be logical.
  * Why This Approach Helps Avoid Issues:
  * 
  * Avoiding Circular Dependencies: When entities have relationships (e.g., a
  * User might have a collection of Order entities, and an Order might refer back
  * to a User), the default @Data-generated equals() and hashCode() might try to
  * traverse these relationships. This can lead to infinite recursion and a
  * StackOverflowError. By explicitly controlling which fields are included, you
  * can avoid these cycles.
  * 
  * Handling Transient Fields: If your entity has fields that are not persisted
  * in the database (marked with @Transient), you likely don't want them to
  * influence the equality or hash code of the
  * entity. @EqualsAndHashCode(onlyExplicitlyIncluded = true) ensures that only
  * the persistent, identifying attributes are considered.
  * 
  * Consistent Behavior: By explicitly defining which attributes determine
  * equality, you make the behavior of your entities more predictable and less
  * prone to unexpected issues when used in collections (like HashSet) or when
  * comparing entities.
  * 
  * Considerations for @EqualsAndHashCode:
  * 
  * Choose Identifying Attributes Carefully: The fields you include
  * in @EqualsAndHashCode should be the ones that uniquely identify an entity
  * from a business perspective. The primary key (@Id) is often a good candidate,
  * especially for persisted entities. However, for transient entities (not yet
  * persisted), you might rely on other unique business keys.
  * 
  * Immutability: If your entities are designed to be immutable after creation,
  * including all final fields in equals() and hashCode() can be a good strategy.
  * 
  * Performance: While generally not a major concern, including a large number of
  * fields in equals() and hashCode() can slightly impact performance. Stick to
  * the essential identifying attributes.
  * 
  * By adopting this more controlled approach with @Getter, @Setter,
  * and @EqualsAndHashCode(onlyExplicitlyIncluded = true), you gain better
  * control over the generated equals() and hashCode() methods for your JPA
  * entities, reducing the risk of common pitfalls associated with Lombok's @Data
  * in such contexts.
  */