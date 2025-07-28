package com.alexcoder.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.alexcoder.bookstore.model.Author;
import com.alexcoder.bookstore.model.AuthorDetails;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;



@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findById(long id);

    Author getByName(String name);

    boolean existsById(long id);// (Spring Data provides this by default)
    /*
     * For Spring Data JPA repository methods like existsById, use Long (the wrapper
     * class) instead of the primitive long
     * Null support
     */


    List<Author> findAllByNameIgnoreCase(String name);
    @Override
    void deleteById(@NonNull Long id);// (4) (Spring Data provides this by default)

    List<Author> findByNationality(String nationality);

    //Optional<String> readByAuthorBiography(long id);
    @Query("SELECT a.biography FROM Author a WHERE a.id = :id")
    Optional<String> findBiographyById(@Param("id") Long id);
    
    /*You only need @Param when you're using a custom @Query with named parameters like :customerId.
    You don’t need @Param for derived query methods like
    :customerId is a named parameter in JPQL
    Spring needs to know which method parameter it maps to
    @Param("customerId") links the JPQL placeholder to the method parameter
    Without @Param, Spring doesn’t know how to bind the values, and you’ll get an error*/




    AuthorDetails findDetailsById(Long id);



}
