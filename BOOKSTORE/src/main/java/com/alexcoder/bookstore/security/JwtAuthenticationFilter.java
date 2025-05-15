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

}





