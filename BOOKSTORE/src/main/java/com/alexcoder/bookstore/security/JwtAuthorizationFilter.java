package com.alexcoder.bookstore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;

/*import jakarta.servlet.FilterChain;
does two things:

Declares the type you’ll use
It tells the compiler that whenever you refer to FilterChain in your code, you mean the jakarta.servlet.FilterChain interface.

Brings in the Servlet API interface

What it is: an interface defined in the Jakarta EE (formerly Java EE) Servlet specification.

Purpose: it represents the remaining sequence of filters (and ultimately the target servlet or controller) that a request should be passed through.

How you use it in a filter
When you write a custom filter (or override a method in a Spring Security filter), you typically get:


protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain chain)
        throws ServletException, IOException {
    // ... your pre-processing logic ...

    // Pass control to the next filter or the target resource
    chain.doFilter(request, response);

    // ... your post-processing logic (if any) ...
}
Here, chain.doFilter(request, response):

Invokes the next filter in the chain (if any), or

If there are no more filters, dispatches the request to the eventual servlet or controller method.

Why “jakarta.servlet” and not “javax.servlet”?
As of Jakarta EE 9, the package namespace moved from javax.servlet → jakarta.servlet.

Modern Spring Boot starters target the Jakarta EE APIs, so you import from jakarta.servlet.

So in your filter classes—whether extending OncePerRequestFilter, UsernamePasswordAuthenticationFilter, 
or implementing javax.servlet.Filter—you’ll import and use that interface to hand off processing down the chain. */

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    private final JwtTokenProvider tokenProvider;

    public jwtAuthorizationFilter(JwtTokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws IOException, ServletException {
        // Get authorization header
        String header = request.getHeader(HEADER_STRING);
        // Check for token presence
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFlter(request, response);
            return;
        }

        // Extract token (remove "Bearer " prefix)
        String token = header.replace(TOKEN_PREFIX, "");

        // Validate token and set up security context
        if (tokenProvider.validateToken(token)) {
            Authentication auth = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // Continue with the filter chain
        chain.doFilter(request, response);

        /*
         * chain.doFilter(request, response);
         * is the mechanism by which your filter hands control over to the next element
         * in the processing pipeline. Here’s exactly what happens:
         * 
         * 1. What chain Is
         * The chain parameter is a jakarta.servlet.FilterChain object.
         * 
         * It represents the ordered list of all filters (plus the target servlet or
         * controller dispatcher) that should handle this request.
         * 
         * 2. What doFilter() Does
         * When you invoke
         * 
         * 
         * chain.doFilter(request, response);
         * you are saying:
         * 
         * “I’m done with my part of processing for this request—now please invoke the
         * next filter in line (or, if there are no more filters, dispatch to the
         * servlet/controller that actually generates the response).”
         * 
         * 3. Typical Filter Structure
         *
         * public void doFilter(ServletRequest req, ServletResponse res, FilterChain
         * chain)
         * throws IOException, ServletException {
         * // ——— Pre-processing logic ———
         * // e.g. logging, auth checks, header modifications
         * 
         * // Pass to next filter or servlet
         * chain.doFilter(req, res);
         * 
         * // ——— Post-processing logic ———
         * // e.g. response header tweaks, cleanup, logging
         * }
         * Before chain.doFilter(...): you can examine or wrap the request.
         * 
         * After chain.doFilter(...): you can inspect or modify the response generated
         * downstream.
         * 
         * 4. In Spring Security’s Filters
         * In JwtAuthorizationFilter.doFilterInternal(...), you check the JWT, set the
         * SecurityContext, then call chain.doFilter(...)—allowing the request to
         * proceed to other security filters and eventually to your controllers.
         * 
         * In UsernamePasswordAuthenticationFilter, Spring itself calls
         * chain.doFilter(...) internally once authentication is complete (you don’t
         * normally call it yourself in your overrides).
         * 
         * 5. Why It Matters
         * Short-circuiting: if you choose not to call chain.doFilter(...) (for example,
         * in an authentication filter after successful login where you immediately
         * write a response), the request stops there. No downstream filters or
         * controllers run.
         * 
         * Continuing: by always calling chain.doFilter(...), your filter ensures that
         * everyone else still gets a chance to process the request or response.
         * 
         * Demo Scenario
         * Request arrives at CorsFilter.doFilter(...). It adds headers, then calls
         * chain.doFilter(...).
         * 
         * That invokes JwtAuthorizationFilter.doFilterInternal(...), which validates
         * JWT and sets the security context, then calls chain.doFilter(...).
         * 
         * Next in line might be a logging filter—it logs the request, calls
         * chain.doFilter(...), and then logs the response afterward.
         * 
         * Finally, the DispatcherServlet is invoked, routing to your controller.
         * 
         * Each filter in turn either wraps the call to chain.doFilter(...) (pre- and
         * post-logic) or, in rare cases, stops the chain by not calling it (e.g.
         * returning an error response immediately). That’s how the servlet/filter
         * pipeline is constructed and executed.
         */
    }

}

