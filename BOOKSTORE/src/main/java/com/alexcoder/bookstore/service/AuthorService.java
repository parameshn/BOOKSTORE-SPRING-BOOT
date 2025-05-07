package com.alexcoder.bookstore.service;

import com.alexcoder.bookstore.dto.AuthorDTO;
import com.alexcoder.bookstore.model.AuthorDetails;

import java.util.*;

public interface AuthorService {
    
    AuthorDTO createAuthor(AuthorDTO authorDTO);

    AuthorDTO getAuthorByID(Long id);

    List<AuthorDTO> getAllAuthors();

    AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO);

    void deleteAuthor(Long id);

    List<AuthorDTO> searchAuthorsByName(String name);

    List<AuthorDTO> searchAuthorsByNationality(String nationality);

    Optional<String> getAuthorBiography(Long id);

    AuthorDetails getAuthorDetails(Long id);

}
