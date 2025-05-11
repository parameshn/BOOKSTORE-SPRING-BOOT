package com.alexcoder.bookstore.service;

// Spring Security interfaces and classes for UserDetails and authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Represents an authority granted to an Authentication object
import org.springframework.security.core.userdetails.UserDetails; // Core user information interface
import org.springframework.security.core.userdetails.UserDetailsService; // Loads user-specific data for authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Thrown when a username is not found during

/*
├── io.jsonwebtoken
│   ├── Claims (Interface)
│   │   ├── getIssuer()
│   │   ├── getSubject()
│   │   ├── getAudience()
│   │   ├── getExpiration()
│   │   ├── getNotBefore()
│   │   ├── getIssuedAt()
│   │   ├── getId()
│   │   └── get(String name, Class<T> requiredType)
│   ├── Jwts (Class)
│   ├── SignatureAlgorithm (Enum)
│   └── security (Package)
│       └── Keys (Class)
│
├── org.springframework.beans.factory.annotation
│   └── Value (Annotation)
│
├── org.springframework.security.authentication
│   └── UsernamePasswordAuthenticationToken (Class)
│
└── org.springframework.security.core
    ├── Authentication (Interface)
    │   ├── getAuthorities()
    │   ├── getCredentials()
    │   ├── getDetails()
    │   ├── getPrincipal()
    │   ├── isAuthenticated()
    │   └── setAuthenticated(boolean isAuthenticated)
    ├── GrantedAuthority (Interface)
    │   └── getAuthority()
    ├── authority (Package)
    │   └── SimpleGrantedAuthority (Class)
    └── userdetails (Package)
        ├── UserDetails (Interface)
        │   ├── getAuthorities()
        │   ├── getPassword()
        │   ├── getUsername()
        │   ├── isAccountNonExpired()
        │   ├── isAccountNonLocked()
        │   ├── isCredentialsNonExpired()  <-- Corrected list (now 7 methods)
        │   └── isEnabled()
        └── UserDetailsService (Interface)
            └── loadUserByUsername(String username)
*/

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Declarative transaction management
import com.alexcoder.bookstore.model.User;
import com.alexcoder.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Loads user details from the database for Spring Security.
 * <p>
 * Provided by spring-boot-starter-security; no custom interface definition is
 * required.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Retrieves the UserDetails corresponding to the given username.
     * <p>
     * Roles are converted into GrantedAuthority objects for security checks.
     *
     * @param username the username to look up
     * @return UserDetails containing credentials and authorities
     * @throws UsernameNotFoundException when no user matches the provided username
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepositroy.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username:" + username));

        var authorities = user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        /*
         * SimpleGrantedAuthority is a concrete implementation of Spring Security's
         * GrantedAuthority interface, used to represent a single, simple permission or
         * role granted to an authenticated user, typically identified by a string name.
         * 
         * In simpler terms, it's the standard way to wrap a string (like "ROLE_ADMIN"
         * or "USER") so that Spring Security recognizes it as an authority that a user
         * possesses.
         */

        /*
         * // This returns a Set of String, where each string is ONE role name
         * // e.g., user.getRoles() might return {"ADMIN", "USER", "EDITOR"}
         * private Set<String> roles = new HashSet<>();
         * 
         * // ... conversion code ...
         * List<SimpleGrantedAuthority> authorities = user.getRoles().stream() // Stream
         * now contains {"ADMIN",
         * "USER", "EDITOR"}
         * .map(SimpleGrantedAuthority::new) // <-- This is the key step
         * // For each string in the stream, it creates a NEW SimpleGrantedAuthority
         * // "ADMIN" -> new SimpleGrantedAuthority("ADMIN")
         * // "USER" -> new SimpleGrantedAuthority("USER")
         * // "EDITOR"-> new SimpleGrantedAuthority("EDITOR")
         * .collect(Collectors.toList()); // Collects these NEW objects into a List
         */
        /*
         * when you create new SimpleGrantedAuthority("ADMIN"), the string "ADMIN" is
         * stored inside that specific SimpleGrantedAuthority object instance in its
         * authority field. When Spring Security (or your code) later calls
         * getAuthority() on that object, it retrieves the stored string "ADMIN".
         */

        /*
         * you are converting from a collection of things Spring Security doesn't know
         * how to treat as permissions (String) to a collection of things it does
         * understand as permissions (SimpleGrantedAuthority, which are
         * GrantedAuthoritys).
         */

        /*
         * SimpleGrantedAuthority:
         * 
         * This is by far the most common and standard implementation you will use in
         * Spring Security applications.
         * As we discussed, it's a straightforward wrapper around a single String
         * representing the authority's name (like "ROLE_ADMIN", "READ_PERMISSION",
         * etc.).
         * It's simple, efficient, and sufficient for most role-based or basic
         * permission-based access control scenarios where authorities are represented
         * as simple strings.
         * Are there other types of implementations?
         * 
         * Yes, absolutely. The GrantedAuthority is an interface specifically designed
         * to allow for different implementations.  
         * Custom Implementations: Developers can (and sometimes do) create their own
         * custom classes that implement the GrantedAuthority interface. This is done
         * when you need to associate more complex information with an authority than
         * just a single string name. For example, you might create an authority that
         * includes:
         * The permission type (e.g., "READ", "WRITE").
         * The type or ID of the resource the permission applies to (e.g., new
         * ResourcePermission("READ", "project", 123)).
         * Any conditions or validity periods for the permission.
         * Internal/Less Common Implementations: While SimpleGrantedAuthority is the
         * main concrete class for general use, the Spring Security framework or
         * extensions might have other specific implementations used internally for
         * particular features (though you typically don't interact with these directly
         * for basic role/permission setup). Historically or in specific modules, there
         * might have been other implementations, but SimpleGrantedAuthority has become
         * the de facto standard for string-based authorities.
         */

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                authorities);

    }
    /*
     * 1. Why are we using this?
     * 
     * You are using this to return the details of an authenticated user to Spring
     * Security in a format it understands.
     * When someone tries to log in (authenticate), Spring Security calls your
     * UserDetailsService's loadUserByUsername() method.
     * Your job in that method is to fetch the user's information from your data
     * source (database, etc. - represented by your user object in this snippet) and
     * then package that information into a UserDetails object that Spring Security
     * can work with.
     * org.springframework.security.core.userdetails.User is a standard,
     * ready-to-use implementation of the UserDetails interface provided by Spring
     * Security. This line creates an instance of that standard User object using
     * the data from your application's user object and the processed authorities.
     * 2. What is UserDetails and User?
     * 
     * UserDetails: This is a core interface in Spring Security
     * (org.springframework.security.core.userdetails.UserDetails). It defines the
     * essential user information that Spring Security needs for both authentication
     * and authorization decisions. It acts as an adapter between your application's
     * user data model and Spring Security's framework.
     * org.springframework.security.core.userdetails.User: This is a concrete class
     * provided by Spring Security that implements the UserDetails interface. It's
     * the most common built-in implementation of UserDetails. Instead of writing
     * your own class that implements all the methods of UserDetails, you can often
     * just populate an instance of this standard User class with your user's data.
     * 3. Why do we only need only these many fields?
     * 
     * The constructor you've shown is one of the standard constructors for
     * org.springframework.security.core.userdetails.User. It takes precisely the
     * fields that directly correspond to the essential methods defined in the
     * UserDetails interface:
     * getUsername() -> user.getUsername()
     * getPassword() -> user.getPassword()
     * isEnabled() -> user.isEnabled()
     * isAccountNonExpired() -> The first true
     * isCredentialsNonExpired() -> The second true
     * isAccountNonLocked() -> The third true
     * getAuthorities() -> authorities (your List<SimpleGrantedAuthority>)
     * This constructor provides all the minimum information Spring Security needs
     * to represent an authenticated principal (user) and perform basic account
     * status checks and authorization based on authorities.
     * 4. What does true mean for the three fields and why?
     * 
     * These three true values correspond to boolean flags indicating the status of
     * the user's account regarding expiration and locking:
     * 
     * First true (Account Non-expired): Corresponds to
     * UserDetails.isAccountNonExpired(). Setting it to true means the user's
     * account is considered not expired. If your application has logic for expiring
     * accounts after a certain date, you would fetch that status from your user
     * object and pass the actual boolean result (e.g., user.isAccountNonExpired()).
     * Setting it to true effectively disables account expiration checks managed by
     * Spring Security for this user.
     * Second true (Credentials Non-expired): Corresponds to
     * UserDetails.isCredentialsNonExpired(). Setting it to true means the user's
     * credentials (usually their password) are considered not expired. If your
     * application forces users to change passwords periodically, you would pass the
     * result of that check here (e.g., user.isPasswordNonExpired()). Setting it to
     * true disables credentials expiration checks.
     * Third true (Account Non-locked): Corresponds to
     * UserDetails.isAccountNonLocked(). Setting it to true means the user's account
     * is considered not locked. If your application locks accounts after multiple
     * failed login attempts or via administrative action, you would pass that
     * status here (e.g., user.isNonLocked()). Setting it to true disables account
     * locking checks.
     * Setting them to true implies that, based on the successful loading of the
     * user from your data source (user.getUsername(), user.getPassword()), the
     * account is currently in a fully valid, unlocked, and non-expired state
     * according to your application's current logic. If your application enforced
     * any of these statuses, you would replace true with the boolean result of
     * checking your user object's status.
     * 
     * 5. Why the last field is List<SimpleGrantedAuthority> and how does it
     * support?
     * 
     * The last field corresponds to the UserDetails.getAuthorities() method. This
     * method must return a Collection of objects that implement the
     * GrantedAuthority interface.
     * As we discussed, SimpleGrantedAuthority is the standard class that implements
     * GrantedAuthority for simple string-based roles or permissions.
     * Your authorities variable holds the result of converting your Set<String>
     * roles into a List<SimpleGrantedAuthority>. This List contains all the
     * individual permissions/roles granted to the user, wrapped in the
     * GrantedAuthority format.
     * How it supports:
     * 
     * This collection of SimpleGrantedAuthority objects is how you tell Spring
     * Security what the user is allowed to do.
     * 
     * When Spring Security has the authenticated user's UserDetails object (which
     * now contains this List<SimpleGrantedAuthority>), it uses the getAuthorities()
     * method to retrieve the list of permissions.
     * Later, when an authorization decision is needed (e.g., checking if the user
     * can access a URL or call a method protected
     * by @PreAuthorize("hasRole('ADMIN')")), Spring Security looks at the
     * authorities in this list.
     * It iterates through the collection and compares the required authority (e.g.,
     * "ROLE_ADMIN") against the authorities held by the user. If a match is found
     * in the authorities list you provided, access is granted (assuming other
     * conditions are met).
     * So, the List<SimpleGrantedAuthority> is the concrete list of permissions that
     * directly fuels Spring Security's authorization engine for this specific user.
     */

}