/*
 * Class Overview
 * public class JwtAuthorizationFilter extends OncePerRequestFilter
 * This class intercepts every incoming HTTP request once (due to
 * OncePerRequestFilter).
 * 
 * Its job is to:
 * 
 * Check for a JWT token in the Authorization header.
 * 
 * Validate the token.
 * 
 * If valid, extract user details and set them in Spring Security’s context.
 * 
 * This allows Spring Security to treat the user as authenticated in the current
 * request context.
 * 
 * Main Responsibility
 * This filter handles authorization (not login), by checking JWTs on every
 * request after login.
 * 
 * Key Components
 * Fields:
 * java
 * Copy
 * Edit
 * private static final String TOKEN_PREFIX = "Bearer ";
 * private static final String HEADER_STRING = "Authorization";
 * private final JwtTokenProvider tokenProvider;
 * TOKEN_PREFIX: Expected start of the JWT token in the Authorization header.
 * 
 * HEADER_STRING: The header key we look for (Authorization).
 * 
 * tokenProvider: Your helper class to:
 * 
 * Validate the token
 * 
 * Extract user identity and authorities from it
 * 
 * Main Method: doFilterInternal(...)
 * This is the method Spring Security calls for every request.
 * 
 * Step-by-step:
 * 
 * String header = request.getHeader(HEADER_STRING);
 * Extracts the Authorization header from the request.
 * 
 * 
 * if (header == null || !header.startsWith(TOKEN_PREFIX)) {
 * chain.doFilter(request, response);
 * return;
 * }
 * If the token is missing or doesn’t start with Bearer , we skip authorization
 * and let the request pass through.
 * 
 * 
 * String token = header.replace(TOKEN_PREFIX, "");
 * Removes the "Bearer " prefix to get the raw JWT string.
 * 
 * 
 * if (tokenProvider.validateToken(token)) {
 * Authentication auth = tokenProvider.getAuthentication(token);
 * SecurityContextHolder.getContext().setAuthentication(auth);
 * }
 * If the JWT is valid:
 * 
 * Extracts an Authentication object (e.g. with username and roles).
 * 
 * Sets it in Spring Security’s context for the current thread/request.
 * 
 * Now Spring knows the user is authenticated and can apply authorization rules
 * (@PreAuthorize, etc.).
 * 
 * 
 * chain.doFilter(request, response);
 * Proceeds to the next filter in the chain, or to the controller.
 * 
 * How It Works in Context
 * User logs in → gets JWT from JwtAuthenticationFilter.
 * 
 * Client sends JWT in every request:
 * 
 * 
 * Authorization: Bearer <jwt-token>
 * This filter (JwtAuthorizationFilter) catches the request, reads the JWT, and
 * sets up the authenticated user.
 * 
 * Security context is now aware of the user, and access to secure endpoints is
 * granted accordingly.
 * 
 * Where You Register It
 * You’ll typically register this filter after the login filter in your
 * SecurityFilterChain like so:
 * 
 * http
 * .addFilterBefore(jwtAuthorizationFilter,
 * UsernamePasswordAuthenticationFilter.class);
 * Summary
 * Part Role
 * OncePerRequestFilter Ensures this filter runs only once per request
 * Authorization header Where JWT token is expected
 * JwtTokenProvider Validates token & builds Authentication object
 * SecurityContextHolder Stores the authenticated user for the rest of the
 * request
 * chain.doFilter(...) Proceeds with request after processing
 */

