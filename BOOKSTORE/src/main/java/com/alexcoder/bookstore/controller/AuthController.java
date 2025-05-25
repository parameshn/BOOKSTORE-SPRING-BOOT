package com.alexcoder.bookstore.controller;

import com.alexcoder.bookstore.dto.LoginRequest;
import com.alexcoder.bookstore.dto.RegisterRequest;
import com.alexcoder.bookstore.model.User;
import com.alexcoder.bookstore.repository.UserRepository;
import com.alexcoder.bookstore.security.JwtTokenProvider;
import jakarta.validation.Valid;

import org.apache.catalina.startup.PasswdUserDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/*@RestController is a Spring annotation used to define RESTful web services (APIs)
 * @RestController = @Controller + @ResponseBody
@Controller: Marks the class as a Spring MVC controller.
@ResponseBody: Ensures that return values of methods are written directly to the HTTP response as JSON or XML, instead of rendering a view.
 */

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;


    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
            PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Check if username is already taken
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Username is already taken");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Email is already in use");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRoles(Collections.singleton("USER"));

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
            loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        /*
         * The Cornerstone of Authentication:
         * SecurityContextHolder.getContext().setAuthentication(authentication);
         * This single line of code is pivotal in Spring Security; it's the declaration
         * that an identity has been successfully verified for the current request.
         * 
         * Java
         * 
         * SecurityContextHolder.getContext().setAuthentication(authentication);
         * What It Does
         * At its core, this statement takes an Authentication object (which
         * encapsulates the user's principal, credentials, and authorities/roles) and
         * places it into Spring Security's SecurityContext. This SecurityContext is
         * then stored in a ThreadLocal specific to the current executing request
         * thread.
         * 
         * Why It's Indispensable
         * Spring Security operates on the principle that once a user is authenticated,
         * their details should be easily accessible throughout the entire lifespan of
         * that specific HTTP request without needing to re-authenticate or pass objects
         * around manually.
         * 
         * By setting the Authentication object in the SecurityContextHolder:
         * 
         * Global Accessibility: Any component within the same request lifecycle (e.g.,
         * other security filters, HandlerInterceptors, controllers, services,
         * repositories) can now effortlessly retrieve the authenticated user's details.
         * Authorization Decisions: Subsequent authorization mechanisms
         * (like @PreAuthorize annotations or URL-based access rules) will consult the
         * SecurityContextHolder to determine if the current user has the necessary
         * permissions to access a resource.
         * Thread Safety: Leveraging ThreadLocal ensures that the user's authentication
         * context is isolated to the specific request thread, preventing conflicts in
         * multi-threaded environments.
         * Example Scenario
         * Imagine a user logs in via a POST /api/auth/login endpoint:
         * 
         * JSON
         * 
         * POST /api/auth/login
         * {
         * "username": "alice",
         * "password": "password123"
         * }
         * Your JwtAuthenticationFilter intercepts this request.
         * It extracts "alice" and "password123".
         * It then uses the AuthenticationManager to verify these credentials against
         * your UserDetailsService and PasswordEncoder.
         * Upon successful verification, an Authentication object is created for "alice"
         * (including her roles).
         * Crucially, your filter then executes:
         * SecurityContextHolder.getContext().setAuthentication(authentication);.
         * Now, if "alice" subsequently makes a request to a protected endpoint, say
         * /api/me, her identity is readily available:
         * 
         * Java
         * 
         * @RestController
         * public class UserController {
         * 
         * @GetMapping("/api/me")
         * public String getCurrentUser() {
         * // Retrieve the Authentication object from the SecurityContextHolder
         * Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         * return "You are logged in as: " + auth.getName(); // Returns
         * "You are logged in as: alice"
         * }
         * }
         * This seamless access is entirely due to the SecurityContextHolder being
         * populated earlier in the filter chain by your authentication process. It acts
         * as the central registry for the currently authenticated principal within a
         * request.
         */
        String jwt = tokenProvider.generateToken(authentication);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("tokenType", "Bearer");
        response.put("username", authentication.getName());

        return ResponseEntity.ok(response);

    }
}
