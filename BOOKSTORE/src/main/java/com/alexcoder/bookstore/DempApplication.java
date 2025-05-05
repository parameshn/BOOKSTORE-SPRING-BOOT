package com.alexcoder.bookstore;

import org.springframework.boot.SpringApplication;
/*Purpose: This is the class that is used to launch the Spring Boot application.

Explanation: SpringApplication is the entry point for running a Spring Boot application. The run() method is 
responsible for starting the application. It sets up the Spring context and triggers the embedded web server (like Tomcat)
 to run your application. */
import org.springframework.boot.autoconfigure.SpringBootApplication;
/*import org.springframework.boot.autoconfigure.SpringBootApplication;
Purpose: This is a core annotation in Spring Boot that marks the main class of a Spring Boot application.

Explanation:

@SpringBootApplication is a composite annotation that:

@Configuration: This means that the class will provide bean definitions for the Spring context.

@EnableAutoConfiguration: Automatically configures Spring based on the libraries in the classpath. 
This removes the need for you to manually configure most Spring beans (like DataSource or JPA beans) unless
 you want to customize them.

@ComponentScan: This tells Spring to scan the current package and sub-packages for Spring components (like @Service,
 @Repository, etc.), so it can manage them as beans. */
@SpringBootApplication
public class DempApplication {

    public static void main(String[] args) {
        SpringApplication.run(DempApplication.class, args);
    }
    // .class refers to the compiled class type in Java, not the actual object
    // The .class suffix refers to the class definition itself and not an instance
    // of that class.

    /*
     * Spring Boot’s SpringApplication.run() method expects a Class object as the
     * first parameter. This is how it knows which class to start the application
     * from. It looks at the class provided, loads it into the Spring context, and
     * starts the embedded web server (if applicable).
     */


}