/**
 * The UserDetailsService interface is part of Spring Security (packaged in
 * spring-boot-starter-security) and defines a single method:
 * 
 * java
 * Copy
 * Edit
 * UserDetails loadUserByUsername(String username) throws
 * UsernameNotFoundException;
 * 1. Predefined Methods
 * loadUserByUsername(String)
 * – Spring Security calls this during the authentication process (e.g. when you
 * hit your /login endpoint or when the UsernamePasswordAuthenticationFilter
 * runs).
 * – You don’t need to define any other methods—this one contract is enough for
 * Spring to retrieve a user’s credentials and authorities.
 * 
 * 2. How It Works
 * Authentication Begins
 * When a user submits credentials (username / password), Spring’s
 * AuthenticationManager delegates to a UserDetailsService bean.
 * 
 * Loading the User
 * Your implementation (in UserDetailsServiceImpl) looks up the User entity from
 * the database via UserRepository.
 * 
 * Building UserDetails
 * You convert that User into Spring Security’s own UserDetails model (usually
 * via new org.springframework.security.core.userdetails.User(...)), supplying:
 * 
 * Username
 * 
 * Encrypted password
 * 
 * Enabled/disabled flags
 * 
 * Account non-expired, non-locked, credentials non-expired (we passed true for
 * those)
 * 
 * Granted authorities (mapped from your roles)
 * 
 * Password Check & Authority Check
 * Once Spring has the UserDetails, it compares the submitted (raw)
 * password—after encoding via your PasswordEncoder—to the stored password.
 * If they match, authentication succeeds, and the Authentication object (with
 * your authorities) is stored in the SecurityContext.
 * 
 * 3. Conditions & Exceptions
 * If no user is found for the given username, you must throw
 * UsernameNotFoundException. Spring Security will catch this and fail
 * authentication with a 401.
 * 
 * You can also throw other exceptions (e.g. DisabledException) if you want to
 * enforce additional conditions (e.g. account locked or disabled).
 * 
 * 4. Spring’s Role
 * Provided by Spring Security: You don’t write or register the
 * UserDetailsService interface itself—it comes out of the box with the security
 * starter.
 * 
 * Mandatory for form/JWT authentication: Almost every authentication flow in
 * Spring Security (username/password, LDAP, JDBC, OAuth2, etc.) ultimately
 * needs a UserDetailsService to fetch user data, unless you use a different
 * mechanism (e.g. purely token-based without a DB lookup).
 * 
 * implementing Spring Security’s UserDetailsService exactly this way is the de
 * facto industry standard for username/password authentication in Spring Boot
 * applications:
 * 
 * Single‐Method Interface: UserDetailsService defines just
 * loadUserByUsername(...), keeping your implementation focused.
 * 
 * Repository Lookup: Fetching your domain User via a JPA repository is the most
 * common pattern.
 * 
 * Mapping to UserDetails: Wrapping the entity’s username, password, enabled
 * flag, and roles (as GrantedAuthority) into Spring Security’s built-in User
 * class is textbook.
 * 
 * Transactional Read-Only: Annotating the method with @Transactional(readOnly =
 * true) ensures efficient, consistent reads.
 * 
 * Exception Handling: Throwing UsernameNotFoundException is required so Spring
 * can correctly respond with a 401.
 * 
 * Best Practices You’re Already Following
 * Constructor Injection for the repository.
 * 
 * Eager loading of roles so you don’t hit lazy-loading issues during the
 * security filter chain.
 * 
 * Prefixing roles with ROLE_ (or storing them already prefixed) ensures
 * compatibility with hasRole(...) and hasAuthority(...) checks.
 */ 

 /*
  * Load the domain user
  * 
  * java
  * Copy
  * Edit
  * User user = userRepository.findByUsername(username)
  * .orElseThrow(() -> new UsernameNotFoundException(
  * "User not found with username: " + username));
  * Calls your JPA repository to look up a User entity by its username.
  * 
  * If no user is found, it immediately throws UsernameNotFoundException, which
  * Spring Security catches and turns into a 401 Unauthorized.
  * 
  * Map your roles into Spring authorities
  * 
  * java
  * Copy
  * Edit
  * var authorities = user.getRoles().stream()
  * .map(SimpleGrantedAuthority::new)
  * .collect(Collectors.toList());
  * Takes the Set<String> of roles on your User (e.g. "ROLE_USER", "ROLE_ADMIN").
  * 
  * Streams each role string into a SimpleGrantedAuthority object, which is what
  * Spring uses internally to represent permissions.
  * 
  * Collects them into a List<GrantedAuthority> you can hand back to Spring.
  * 
  * Build and return a Spring Security UserDetails
  * 
  * java
  * Copy
  * Edit
  * return new org.springframework.security.core.userdetails.User(
  * user.getUsername(), // the principal’s username
  * user.getPassword(), // the (already encrypted) password
  * user.isEnabled(), // whether the account is active
  * true, // accountNonExpired
  * true, // credentialsNonExpired
  * true, // accountNonLocked
  * authorities // the granted authorities
  * );
  * You’re instantiating Spring Security’s built-in User class (which implements
  * UserDetails).
  * 
  * By passing in your domain user’s data and the mapped authorities, you give
  * Spring Security everything it needs to:
  * 
  * Check the submitted password (it compares it—via your PasswordEncoder—against
  * user.getPassword()).
  * 
  * Know whether the account is enabled, expired, or locked.
  * 
  * Assign the proper roles/authorities to the authenticated principal.
  * 
  * After this method returns, Spring Security has a fully populated
  * Authentication object—your user is considered “logged in,” and your
  * controllers can now use annotations like @PreAuthorize("hasRole('ADMIN')") to
  * gate access.
  */

  /*
   * The last parameter to that User constructor is declared as:
   * 
   * java
   * Copy
   * Edit
   * Collection<? extends GrantedAuthority> authorities
   * So you can pass in any Collection (e.g. List, Set) whose elements implement
   * Spring Security’s GrantedAuthority interface. In your code:
   * 
   * java
   * Copy
   * Edit
   * List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
   * .map(SimpleGrantedAuthority::new)
   * .collect(Collectors.toList());
   * A List<SimpleGrantedAuthority> is perfectly valid because:
   * 
   * SimpleGrantedAuthority implements GrantedAuthority.
   * 
   * List<SimpleGrantedAuthority> is a subtype of Collection<? extends
   * GrantedAuthority>.
   * 
   * You don’t need to wrap it in any special Collections helper call—just passing
   * the List works:
   * 
   * java
   * Copy
   * Edit
   * return new org.springframework.security.core.userdetails.User(
   * user.getUsername(),
   * user.getPassword(),
   * user.isEnabled(),
   * true, // accountNonExpired
   * true, // credentialsNonExpired
   * true, // accountNonLocked
   * authorities // List<SimpleGrantedAuthority> implements Collection<? extends
   * GrantedAuthority>
   * );
   * If you ever needed an immutable or empty collection, you could use
   * Collections.emptyList() or Collections.unmodifiableList(...), but for most
   * cases your mutable List is fine
   */


   /*
    * username
    * 
    * password
    * 
    * enabled (is the account active?)
    * 
    * accountNonExpired (has the account expired?)
    * 
    * credentialsNonExpired (have the credentials—i.e. password—expired?)
    * 
    * accountNonLocked (is the account locked?)
    * 
    * authorities (the roles/permissions granted)
    * 
    * So if your own User entity has additional fields—say:
    * 
    * boolean accountNonExpired;
    * 
    * boolean accountNonLocked;
    * 
    * boolean credentialsNonExpired;
    * 
    * boolean enabled; (you already have this)
    * 
    * you can simply wire those into the constructor instead of hardcoding true:
    * 
    * java
    * Copy
    * Edit
    * return new org.springframework.security.core.userdetails.User(
    * user.getUsername(),
    * user.getPassword(),
    * user.isEnabled(), // enabled
    * user.isAccountNonExpired(), // dynamic accountNonExpired
    * user.isCredentialsNonExpired(),// dynamic credentialsNonExpired
    * user.isAccountNonLocked(), // dynamic accountNonLocked
    * authorities
    * );
    * When Might You Need More?
    * Custom Attributes
    * If you want to expose extra data (e.g. firstName, department, tenantId) to
    * your controllers via the security context, then you’d create your own
    * UserDetails implementation:
    * 
    * java
    * Copy
    * Edit
    * public class CustomUserPrincipal implements UserDetails {
    * private final User user;
    * // implement all UserDetails methods by delegating to user...
    * 
    * // plus custom getters:
    * public String getFirstName() { return user.getFirstName(); }
    * public Long getTenantId() { return user.getTenantId(); }
    * }
    * Additional Flags
    * Spring Security only supports those four boolean flags. If you need, for
    * example, an isPasswordExpiringSoon flag, you’d handle that in a separate
    * service or by rejecting login in a custom authentication provider.
    * 
    * TL;DR
    * No, you don’t need more constructor parameters unless you’re tracking those
    * four account‐state flags on your domain model.
    * 
    * For truly custom user data beyond what Spring expects, implement your own
    * UserDetails class and return that from loadUserByUsername(...).
    * 
    * In Spring Security, roles are not predefined — you can define any roles you
    * want, such as "USER", "ADMIN", "MANAGER", "SUPPORT", "MODERATOR", etc.
    * However, there are a few conventions and rules you should follow to ensure
    * proper behavior:
    * 
    *  Rules & Conventions for Roles
    * Roles must start with ROLE_ internally
    * Spring Security expects all roles to have the prefix ROLE_ when checking
    * authorities internally.
    * So "ADMIN" becomes "ROLE_ADMIN" in code.
    * 
    * Example:
    * 
    * java
    * Copy
    * Edit
    * new SimpleGrantedAuthority("ROLE_ADMIN")
    * In annotations, omit the prefix
    * When using annotations like @PreAuthorize("hasRole('ADMIN')"), Spring
    * automatically adds the ROLE_ prefix behind the scenes.
    * 
    * Correct:
    * 
    * java
    * Copy
    * Edit
    * 
    * @PreAuthorize("hasRole('ADMIN')")
    * Wrong:
    * 
    * java
    * Copy
    * Edit
    * 
    * @PreAuthorize("hasRole('ROLE_ADMIN')") // redundant and will fail
    * Stored role names in the database can be with or without the prefix
    * 
    * If stored without ROLE_, you must add the prefix in your mapping code (e.g.
    * new SimpleGrantedAuthority("ROLE_" + role)).
    * 
    * If stored with the prefix already, use it directly.
    * 
    *  Custom Role Names Are Allowed
    * There’s no list of “approved” roles — they’re entirely up to you.
    * 
    * Examples of valid custom roles:
    * 
    * CUSTOMER_SUPPORT
    * 
    * PROJECT_MANAGER
    * 
    * VIEWER
    * 
    * SUPER_ADMIN
    * 
    * Just remember to apply the ROLE_ prefix when converting to GrantedAuthority.
    * 
    * 🛠 Summary
    * Purpose Format Example
    * Database "ADMIN" (recommended, without prefix)
    * Spring Security authority "ROLE_ADMIN"
    * In annotations @PreAuthorize("hasRole('ADMIN')")
    */

    /*
     * 1. Why Convert Set<String> to List<SimpleGrantedAuthority>?
     * 
     * The conversion is necessary because you need to transform your application's
     * internal representation of roles (Set<String>) into the specific format that
     * the Spring Security framework requires and understands for authorization
     * purposes.
     * 
     * Your Set<String> contains plain Java String objects like "ADMIN", "USER",
     * etc.
     * Spring Security's core authorization components (like AccessDecisionManager,
     * security expressions used in @PreAuthorize, etc.) operate on collections of
     * objects that implement the org.springframework.security.core.GrantedAuthority
     * interface.
     * A String object does not implement the GrantedAuthority interface.
     * SimpleGrantedAuthority does implement the GrantedAuthority interface. It's
     * Spring Security's provided class for wrapping a simple string authority name.
     * So, you are converting from a collection of things Spring Security doesn't
     * know how to treat as permissions (String) to a collection of things it does
     * understand as permissions (SimpleGrantedAuthority, which are
     * GrantedAuthoritys).
     * 
     * The stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
     * process essentially says:
     * "For every string in my Set<String>, create a new SimpleGrantedAuthority object using that string, and put all these new SimpleGrantedAuthority objects into a list."
     * 
     * You could also collect them into a Set<SimpleGrantedAuthority> using
     * Collectors.toSet(); the key is that the elements in the collection must be
     * GrantedAuthoritys. List is just a common target for Collectors.toList().
     * 
     * 2. How is SimpleGrantedAuthority store String type?
     * 
     * SimpleGrantedAuthority stores the String type it represents as a private
     * internal field. It's a very simple class.
     * 
     * Conceptually, its structure is something like this:
     * 
     * Java
     * 
     * package org.springframework.security.core.authority;
     * 
     * import org.springframework.security.core.GrantedAuthority;
     * 
     * // ... (other imports and class definition)
     * 
     * public class SimpleGrantedAuthority implements GrantedAuthority, /* other
     * interfaces
     */ /*{

    private final String authority; // <-- It has a private field to hold the string

    public SimpleGrantedAuthority(String authority) {
        // Constructor takes the string you provide
        if (authority == null) {
            throw new IllegalArgumentException("authority cannot be null");
        }
        this.authority = authority; // <-- Stores the string in the private field
    }

    @Override
    public String getAuthority() {
        // The method required by the GrantedAuthority interface returns the stored
        // string
        return this.authority;
    }

    // ... (other methods like equals, hashCode, toString)
}So,

when you create new SimpleGrantedAuthority("ADMIN"),
the string"ADMIN"
is stored
inside that
specific SimpleGrantedAuthority
object instance
in its
authority field.
When Spring

Security (or your code) later calls getAuthority() on that object, it retrieves the stored string "ADMIN". */