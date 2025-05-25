package com.alexcoder.bookstore.security;

import io.jsonwebtoken.Claims; // JWT claims representation
import io.jsonwebtoken.Jwts; // JWT builder/parser
import io.jsonwebtoken.SignatureAlgorithm; // Algorithm for signing
import io.jsonwebtoken.security.Keys; // Utility to create signing keys
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/*
 * io.jsonwebtoken
├── Claims
│   - Interface (extends Map<String, Object>)
│   - Usage: Represents the payload of a JWT (e.g., subject, issuer, expiration)
│   - Key Methods: getSubject(), getExpiration(), get(String key)
│
├── Jwts
│   - Utility class
│   - Usage: Entry point for creating JWT builders and parsers
│   - Key Methods: builder(), parserBuilder()
│
├── SignatureAlgorithm
│   - Enum
│   - Usage: Defines supported JWT signing algorithms (e.g., HS256, RS256)
│   - Key Constants: HS256, RS256, etc.
│
└── security
    └── Keys
        - Final utility class
        - Usage: Secure key generation and conversion utilities for JWTs
        - Key Methods:
          • Key secretKeyFor(SignatureAlgorithm alg)
              - Generate a new HMAC key
          • Key hmacShaKeyFor(byte[] secret)  ----------------> the one we are using
              - Create HMAC key from provided secret
          • KeyPair keyPairFor(SignatureAlgorithm alg)
              - Generate a key pair for asymmetric algorithms (RSA, EC)
          • PrivateKey privateKeyFromBytes(byte[])
              - Decode a private key from encoded bytes
          • PublicKey publicKeyFromBytes(byte[])
              - Decode a public key from encoded bytes

 */

 /*org.springframework.security.authentication
 └── UsernamePasswordAuthenticationToken
    - Class (extends AbstractAuthenticationToken)
    - Usage: Represents authentication info for username/password logins
    - Key Methods: getPrincipal(), getCredentials(), getAuthorities()
 */

import java.security.Key; // Represents cryptographic key
import java.util.Date; // For issuedAt and expiration
import java.util.List; // For authorities list
import java.util.stream.Collectors; // For mapping authorities

/**
 * Utility class for generating and validating JWT tokens.
 *
 * Reads configuration values (secret and expiration) from
 * application.properties.
 */

@Component

public class JwtTokenProvider {

    /*
     * JwtTokenProvider is a custom utility (annotated @Component) that you’ve wired
     * into your Spring Security setup to handle JWT creation and parsing. There is
     * no built-in Spring interface that you must implement; instead, you’ve just
     * chosen a sensible, conventional set of methods.
     * 
     * What you have
     * getSigningKey()
     * 
     * Builds a Key from your secret for HMAC-SHA signing.
     * 
     * generateToken(Authentication)
     * 
     * Issues a JWT with subject, custom roles claim, issuedAt, expiration, and
     * signature.
     * 
     * getUsernameFromToken(String)
     * 
     * Parses the token and returns the sub (subject) claim.
     * 
     * validateToken(String)
     * 
     * Verifies signature and expiration, returning a boolean.
     * 
     * getAuthentication(String)
     * 
     * Parses the token, recreates a UserDetails principal and authorities, and
     * wraps them in a UsernamePasswordAuthenticationToken.
     * 
     * Standard/Conventional Methods (You’ve covered all the essentials)
     * While there’s no “must-implement” interface, most JWT-provider utilities
     * offer these same four or five operations:
     * 
     * Key/Secret Management
     * 
     * Build or load the signing key.
     * 
     * Token Generation
     * 
     * Issue a JWT for a given user or Authentication.
     * 
     * Claim Extraction
     * 
     * Helpers to pull out subject, roles, expiration, or any other custom claims.
     * 
     * Validation
     * 
     * Check signature correctness and token lifetime.
     * 
     * Authentication Creation
     * 
     * Turn a valid JWT into a Spring Authentication for the security context.
     * 
     * Optional/Additional Helpers you might add
     * getExpirationFromToken(String) – to read the expiry date alone.
     * 
     * isTokenExpired(String) – a boolean helper.
     * 
     * refreshToken(String) – issue a new token based on an old one.
     * 
     * Custom Claims – getters/setters for any other claims (e.g. user ID, tenant
     * ID).
     * 
     * Revocation Support – consult a blacklist or cache to revoke tokens early.
     */

