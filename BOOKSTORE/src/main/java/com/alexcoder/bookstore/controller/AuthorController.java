package com.alexcoder.bookstore.controller;

import com.alexcoder.bookstore.dto.AuthorDTO;
import com.alexcoder.bookstore.model.AuthorDetails;
import com.alexcoder.bookstore.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO authorDTO) {
        AuthorDTO createdAuthor = authorService.createAuthor(authorDTO);
        return new ResponseEntity<>(createdAuthor, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<AuthorDTO> authors = authorService.getAllAuthors();
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable("id") Long id) {
        AuthorDTO author = authorService.getAuthorByID(id);
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable("id") Long id,
            @Valid @RequestBody AuthorDTO authorDTO) {
        AuthorDTO author = authorService.updateAuthor(id, authorDTO);
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable("id") Long id) {
        authorService.deleteAuthor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AuthorDTO>> searchAuthorsByName(@RequestParam(name = "name") String name) {
        List<AuthorDTO> authors = authorService.searchAuthorsByName(name);
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/search-by-nationality")
    public ResponseEntity<List<AuthorDTO>> searchAuthorsByNationality(@RequestParam(name = "nationality") String nationality) {
        List<AuthorDTO> authors = authorService.searchAuthorsByNationality(nationality);
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/{id}/biography")
    public ResponseEntity<String> getAuthorBiography(@PathVariable("id") Long id) {
        Optional<String> biography = authorService.getAuthorBiography(id);
        return biography
                .map(bio -> new ResponseEntity<>(bio, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<AuthorDetails> getAuthorDetails(@PathVariable("id") Long id) {
        AuthorDetails authorDetails = authorService.getAuthorDetails(id);
        return new ResponseEntity<>(authorDetails, HttpStatus.OK);
    }
}