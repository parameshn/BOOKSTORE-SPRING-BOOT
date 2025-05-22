package com.alexcoder.bookstore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * This filter adds the CSRF token to the response headers.
 * It's useful when you have enabled CSRF protection and need to send the token
 * to the client.
 * For a SPA that uses JWT authentication, you might disable CSRF entirely (as
 * we did in SecurityConfig),
 * but this can be useful if you decide to enable it.
 */

public class CsrfTokenResponseFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrf != null) {
            response.setHeader("X_CSRF_HEADER", csrf.getHeaderName());
            response.setHeader("X_CSRF_PARAM", csrf.getParameterName());
            response.setHeader("X_CSRF_TOKEN", csrf.getToken());
        }
        filterChain.doFilter(request, response);
    }
}