    /**
     * Secret signing key, loaded from properties. Should be at least 32 bytes.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Token expiration in milliseconds (e.g., 86400000 = 24 hours).
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Builds the signing key from the secret.
     * 
     * @return HMAC-SHA key for signing tokens
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes); //// just generates the key

        /*
         * The hmacShaKeyFor() method from this library is designed specifically for
         * creating HMAC-SHA keys from raw byte arrays.
         * When you call keys.hmacShaKeyFor(keyBytes), the jjwt library takes those raw
         * bytes and internally formats them into a proper java.security.Key object
         * suitable for use with one of the HMAC-SHA algorithms (like HS256, HS384, or
         * HS512). The specific algorithm chosen might depend on the length of keyBytes
         * (jjwt often infers HS256 for keys ≥256 bits, HS512 for keys ≥512 bits, etc.)
         * or could potentially be specified in an overloaded method.
         * This method handles the complexities of ensuring the key bytes are correctly
         * interpreted and formatted for the underlying cryptographic operations
         * required by HMAC-SHA. It essentially prepares the secret key material for the
         * signing process.
         */
    }

    /**
     * Generates a JWT for the given Authentication object.
     * 
     * @param authentication the authenticated principal
     * @return a signed JWT string
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        /*
         * How does it directly fetch UserDetails from my code?
         * 
         * It calls your loadUserByUsername() method.
         * 
         * You return a UserDetails object.
         * 
         * Spring Security saves that inside the Authentication token as the principal.
         * 
         * When you later access getPrincipal(), you're just retrieving the object you
         * originally returned.
         */

        // Extract roles as list of strings
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        /*
         * authentication.getAuthorities() returns a collection of GrantedAuthority
         * objects, each representing a user role or permission.
         * 
         * The stream() allows you to process each one.
         * 
         * map(GrantedAuthority::getAuthority) extracts just the string name (e.g.,
         * "ROLE_ADMIN") from each.
         * 
         * collect(Collectors.toList()) gathers them into a List<String>.
         * 
         * If a user is logged in with two roles:
         * [ new SimpleGrantedAuthority("ROLE_USER"),
         * new SimpleGrantedAuthority("ROLE_ADMIN") ]
         * 
         * Your roles list will be:
         * ["ROLE_USER", "ROLE_ADMIN"]
         */

        // Build token with subject, claims, issued and expiration
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // sets the user identity (typically the username)
                .claim("roles", roles) // attaches roles (as a custom claim)
                .setIssuedAt(now) // sets the issue time
                .setExpiration(expiryDate) // sets the token expiry
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // signs the token using HS512
                .compact(); // returns the final JWT string
    }

    /**
     * Retrieves username (subject) from the JWT.
     * 
     * @param token the JWT string
     * @return username stored in token
     */

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
     
    /*
     * Jwts.parserBuilder()
     * 
     * Creates a new JwtParserBuilder, which you can customize before parsing.
     * 
     * .setSigningKey(getSigningKey())
     * 
     * Supplies the same HMAC-SHA key you used to sign the token.
     * 
     * This ensures the parser will verify the token’s signature and reject any
     * tampered tokens.
     * 
     * .build()
     * 
     * Produces a fully configured JwtParser instance.
     * 
     * .parseClaimsJws(token)
     * 
     * Parses the compact JWS string (header.payload.signature).
     * 
     * Verifies the signature and the token’s expiration (by default).
     * 
     * Throws an exception if the token is invalid, expired, or the signature
     * doesn’t match.
     * 
     * .getBody()
     * 
     * Returns the Claims object (the JWT payload).
     * 
     * claims.getSubject()
     * 
     * The “sub” claim in the payload is conventionally used for the principal’s
     * identifier (your username).
     * 
     * Returns that string.
     */

     /**
     * Validates the token’s signature and expiration.
     * 
     * @param token the JWT string
     * @return true if valid, false otherwise
     */

     public boolean validateToken(String token) {
         try {
             Jwts.parserBuilder()
                     .setSigningKey(getSigningKey())
                     .build()
                     .parseClaimsJws(token);
             return true;
         } catch (Exception ex) {
             //token invalid or expired
             return false;
         }
     }
     
     /**
      * Creates an Authentication object from the JWT, for use in security context.
      * 
      * @param token the JWT string
      * @return Authentication containing principal and authorities
      */
      public Authentication getAuthentication(String token) {
         String username = getUsernameFromToken(token);
         Claims claims = Jwts.parserBuilder()
                 .setSigningKey(getSigningKey())
                 .build()
                 .parseClaimsJws(token)
                 .getBody();

         @SuppressWarnings("unchecked")
         List<String> roles = claims.get("roles", List.class);

         List<GrantedAuthority> authorities = roles.stream()
                 .map(SimpleGrantedAuthority::new)
                 .collect(Collectors.toList());

         UserDetails principal = org.springframework.security.core.userdetails.User
                 .withUsername(username)
                 .authorities(authorities)
                 .password("") // password not needed here
                 .build();

         return new UsernamePasswordAuthenticationToken(principal, token, authorities);
     }

}
/*
 * The Spring Security Authentication Flow (Simplified)
 * User sends credentials (username/password) — typically via login form or an
 * API endpoint.
 * 
 * Spring Security's UsernamePasswordAuthenticationFilter intercepts the request
 * and creates a UsernamePasswordAuthenticationToken with just
 * username/password.
 * 
 * Spring passes that token to the AuthenticationManager, which delegates to a
 * provider — usually:
 * 
 * DaoAuthenticationProvider
 * DaoAuthenticationProvider calls your UserDetailsService implementation:
 
 * public UserDetails loadUserByUsername(String username) { ... }
 * You return a UserDetails object (e.g., new User(...)) containing:
 * 
 * Username
 * 
 * Password (hashed)
 * 
 * Authorities (roles/permissions)
 * 
 * Account status flags
 * 
 * Spring then:
 * 
 * Verifies the password using a PasswordEncoder
 * 
 * If valid, it creates a fully-authenticated
 * UsernamePasswordAuthenticationToken with:
 * 
 * Principal = your UserDetails object
 * 
 * Credentials = null (cleared for security)
 * 
 * Authorities = user roles
 * 
 * It stores that token in the SecurityContextHolder
 * 
 * Later, when you do:

 * Authentication auth = SecurityContextHolder.getContext().getAuthentication();
 * UserDetails user = (UserDetails) auth.getPrincipal();
 * — You're retrieving the UserDetails object you originally returned from your
 * UserDetailsService.
 * 
 * org.springframework.security.authentication
 * │ └── UsernamePasswordAuthenticationToken (Class)
 * │
 */


 /*
  * What the JWT Looks Like
  * A JWT consists of three parts, separated by dots (.):
  * 
  * <Header>.<Payload>.<Signature>
  * Example (simplified):
  * 
 
  * eyJhbGciOiJIUzUxMiJ9.
  * eyJzdWIiOiJjohn.doe","roles":["ROLE_USER"],"iat":...,"exp":...}
  * .yourGeneratedSignatureHere
  *  1. Header (Base64-encoded JSON)
  * json
  * Copy
  * Edit
  * {
  * "alg": "HS512",
  
  * "typ": "JWT"
  * }
  *  2. Payload (Claims) (Base64-encoded JSON)
  * {
  * "sub": "john.doe", // subject: username
  * "roles": ["ROLE_USER", "ROLE_ADMIN"], // custom claim
  * "iat": 1715276823, // issued at (epoch)
  * "exp": 1715280423 // expiration (epoch)
  * }
  *  3. Signature
  * Generated by:
  * 
  * HMAC-SHA512(
  * Base64UrlEncode(header) + "." + Base64UrlEncode(payload),
  * secretSigningKey
  * )
  * This ensures the token is tamper-proof — if someone alters the token, the
  * signature won't match.
  * 
  * What You Get Back
  * The .compact() method returns a JWT string, like:
  
  * eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.
  * eyJzdWIiOiJqb2huLmRvZSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE3MTUyNzY4MjMsImV4cCI6MTcxNTI4MDQyM30.
  * QZ-3MZtC9fZJ6UdnwAXG29LZ9L-...
  * This token can now be:
  * 
  * Sent to clients via HTTP response headers or body (e.g., Authorization:
  * Bearer <token>)
  * 
  * Used by clients in requests to prove authentication
  * 
  */