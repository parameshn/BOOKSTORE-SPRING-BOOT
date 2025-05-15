package com.alexcoder.bookstore.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.Data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.RuntimeErrorException;

/*com
└── fasterxml
    └── jackson
        └── databind
            └── ObjectMapper

jakarta
└── servlet
    ├── FilterChain
    └── http
        ├── HttpServletRequest
        └── HttpServletResponse

org
└── springframework
    └── security
        ├── authentication
        │   ├── AuthenticationManager
        │   ├── UsernamePasswordAuthenticationToken
        │   └── UsernamePasswordAuthenticationFilter
        ├── core
        │   ├── Authentication
        │   └── AuthenticationException
        └── web
            └── authentication
                └── UsernamePasswordAuthenticationFilter
 */






public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    /*
     * HTTP POST /api/auth/login ──> JwtAuthenticationFilter.attemptAuthentication()
     * │
     * ├─[1] Read JSON → LoginRequest(username, password)
     * │
     * ├─[2] new UsernamePasswordAuthenticationToken(username, password)
     * │
     * └─[3] authenticationManager.authenticate(token)
     * ↓
     * ProviderManager (AuthenticationManager)
     * ↓
     * DaoAuthenticationProvider.authenticate(token)
     * ├─ loadUserByUsername(username) → UserDetails
     * └─ passwordEncoder.matches(raw, encoded)
     * ↓
     * → returns authenticated
     * UsernamePasswordAuthenticationToken(principal=UserDetails, authorities)
     * │
     * └─> JwtAuthenticationFilter.successfulAuthentication() → issue JWT
     */

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;

        // Set the URL for form login
        setFilterProcessesUrl("api/auth/login");

        /*
         * authenticationManager: the Spring bean that performs username/password checks
         * (via your UserDetailsService + PasswordEncoder).
         * 
         * tokenProvider: your custom JwtTokenProvider that issues tokens.
         * 
         * setFilterProcessesUrl(...): overrides the default form-login endpoint.
         */

        /*
         * How is authenticationManager combining UserDetailsService + PasswordEncoder?
         * It delegates to DaoAuthenticationProvider, which is configured with:
         * 
         * Your UserDetailsService (to load the user from DB)
         * 
         * Your PasswordEncoder (to validate the password)
         * 
         * You don’t call either manually — Spring handles it during the authenticate()
         * call.
        
         * UsernamePasswordAuthenticationToken
         * ↓
         * AuthenticationManager.authenticate()
         * ↓
         * DaoAuthenticationProvider.authenticate()
         * ↓
         * UserDetailsService.loadUserByUsername()
         * ↓
         * PasswordEncoder.matches(raw, encoded)
         * 
         */
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            /*LoginRequest
            What it is: a plain old Java object (POJO) that you define in your code, typically something like:
            
            public class LoginRequest {
            private String username;
            private String password;
            // getters & setters
            }
            Why you need it: Jackson (the ObjectMapper) needs a target type to deserialize the incoming JSON into. 
            You want a Java object with getUsername() and getPassword() methods so you can work with them in your code.
            
            2. LoginRequest.class
            What this means: the Java class literal for your LoginRequest type.
            
            Why it’s there: you’re telling Jackson, “Take the JSON from the input stream, and convert it into an instance of LoginRequest.”
            
            3. request.getInputStream()
            What it returns: a ServletInputStream (a subtype of InputStream) that gives you the raw bytes of the already-opened HTTP request body.
            
            Important detail: you are not “establishing” the TCP or HTTP connection here—that’s done by the servlet container (Tomcat, Jetty, etc.) before your filter or controller ever runs. By the time you call getInputStream(), the container has already parsed the HTTP headers and is handing you the body to read.
            
            4. new ObjectMapper().readValue(...)
            What it does:
            
            Reads all bytes from the InputStream
            
            Parses them as JSON
            
            Uses reflection + your getters/setters to populate a new LoginRequest object
            
            Putting it all together:
            Container accepts POST /api/auth/login with JSON body.
            
            In your filter, you call request.getInputStream() to read that JSON.
            
            You pass the stream plus the target type (LoginRequest.class) into Jackson.
            
            Jackson hands you back a populated LoginRequest instance, e.g.
            
            
            loginRequest.getUsername(); // "alice"
            loginRequest.getPassword(); // "pass"
            That’s it—no extra connection code needed; you’re simply reading the request payload that’s already been
            delivered to your filter. */

            /*ServletInputStream in = request.getInputStream();
            you’re asking the servlet container (e.g. Tomcat, Jetty) for a stream of the raw bytes that make up the body 
            of the HTTP request. In other words:
            
            The client sent you an HTTP request:
            
            POST /api/auth/login HTTP/1.1
            Host: example.com
            Content-Type: application/json
            Content-Length: 35
            
            {"username":"alice","password":"pass"}
            By the time your filter or controller runs, the container has already:
            
            Read the start-line (POST /api/auth/login…)
            
            Parsed all the headers (Content-Type, etc.)
            
            Opened a request body stream for you
            
            getInputStream() gives you that open stream. You can then read from it—byte by byte or
            all at once—to obtain the JSON (or form-data, or whatever payload) the client sent.
            
            Why use getInputStream()?
            It’s the only way to read a request body in a servlet or filter.
            
            You can pass it directly to libraries (like Jackson’s ObjectMapper) that know how to 
            read from an InputStream and parse it into Java objects.
            
            Unlike getParameter(), which only works for form-encoded data (application/x-www-form-urlencoded or 
            multipart/form-data), getInputStream() works for any content type, including raw JSON, XML, binary, etc.
            */

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword);

            /*Exactly—both HttpServletRequest and HttpServletResponse are interfaces in the Jakarta Servlet API 
            (formerly under javax.servlet.http) that define the contract for accessing and manipulating an HTTP
             request and response. In a servlet filter or controller you’re handed concrete implementations of these interfaces
             by the servlet container (e.g., Tomcat, Jetty), and you interact with them entirely through their methods 
             
            
             HttpServletRequest lets you read all aspects of the incoming request:
            
             Headers: getHeader("User-Agent"), getHeaders("Accept")
            
             Parameters: getParameter("id"), getParameterValues("tags")
            
             Body: getInputStream() or getReader() for reading JSON/XML payloads
            
             Session: getSession(), getSession(false)
            
             URI/Method: getRequestURI(), getMethod(), getQueryString()
            
             HttpServletResponse lets you shape what goes back to the client:
            
             Status: setStatus(200), or helper constants like SC_CREATED
            
             Headers: setHeader("Location", "/new-resource"), addHeader(...)
            
             Content Type: setContentType("application/json")
            
             Body: getWriter().write(...), or getOutputStream() for binary data
             */

            // 3. Delegate to AuthenticationManager (your UserDetailsService + PasswordEncoder)
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeErrorException("Failed to parse authentication request", e);
        }
    }
    

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException {
        String token = tokenProvider.generateToken(authResult);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", token);
        responseBody.put("username", authResult.getName());

        new ObjectMapper().writeValue(response.getWriter(), responseBody);
        /*
         * Using new ObjectMapper() works, but it's more efficient to reuse a singleton
         * instance of ObjectMapper (can be injected via Spring Boot as a @Bean).
         */

        /*
         * You're creating a new instance of ObjectMapper on the fly and immediately
         * using it to write JSON into the HTTP response.
         * 
         * The line explained again:
         *
         * new ObjectMapper().writeValue(response.getWriter(), responseBody);
         * This does three things in one go:
         * 
         * Creates a new ObjectMapper:
         * 
         *
         * new ObjectMapper()
         * Serializes responseBody (a Map<String, Object>) into JSON:
        
         * .writeValue(...)
         * Writes the resulting JSON directly into the output stream:
         *
         * response.getWriter()
         * So it is a one-time-use, throwaway object that you're not storing in a
         * variable. This is valid and works fine — especially in small or one-off
         * scenarios.
         * 
         *  But as we discussed earlier...
         * It's better practice to reuse an ObjectMapper, like this:
         * 
         * @Autowired
         * private ObjectMapper objectMapper;
         * 
         * objectMapper.writeValue(response.getWriter(), responseBody);
         * This avoids the overhead of repeatedly creating and configuring ObjectMapper
         * instances.
         * 
         *  Summary
         * Approach Behavior
         * new ObjectMapper().writeValue(...) Works, but creates a new object every time
         * 
         * @Autowired ObjectMapper Reuses a singleton bean managed by Spring (preferred)
         */
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }

    /*
     * Given: A successful login
     * Let's say the user "alice" logs in with password "password123".
     * 
     * Spring Security uses:
     * 
     * Your UserDetailsService to load the user by username.
     * 
     * Your PasswordEncoder to verify the password.
     * 
     * The result is an authenticated Authentication object, called authResult.
     * 
     *  What is authResult?
     * authResult is typically an instance of:
     * 
     *
     * UsernamePasswordAuthenticationToken
     * But now it's in authenticated state, holding the user's identity and
     * authorities.
     * 
     *  Sample demo authResult (Java-side)
     * 
     * UsernamePasswordAuthenticationToken authResult = new
     * UsernamePasswordAuthenticationToken(
     * new org.springframework.security.core.userdetails.User(
     * "alice",
     * "$2a$10$...hashedPasswordHere...", // already hashed password
     * List.of(new SimpleGrantedAuthority("ROLE_USER"))
     * ),
     * null,
     * List.of(new SimpleGrantedAuthority("ROLE_USER"))
     * );
     * So:
     * 
     * Property Value
     * authResult.getName() "alice"
     * authResult.getPrincipal() User object (from UserDetailsService)
     * authResult.getAuthorities() [ROLE_USER]
     * authResult.isAuthenticated() true
     * authResult.getCredentials() null (password is cleared after authentication)
     * 
     * JSON-style representation of authResult.getPrincipal()
     * 
     * {
     * "username": "alice",
     * "password": "$2a$10$encryptedPassword...",
     * "authorities": [
     * {
     * "authority": "ROLE_USER"
     * }
     * ],
     * "accountNonExpired": true,
     * "accountNonLocked": true,
     * "credentialsNonExpired": true,
     * "enabled": true
     * }
     * This is what the internal UserDetails object (authResult.getPrincipal())
     * looks like.
     * 
     *  Use in successfulAuthentication(...)
     * When you write:
     * 
    
     * responseBody.put("username", authResult.getName());
     * You’re pulling "alice" from the Authentication object.
     * 
     * If you want to extract roles too:
     * 
    
     * List<String> roles = authResult.getAuthorities().stream()
     * .map(GrantedAuthority::getAuthority)
     * .collect(Collectors.toList());
     * This will give you:
     *
     * ["ROLE_USER"]
     */
    
     /*
      * Okay, here is a rewritten explanation of the `JwtAuthenticationFilter` based
      * on your provided text, aiming for clarity and conciseness:
      * 
      * ### JwtAuthenticationFilter: Customizing Spring Security for JWT Login
      * 
      * This document describes a custom Spring Security filter,
      * `JwtAuthenticationFilter`, designed to handle JSON-based login requests and
      * issue JSON Web Tokens (JWTs) upon successful authentication. It extends
      * Spring Security's standard `UsernamePasswordAuthenticationFilter`,
      * integrating seamlessly into the existing security filter chain.
      ** 
      * Core Functionality:**
      * 
      * The `JwtAuthenticationFilter` intercepts login requests (specifically POST
      * requests to `/api/auth/login`), extracts username and password from a JSON
      * request body, authenticates the user via Spring Security's
      * `AuthenticationManager`, and on success, generates and returns a JWT to the
      * client.
      ** 
      * Key Components:**
      * 
      * 1. **Class Declaration:**
      * 
      * ```java
      * public class JwtAuthenticationFilter extends
      * UsernamePasswordAuthenticationFilter
      * ```
      * 
      * By extending `UsernamePasswordAuthenticationFilter`, this class leverages
      * Spring Security's built-in form-login processing while providing hooks to
      * customize behavior for:
      * 
      * Parsing credentials (`attemptAuthentication`).
      * Handling successful authentication (`successfulAuthentication`).
      * Handling failed authentication (`unsuccessfulAuthentication` - optional).
      * 
      * 2. **Key Fields:**
      * 
      * ```java
      * private final JwtTokenProvider tokenProvider;
      * private final ObjectMapper objectMapper;
      * ```
      * 
      * `tokenProvider`: An instance of your custom class responsible for creating
      * (and validating) JWTs.
      * `objectMapper`: An instance of Jackson's `ObjectMapper`, used for converting
      * JSON request bodies into Java objects and Java objects into JSON response
      * bodies.
      * 
      * 3. **Constructor:**
      * 
      * ```java
      * public JwtAuthenticationFilter(AuthenticationManager authManager,
      * JwtTokenProvider tokenProvider,
      * ObjectMapper objectMapper) {
      * super.setAuthenticationManager(authManager);
      * this.tokenProvider = tokenProvider;
      * this.objectMapper = objectMapper;
      * setFilterProcessesUrl("/api/auth/login");
      * }
      * ```
      * 
      * The constructor injects necessary dependencies:
      * 
      * `AuthenticationManager`: Spring Security's core component for handling
      * authentication requests (typically configured with your `UserDetailsService`
      * and `PasswordEncoder`).
      * `JwtTokenProvider`: Your JWT handling logic.
      * `ObjectMapper`: For JSON processing.
      * It also explicitly sets the URL this filter will process to
      * `/api/auth/login`, overriding the default `/login`.
      * 
      * 4. **`attemptAuthentication(...)` Method:**
      * 
      * ```java
      * 
      * @Override
      * public Authentication attemptAuthentication(HttpServletRequest request,
      * HttpServletResponse response)
      * throws AuthenticationException {
      * // 1. Read JSON credentials into a LoginRequest POJO
      * // 2. Build an unauthenticated UsernamePasswordAuthenticationToken
      * // 3. Delegate to authManager.authenticate(...) to run through
      * DaoAuthenticationProvider
      * // ... implementation details ...
      * }
      * ```
      * 
      * This is the entry point for authentication. It's responsible for:
      * 
      * Reading the incoming HTTP request body, expecting a JSON structure containing
      * username and password.
      * Mapping the JSON data to a `LoginRequest` DTO.
      * Creating an `UsernamePasswordAuthenticationToken` (initially unauthenticated)
      * from the extracted credentials.
      * Passing this token to the injected `AuthenticationManager` to perform the
      * actual authentication (which typically involves loading user details and
      * verifying the password).
      * 
      * 5. **`successfulAuthentication(...)` Method:**
      * 
      * ```java
      * 
      * @Override
      * protected void successfulAuthentication(HttpServletRequest request,
      * HttpServletResponse response,
      * FilterChain chain,
      * Authentication authResult)
      * throws IOException {
      * // 1. Generate a JWT (via tokenProvider.generateToken(authResult))
      * // 2. Build a JSON response with token & username
      * // 3. Write that JSON to response.getWriter()
      * // ... implementation details ...
      * }
      * ```
      * 
      * This method is called when `attemptAuthentication` successfully authenticates
      * the user. Its purpose is to:
      * 
      * Generate a JWT using the `tokenProvider`, typically embedding user details or
      * roles from the authenticated `Authentication` object (`authResult`).
      * Construct a response (usually a JSON object) containing the generated JWT and
      * potentially other user information (like the username).
      * Write this JSON response back to the client.
      * Crucially, this method typically *does not* call `chain.doFilter()`,
      * terminating the filter chain for this request as the authentication process
      * is complete and a response has been sent.
      * 
      * 6. **`(Optional) unsuccessfulAuthentication(...)` Method:**
      * While not shown in the snippet, you can override this method to handle
      * authentication failures. This allows you to:
      * 
      * Return a specific HTTP status code (e.g., 401 Unauthorized).
      * Provide a custom error message in the response body.
      * Log failed login attempts.
      * 
      * 7. **Inner `LoginRequest` DTO:**
      * 
      * ```java
      * 
      * @Data // Lombok annotation
      * private static class LoginRequest {
      * private String username;
      * private String password;
      * }
      * ```
      * 
      * A simple static inner class representing the expected structure of the
      * incoming JSON login request body. `ObjectMapper` uses this class to
      * deserialize the JSON.
      ** 
      * How it Fits in the Security Chain:**
      * 
      * When a POST request is sent to `/api/auth/login`:
      * 
      * 1. The request enters the Spring Security filter chain.
      * 2. Your configured `JwtAuthenticationFilter` intercepts the request because
      * its `filterProcessesUrl` matches.
      * 3. `attemptAuthentication()` is invoked, processing the JSON body and
      * delegating to the `AuthenticationManager`.
      * 4. If authentication succeeds, `successfulAuthentication()` is called,
      * generating the JWT and sending it back to the client in the response body.
      * 5. If authentication fails (and `unsuccessfulAuthentication` is overridden),
      * that method is called to handle the failure response.
      ** 
      * Benefits of Using This Class:**
      * 
      * **Customizable Login Endpoint:** Allows using a specific URL
      * (`/api/auth/login`) different from the default `/login`.
      * **JSON Support:** Handles login credentials submitted in a JSON request body,
      * suitable for modern frontends or APIs.
      * **JWT Issuance:** Integrates JWT generation directly into the authentication
      * success flow.
      * **Decoupling:** Separates authentication logic from your application's
      * controller layer.
      * **Leverages Spring Security:** Builds upon the robust and well-tested
      * `AuthenticationManager` and provider architecture for credential validation.
      */

}





