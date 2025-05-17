package com.alexcoder.bookstore.config;

import com.alexcoder.bookstore.security.JwtAuthenticationFilter;
import com.alexcoder.bookstore.security.JwtAuthorizationFilter;
import com.alexcoder.bookstore.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)


public class SecurityConfig {

    private final UserDetailService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class) // Grab the shared AuthenticationManagerBuilder
                // instance that Spring Security is using under
                // the covers
                .userDetailsService(userDetailsService) // Tell it how to load users (your UserDetailsService)…

                .passwordEncoder(passwordEncoder()) // …and how to verify passwords (your PasswordEncoder)

                .and() // “.and()” returns you back to the HttpSecurity context…

                .build(); // …and then build() produces a fully–initialized AuthenticationManager

    }
    
    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager,
                jwtTokenProvider);
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");

        http
                //.cors().configurationSource(corsConfigurationSource())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Modern CORS configuration
                .and()
                // .csrf().disable() // For API usage, often disabled. In production, consider enabling with proper configuration
                .csrf(csrf -> csrf.disable()) // Modern CSRF configuration
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll() // Only for development
                        .requestMatchers("/api/authors/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated())
                //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // .and()
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        //  http.headers().frameOptions().disable();
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
        /*
         * http: This is the HttpSecurity object, which is the main entry point for
         * configuring web-based security. You chain various methods onto it to define
         * security rules and filters.
         * 
         * .cors().configurationSource(corsConfigurationSource()).and():
         * 
         * .cors(): This enables CORS (Cross-Origin Resource Sharing) support.
         * .configurationSource(corsConfigurationSource()): This specifies the source
         * for CORS configuration. It delegates the CORS handling logic to the
         * corsConfigurationSource() bean you defined later in the SecurityConfig class.
         * This bean typically specifies which origins, methods, and headers are allowed
         * for cross-origin requests.
         * .and(): This method is used to "move up" in the configuration chain, allowing
         * you to configure other aspects of HttpSecurity after configuring CORS. (While
         * still commonly seen, in more modern Spring Security configurations, the use
         * of .and() is often replaced by just continuing the chain directly after the
         * previous configuration block, but it's perfectly valid here).
         * .csrf().disable():
         * 
         * .csrf(): Configures Cross-Site Request Forgery (CSRF) protection.
         * .disable(): This disables CSRF protection entirely.
         * Comment: // For API usage, often disabled. In production, consider enabling
         * with proper configuration. This comment correctly points out that for
         * stateless APIs (like one using JWTs where session cookies aren't the primary
         * authentication mechanism), CSRF protection is often disabled because the
         * typical CSRF attack vectors rely on the browser automatically sending session
         * cookies with requests. However, for applications using browser-based
         * sessions, CSRF protection is vital and should be carefully configured, not
         * simply disabled.
         * .authorizeHttpRequests(authorize -> authorize ...):
         * 
         * .authorizeHttpRequests(): This is where you define authorization rules for
         * specific HTTP requests based on their URL patterns and HTTP methods. The
         * lambda expression authorize -> authorize... provides a builder for
         * configuring these rules.
         * .requestMatchers("/api/auth/**").permitAll(): This rule specifies that any
         * request whose path starts with /api/auth/ (e.g., /api/auth/login,
         * /api/auth/register) should be permitted for all, meaning no authentication is
         * required to access these endpoints. This is typical for login and
         * registration endpoints.
         * .requestMatchers("/h2-console/**").permitAll(): This rule permits access to
         * any URL starting with /h2-console/. This is often used in development
         * environments to access the H2 database console via the browser, but it's
         * important to secure or disable this in production.
         * .requestMatchers("/api/authors/**").hasAnyRole("USER", "ADMIN"): This rule
         * requires that any request whose path starts with /api/authors/ must have at
         * least one of the roles "USER" or "ADMIN". Spring Security will check the
         * authenticated user's authorities (roles) against this requirement.
         * .anyRequest().authenticated(): This is a catch-all rule. It states that any
         * other request that hasn't been matched by the previous requestMatchers rules
         * must be authenticated. This means a user must be logged in (have an
         * Authentication object in the SecurityContextHolder) to access any other
         * endpoint.
         * .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
         * and():
         * 
         * .sessionManagement(): Configures session management.
         * .sessionCreationPolicy(SessionCreationPolicy.STATELESS): This is crucial for
         * JWT-based authentication. It tells Spring Security never to create an HTTP
         * session and not to use sessions to obtain the SecurityContext. Each request
         * must contain its own authentication information (the JWT). This makes the
         * application stateless, which is beneficial for scalability and simplifies API
         * design as the server doesn't need to maintain session state for each client.
         * .and(): Again, used to return to the HttpSecurity builder.
         * .addFilter(jwtAuthenticationFilter):
         * 
         * .addFilter(): This method adds a custom filter to a specific, known location
         * in the Spring Security filter chain. Spring Security attempts to place the
         * filter instance (jwtAuthenticationFilter) at a logical position based on its
         * type (UsernamePasswordAuthenticationFilter in this case, as
         * JwtAuthenticationFilter likely extends or replaces it).
         * jwtAuthenticationFilter: This is the JwtAuthenticationFilter instance you
         * created and configured with the login URL /api/auth/login. By adding it here,
         * you integrate your custom JWT-based authentication process into the Spring
         * Security flow. It effectively replaces or works alongside Spring's default
         * form-based authentication filter.
         * .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider),
         * UsernamePasswordAuthenticationFilter.class):
         * 
         * .addFilterBefore(): This method explicitly adds a custom filter before a
         * specified existing filter class in the chain.
         * new JwtAuthorizationFilter(jwtTokenProvider): This creates an instance of
         * your JwtAuthorizationFilter, providing it with the JwtTokenProvider.
         * UsernamePasswordAuthenticationFilter.class: This specifies the target filter
         * class. By placing your JwtAuthorizationFilter before
         * UsernamePasswordAuthenticationFilter, you ensure that on every request
         * (except those specifically permitted to all), your JwtAuthorizationFilter
         * runs first to check for a JWT in the header. If a JWT is found and is valid,
         * it populates the SecurityContextHolder. This happens before the
         * UsernamePasswordAuthenticationFilter (which is primarily for form login) or
         * other authentication mechanisms would typically run. For API calls with a
         * JWT, the JwtAuthorizationFilter handles the authentication, and subsequent
         * filters in the chain (like FilterSecurityInterceptor) can then rely on the
         * SecurityContextHolder being populated.
         * http.headers().frameOptions().disable();:
         * 
         * http.headers(): Configures security-related HTTP headers.
         * .frameOptions(): Configures the X-Frame-Options header, which helps prevent
         * Clickjacking attacks by controlling whether a page can be rendered in a
         * <frame>, <iframe>, <embed>, or <object>.
         * .disable(): Disables the X-Frame-Options header.
         * Comment: // Only for H2 console in development. This is necessary because the
         * H2 console often uses frames, and disabling the X-Frame-Options header
         * prevents browsers from blocking it due to this setting. Again, disabling
         * security headers should be done cautiously and typically only in development
         * or when absolutely necessary with a clear understanding of the security
         * implications.
         * return http.build();:
         * 
         * .build(): This finalizes the HttpSecurity configuration and builds the
         * SecurityFilterChain bean that Spring Security will use to protect your
         * application.
         * In summary, this configuration sets up a stateless API secured by JWTs. It
         * enables CORS, disables CSRF (common for stateless APIs), defines URL-based
         * access rules (permitting /api/auth/** and /h2-console/**, requiring USER or
         * ADMIN for /api/authors/**, and requiring authentication for everything else),
         * configures session creation to be stateless, and explicitly adds your custom
         * JwtAuthenticationFilter (for login) and JwtAuthorizationFilter (for checking
         * JWTs on subsequent requests) into the filter chain at specific positions. It
         * also disables frame options for the H2 console in development
         */
    }
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.satAllowedOrigins(List.of("http://localhost:3000", "https://yourdomain.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

        /*
         * corsConfigurationSource() bean definition you've provided. This method is
         * responsible for defining the Cross-Origin Resource Sharing (CORS) policy for
         * your application, which is essential when your frontend and backend are
         * served from different origins (domains, ports, or protocols).
         * 
         * Here's an explanation of each part:
         * 
         * @Bean public CorsConfigurationSource corsConfigurationSource():
         * 
         * @Bean: This annotation tells Spring that this method produces a bean to be
         * managed by the Spring application context. The bean's type is
         * CorsConfigurationSource.
         * CorsConfigurationSource: This is an interface in Spring Framework that
         * provides a way to determine the CORS configuration for a given request.
         * Spring Security uses this source to enforce CORS rules.
         * CorsConfiguration configuration = new CorsConfiguration();:
         * 
         * You create a new instance of CorsConfiguration. This object holds the
         * specific rules (allowed origins, methods, headers, etc.) that will define
         * your CORS policy.
         * configuration.setAllowedOrigins(List.of("http://localhost:3000",
         * "https://yourdomain.com"));:
         * 
         * setAllowedOrigins(): This is one of the most critical CORS settings. It
         * specifies which origins (combinations of protocol, domain, and port) are
         * permitted to access your backend resources via cross-origin requests.
         * List.of("http://localhost:3000", "https://yourdomain.com"): In this example,
         * you're allowing requests from a frontend running on http://localhost:3000
         * (common during local development) and from a production frontend at
         * https://yourdomain.com. Requests from any other origin will be blocked by the
         * CORS policy unless they match other configurations (like default browser
         * behavior for simple requests).
         * configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
         * "OPTIONS"));:
         * 
         * setAllowedMethods(): This specifies which HTTP methods are allowed for
         * cross-origin requests.
         * Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"): You are allowing
         * the standard CRUD methods (GET, POST, PUT, DELETE) and OPTIONS. The OPTIONS
         * method is used for CORS "preflight" requests, which browsers automatically
         * send before certain complex requests (like POST, PUT, DELETE, or requests
         * with custom headers) to check the server's CORS policy.
         * configuration.setAllowedHeaders(Arrays.asList("Authorization",
         * "Content-Type", "X-Requested-With"));:
         * 
         * setAllowedHeaders(): This specifies which HTTP headers clients are allowed to
         * send in a cross-origin request.
         * Arrays.asList("Authorization", "Content-Type", "X-Requested-With"): You are
         * explicitly allowing headers like Authorization (necessary for sending JWTs or
         * other credentials), Content-Type (needed for sending request bodies, like
         * JSON), and X-Requested-With (often sent by JavaScript frameworks). If a
         * client sends a header not in this list (and it's not a "simple header"), the
         * browser will perform a preflight request, and if the server's policy doesn't
         * allow that header, the request will fail.
         * configuration.setExposedHeaders(List.of("Authorization"));:
         * 
         * setExposedHeaders(): By default, browsers only expose a limited set of simple
         * response headers to the client-side JavaScript (like Content-Length,
         * Content-Type, etc.). If your server sends custom headers that your frontend
         * JavaScript needs to read (e.g., a JWT in the Authorization header after
         * login), you must explicitly expose them here.
         * List.of("Authorization"): This line allows your frontend JavaScript to read
         * the Authorization header from the response, which is useful if your login
         * endpoint returns the JWT in that header.
         * configuration.setAllowCredentials(true);:
         * 
         * setAllowCredentials(): Setting this to true indicates that the browser is
         * allowed to include credentials (like cookies, HTTP authentication, or
         * Authorization headers) in the cross-origin request. This is necessary if your
         * application relies on sending credentials for authentication or session
         * management.
         * configuration.setMaxAge(3600L);:
         * 
         * setMaxAge(): This sets the maximum time (in seconds) for which the results of
         * a CORS preflight request can be cached by the browser.
         * 3600L: Here, it's set to 3600 seconds (1 hour). This means the browser will
         * cache the CORS policy for an hour, reducing the number of OPTIONS preflight
         * requests for subsequent identical requests within that time frame, improving
         * performance.
         * UrlBasedCorsConfigurationSource source = new
         * UrlBasedCorsConfigurationSource();:
         * 
         * You create an instance of UrlBasedCorsConfigurationSource. This
         * implementation of CorsConfigurationSource allows you to apply different CORS
         * configurations based on the request URL path.
         * source.registerCorsConfiguration("/**", configuration);:
         * 
         * registerCorsConfiguration(pathPattern, config): This method registers the
         * previously created CorsConfiguration (configuration) to apply to requests
         * matching the specified path pattern ("/**").
         * "/**": This path pattern means the configuration will apply to all paths in
         * your application.
         * return source;:
         * 
         * The method returns the configured UrlBasedCorsConfigurationSource bean.
         * In Summary:
         * 
         * This corsConfigurationSource bean defines a comprehensive CORS policy that
         * allows requests from specific origins (localhost:3000, yourdomain.com),
         * permits common HTTP methods and headers, exposes the Authorization response
         * header, allows credentials to be included in requests, and caches the CORS
         * policy for 1 hour. This configuration is applied to all incoming requests
         * (/**) and is then used by Spring Security's CORS filter to enforce these
         * rules.
         */
    }
}
/*
 * The SecurityConfig class is the central configuration for Spring Security in
 * your bookstore application. It defines authentication, authorization, JWT
 * filters, CORS settings, session management, and password encoding. Below is a
 * clear breakdown:
 * 
 * Class Overview
 * 
 * @Configuration
 * 
 * @EnableWebSecurity
 * 
 * @EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
 * public class SecurityConfig
 * 
 * @Configuration: Declares that this class provides Spring configuration beans.
 * 
 * @EnableWebSecurity: Activates Spring Security’s web support.
 * 
 * @EnableMethodSecurity: Enables method-level security like @PreAuthorize
 * or @Secured.
 * 
 *  Injected Dependencies
 
 * private final UserDetailsService userDetailsService;
 * private final JwtTokenProvider jwtTokenProvider;
 * UserDetailsService: Loads user-specific data (username, roles, etc.) from DB
 * or memory.
 * 
 * JwtTokenProvider: Utility to generate and validate JWT tokens.
 * 
 *  AuthenticationManager Bean

 * @Bean
 * public AuthenticationManager authenticationManager(HttpSecurity http) throws
 * Exception
 * Configures Spring’s AuthenticationManager with:
 * 
 * userDetailsService for fetching user info.
 * 
 * passwordEncoder() for validating password hashes (BCrypt).
 * 
 * It will be used by JwtAuthenticationFilter to authenticate login credentials.
 * 
 * SecurityFilterChain Bean
 * 
 * @Bean
 * public SecurityFilterChain filterChain(HttpSecurity http,
 * AuthenticationManager authenticationManager)
 * Configures Security:
 * CORS + CSRF
 * 
 * Enables custom CORS (Cross-Origin Resource Sharing).
 * 
 * Disables CSRF (common for stateless APIs).
 * 
 * Authorization Rules
 * 

 * .authorizeHttpRequests(authorize -> authorize
 * .requestMatchers("/api/auth/**").permitAll()
 * .requestMatchers("/h2-console/**").permitAll()
 * .requestMatchers("/api/authors/**").hasAnyRole("USER", "ADMIN")
 * .anyRequest().authenticated()
 * )
 * Allows public access to auth endpoints and H2 console.
 * 
 * Protects /api/authors/** for authenticated roles.
 * 
 * Session Policy
 * 
 * .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
 * No server-side session will be created.
 * 
 * Authentication is purely token-based.
 * 
 * JWT Filters
 * 
 * .addFilter(jwtAuthenticationFilter)
 * .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider),
 * UsernamePasswordAuthenticationFilter.class);
 * Adds:
 * 
 * Authentication Filter (for login, generates JWT).
 * 
 * Authorization Filter (reads and validates JWT on every request).
 * 
 * H2 Console Support (Dev Only)
 * 
 * http.headers().frameOptions().disable();
 *  Password Encoder Bean
 * 
 * @Bean
 * public PasswordEncoder passwordEncoder() {
 * return new BCryptPasswordEncoder();
 * }
 * Ensures passwords are stored in BCrypt-hashed form and verified accordingly.
 * 
 *  CORS Configuration Bean
 * 
 * @Bean
 * public CorsConfigurationSource corsConfigurationSource()
 * Allows cross-origin requests from frontend (e.g., React app at
 * localhost:3000).
 * 
 * Allows methods like GET, POST, PUT, DELETE.
 * 
 * Exposes the Authorization header so the frontend can read JWT tokens.
 * 
 * Overall Flow:
 * Scenario: User Logs In
 * POST /api/auth/login
 * 
 * → JwtAuthenticationFilter triggers
 * 
 * → AuthenticationManager verifies credentials using UserDetailsService and
 * PasswordEncoder
 * 
 * → JWT token is generated and returned in response
 * 
 * Scenario: User Accesses Protected Resource
 * Request to /api/authors
 * 
 * → JwtAuthorizationFilter reads JWT from Authorization header
 * 
 * → If valid, user is set into SecurityContext
 * 
 * → Controller is allowed to handle request if role matches
 * 
 *  Summary
 * Component Role
 * UserDetailsService Fetches user data from DB or in-memory store
 * JwtAuthenticationFilter Handles login and JWT issuance
 * JwtAuthorizationFilter Validates JWT and sets authentication context
 * AuthenticationManager Delegates authentication using UserDetailsService +
 * PasswordEncoder
 * SecurityFilterChain Controls route access, session, and filter chaining
 * CorsConfigurationSource Handles cross-origin configuration
 */