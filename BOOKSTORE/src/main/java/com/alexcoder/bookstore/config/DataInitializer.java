package com.alexcoder.bookstore.config;

/*For a real deployment with active users, the DataInitializer class is not needed and should be disabled or removed from the application. */


import com.alexcoder.bookstore.model.User;
import com.alexcoder.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            createUsers();
        }
    }

    private void createUsers() {
        
        //Regular user
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("user@example.com");
        Set<String> userRoles = new HashSet<>();
        userRoles.add("USER");
        user.setRoles(userRoles);
        userRepository.save(user);

        // Admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("USER");
        adminRoles.add("ADMIN");
        admin.setRoles(adminRoles);
        admin.setEmail("admin@example.com");
        userRepository.save(admin);

        System.out.println("Created test Users : ser/password and admin/admin123");
    }

}




/*
 * DataInitializer Class Overview
 * The DataInitializer class is a Spring component (@Component) designed to seed
 * your database with initial data when your application starts up. It
 * implements the CommandLineRunner interface, which means its run method will
 * be executed automatically by Spring Boot after the application context has
 * been loaded.
 * 
 * Key Components and Their Roles
 * 
 * @Component: Marks this class as a Spring-managed component, allowing Spring
 * to detect it, create an instance, and manage its lifecycle.
 * CommandLineRunner Interface: Implementing this interface instructs Spring
 * Boot to execute the run(String... args) method once the entire Spring
 * application context has been initialized. This is perfect for one-time setup
 * tasks.
 * UserRepository: Injected via constructor Autowired, this is your data access
 * interface for User entities. The initializer uses it to save new User objects
 * to the database.
 * PasswordEncoder: Also injected, this is the same BCryptPasswordEncoder (or
 * similar) you've configured in SecurityConfig. It's crucial here because you
 * must never store plain-text passwords in your database. This encoder hashes
 * the passwords before saving them.
 * How It Works
 * Application Startup: When your Spring Boot application starts, it performs
 * its usual initialization.
 * CommandLineRunner Execution: After the application context is fully loaded
 * (including all beans, repositories, and services), Spring Boot finds all
 * CommandLineRunner beans and executes their run() method.
 * Initial Data Check: Inside run(), the code first checks
 * userRepository.count() == 0. This is a vital guard clause. It ensures that
 * the test users are only created if no users already exist in the database.
 * This prevents creating duplicate users every time the application restarts if
 * data persists across restarts (e.g., with an external database).
 * createUsers() Method: If no users are found, this private helper method is
 * called.
 * It instantiates two User objects: one for "user" and one for "admin".
 * It sets their usernames, emails, and importantly, encodes their passwords
 * (passwordEncoder.encode(...)) before assigning them.
 * It defines their roles using HashSet<String> (e.g., "USER" for the regular
 * user; "USER" and "ADMIN" for the admin user). Note that these roles are
 * stored as simple strings in your User model.
 * Finally, it saves both User objects to the database using
 * userRepository.save().
 * Confirmation Message: A System.out.println message confirms that the test
 * users have been created.
 * Purpose and Benefits
 * Development & Testing Convenience: This class simplifies the setup process
 * during development. You don't have to manually register users every time you
 * start your application to test secured endpoints.
 * Guaranteed Initial State: It ensures a consistent initial set of users (e.g.,
 * a standard user and an administrator) for testing different access levels
 * right from the start.
 * Self-Contained Setup: It makes your project more self-contained by providing
 * initial data programmatically rather than relying solely on external SQL
 * scripts (data.sql) or manual database manipulation.
 * In essence, DataInitializer automates the process of populating your database
 * with essential user accounts, making your development workflow much smoother.
 */

 /*
  * For a real deployment with active users, no, you typically do not need or
  * want the DataInitializer class enabled.
  * 
  * Here's why:
  * 
  * Data Integrity: In a production environment, user data is valuable and
  * sensitive. You wouldn't want your application automatically creating default
  * users every time it starts up, as this could overwrite existing data (if the
  * check for userRepository.count() == 0 somehow failed or was removed), or
  * simply create redundant, unwanted accounts.
  * Security Risk: Hardcoded default credentials (like "user/password" or
  * "admin/admin123") in a production environment are a major security
  * vulnerability. These are easily guessable and could give unauthorized access
  * to your system.
  * User Management: In a production system, users register through your
  * application's public registration process, or administrators are created
  * through secure, controlled mechanisms, not via a hardcoded initializer.
  * Database Migrations: For managing schema changes and initial data in
  * production, you'd typically use dedicated database migration tools like
  * Flyway or Liquibase. These tools provide more robust version control,
  * rollback capabilities, and environmental specific configurations.
  * What to do for production deployment:
  * 
  * Remove or Disable DataInitializer:
  * 
  * Best Practice: The simplest and safest approach is to delete the
  * DataInitializer.java file before deploying to production.
  * Alternative (if you need it for specific non-user initialization): If you had
  * other, non-user-related data that genuinely needed to be initialized
  * programmatically (and not via migration tools), you could modify the
  * DataInitializer to be enabled only in certain profiles (e.g., dev or test).
  * You could achieve this with @Profile("!prod") on the DataInitializer class,
  * or by using a configuration property to conditionally enable/disable it.
  * However, for user data specifically, it's best to remove it entirely.
  * Focus on Secure User Registration and Admin Provisioning:
  * 
  * Ensure your /api/auth/register endpoint is robust and secure.
  * For administrator accounts, you'd typically have a separate, highly secure
  * process for their creation (e.g., manual creation in the database, a
  * dedicated admin-only endpoint with strict access controls, or through a
  * super-admin interface).
  * In summary, the DataInitializer is an excellent tool for local development
  * and testing, but it's a liability in a production environment.
  * For a real deployment with active users, no, you typically do not need or
  * want the DataInitializer class enabled.
  */