package com.alexcoder.bookstore.service;

import com.alexcoder.bookstore.dto.AuthorDTO;
import com.alexcoder.bookstore.exception.ResourceNotFoundException;
import com.alexcoder.bookstore.model.Author;
import com.alexcoder.bookstore.model.AuthorDetails;
import com.alexcoder.bookstore.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Primary

public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;


    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        if (authorDTO.getId() != null && authorRepository.existsById(authorDTO.getId())) {
            throw new IllegalArgumentException("Author with ID" + authorDTO.getId() + " already exists");
        }

        Author author = mapToEntity(authorDTO);
        Author savedAuthor = authorRepository.save(author);

        return mapToDTO(savedAuthor);
    }

    @Override
    public AuthorDTO getAuthorByID(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
        return mapToDTO(author);
    }

    @Override
    public List<AuthorDTO> getAllAuthors() {
        List<Author> author = authorRepository.findAll();
        return author.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
        if (authorDTO.getId() != null && !authorDTO.getId().equals(author.getId())
                && authorRepository.existsById(authorDTO.getId()))
                {
            throw new IllegalArgumentException("Cannot change author ID to " + authorDTO.getId() +
                    " as it belongs to another author");
        };
        
            
        author.setName(authorDTO.getName());
        author.setNationality(authorDTO.getNationality());
        author.setEmail(authorDTO.getEmail());
        author.setBiography(authorDTO.getBiography());
        author.setBirthDate(authorDTO.getBirthDate());

        Author updateAuthor = authorRepository.save(author);
        return mapToDTO(updateAuthor);

    }
    
    @Override
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
        authorRepository.delete(author);
    }

    @Override
    public List<AuthorDTO> searchAuthorsByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
        List<Author> author = authorRepository.findAllByNameIgnoreCase(name);
        return author.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AuthorDTO> searchAuthorsByNationality(String nationality) {
        if (nationality == null || nationality.isEmpty()) {
            throw new IllegalArgumentException("Nationality must be not null or empty");
        }

        List<Author> authors = authorRepository.findByNationality(nationality);
        return authors.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<String> getAuthorBiography(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Author id can't be empty");
        }
        Optional<String> bio = authorRepository.findBiographyById(id);

        return bio;
    }

    @Override
    public AuthorDetails getAuthorDetails(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Author id can't be empty");
        }

        AuthorDetails det = authorRepository.findDetailsById(id);
        if (det == null) {
            throw new ResourceNotFoundException("Author", "id", id);
        }

        return det;
        
    }



    





    private AuthorDTO mapToDTO(Author author) {
        return new AuthorDTO(
                author.getId(),
                author.getName(),
                author.getNationality(),
                author.getEmail(),
                author.getBiography(),
                author.getBirthDate());

    }

    private Author mapToEntity(AuthorDTO authorDTO) {
        return new Author(
            authorDTO.getId(),
            authorDTO.getName(),
            authorDTO.getBiography(),
            authorDTO.getBirthDate(),
            authorDTO.getEmail(),
            authorDTO.getNationality(),
            null,
            null
        );
    }

}
