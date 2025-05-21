package com.alexcoder.bookstore.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterRequest {

    @NotBlank(message = "Username cannot be blank")
    @size(min = 3,max = 20,message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email can't be blank")
    @Email(message = "Please provide a valid email")
    private String Email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6,message = "Password must be at least 6 characters")
    private String password;
}