/*
 * Imagine a client calling a protected endpoint—say, GET /api/books—after
 * having logged in and received a JWT. Here’s a concrete walk-through with demo
 * data, showing how your JwtAuthorizationFilter passes control along the filter
 * chain and eventually to your controller:
 * 
 * 1. Client Request
 * 
 * GET /api/books HTTP/1.1
 * Host: api.example.com
 * Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.
 * eyJzdWIiOiJhbGljZSIsImlhdCI6MTcwMDkyMDAwMCwiZXhwIjoxNzAwOTIzNjAwfQ.4-MK5...
 * Accept: application/json
 * Header
 * 
 * Authorization header contains the token issued to user “alice”.
 * 
 * Body
 * 
 * None (GET request).
 * 
 * 2. Entry to JwtAuthorizationFilter.doFilterInternal(...)
 * 
 * String header = request.getHeader("Authorization");
 * // header = "Bearer eyJhbGciOiJIUzUxMiJ9.…"
 * 
 * if (header == null || !header.startsWith("Bearer ")) {
 * chain.doFilter(request, response);
 * return;
 * }
 * 
 * String token = header.replace("Bearer ", "");
 * // token = "eyJhbGciOiJIUzUxMiJ9.…"
 * Because the header is present and well-formed, the filter proceeds to
 * validation.
 * 
 * 3. Token Validation & Context Population
 * 
 * if (tokenProvider.validateToken(token)) {
 * Authentication auth = tokenProvider.getAuthentication(token);
 * SecurityContextHolder.getContext().setAuthentication(auth);
 * }
 * validateToken(...)
 * 
 * Checks signature and expiry—returns true.
 * 
 * getAuthentication(...)
 * 
 * Loads user “alice” from the token’s subject.
 * 
 * Builds an authenticated UsernamePasswordAuthenticationToken with ROLE_USER.
 * 
 * Security Context
 * 
 * Now holds the identity and roles of “alice” for the duration of this request.
 * 
 * 4. Passing to the Next Filter
 * 
 * chain.doFilter(request, response);
 * At this point, control goes to the next filter in the chain. For example,
 * suppose your security chain is:
 * 
 * CORS filter
 * 
 * JwtAuthenticationFilter (login)
 * 
 * JwtAuthorizationFilter (this filter)
 * 
 * UsernamePasswordAuthorizationFilter (checks method‐level @PreAuthorize)
 * 
 * OncePerRequestFilter–based logging filter
 * 
 * Dispatcher servlet → controller
 * 
 * So after step 3, it enters the method-security filter which enforces
 * any @PreAuthorize rules. Since our user has ROLE_USER, they pass.
 * 
 * 5. Controller Invocation
 * Finally, after all filters:
 * 
 * 
 * @RestController
 * 
 * @RequestMapping("/api/books")
 * public class BookController {
 * 
 * @GetMapping
 * public List<Book> listBooks() {
 * // Because SecurityContextHolder has “alice” authenticated:
 * return List.of(
 * new Book("1984", "George Orwell"),
 * new Book("Brave New World", "Aldous Huxley")
 * );
 * }
 * }
 * Spring MVC Dispatcher
 * 
 * Maps the request to BookController.listBooks().
 * 
 * Response
 * 
 * 
 * [
 * { "title": "1984", "author": "George Orwell" },
 * { "title": "Brave New World", "author": "Aldous Huxley" }
 * ]
 * Summary of the Flow
 * Step Who/What
 * 1. Client sends GET /api/books + JWT header Client
 * 2. JwtAuthorizationFilter reads header Your filter
 * 3. Validates token & sets SecurityContext JwtTokenProvider +
 * SecurityContextHolder
 * 4. chain.doFilter(...) → next security filter Next filters in chain
 * 5. Dispatcher → BookController returns data Spring MVC controller
 * 
 * Because chain.doFilter(request, response) simply hands off to the next
 * component, your authorization filter never “swallows” the request—it just
 * injects authentication information, then lets the rest of the pipeline run as
 * if the user had been authenticated by any other means.
 */
