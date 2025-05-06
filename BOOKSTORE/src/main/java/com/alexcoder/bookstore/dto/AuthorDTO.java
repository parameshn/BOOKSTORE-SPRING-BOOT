package com.alexcoder.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDate;



@Data
@NoArgsConstructor
@AllArgsConstructor

public class AuthorDTO {

    private Long id;

    @NotBlank(message = "Name must be provided")
    @Size(max = 50,message = "Author name cannot exceed 50 characters")
    private String name;

    @Size(max = 50, message = "Authors Nationality cannot exceed 50 characters")
    private String nationality;

    @Size(max = 100, message = "Authors Email cannot exceed 100 characters")
    private String email;

    @Size(max = 500, message = "Authors Email cannot exceed 500 characters")
    private String biography;


    @NotNull(message = "Birth date is required")
     private LocalDate birthDate;
    
     @AssertTrue(message = "Birth year must be between 1000 and 9999")
     private boolean isBirthYearValid() {
         if (birthDate == null)
             return false;
         int year = birthDate.getYear();
         return year >= 1000 && year <= 9999;
     }


}
