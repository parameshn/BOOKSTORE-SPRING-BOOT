package com.alexcoder.bookstore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * This controller demonstrates different ways to secure endpoints
 * using method-level security annotations.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    /**
     * This endpoint uses the @PreAuthorize annotation which supports SpEL (Spring
     * Expression Language)
     * and is more flexible than @Secured.
     */

     @GetMapping("/dashboard")
     @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<?> adminDashboard() {
         Map<String, Object> response = new HashMap<>();
         response.put("message", "Welcome to Admin Dashboard");
         response.put("status", "success");
         return ResponseEntity.ok(response);
     }
     /**
      * This endpoint uses the @Secured annotation which is simpler
      * but less flexible than @PreAuthorize.
      */
      @GetMapping("/reports")
      @Secured("ROLE_ADMIN")
      public ResponseEntity<?> adminReports() {
          Map<String, Object> response = new HashMap<>();
          response.put("message", "Admin Reports Available");
          response.put("status", "success");
          return ResponseEntity.ok(response);
      }
      /*
       * @Secured vs. @PreAuthorize: The Flexibility Gap
       * No Spring Expression Language (SpEL) Support:
       * 
       * @Secured: It only accepts a fixed string or an array of strings representing
       * role names (e.g., "ROLE_ADMIN", {"ROLE_USER", "ROLE_ADMIN"}). It performs a
       * direct string comparison against the user's granted authorities. You can't
       * use dynamic logic, access method arguments, or inspect the authenticated
       * principal's properties.
       * 
       * @PreAuthorize: This is where it shines. It's built specifically to leverage
       * Spring Expression Language (SpEL). This allows you to write complex, dynamic
       * authorization rules. For example:
       * 
       * @PreAuthorize("hasRole('ADMIN') and authentication.principal.username != 'temporary_admin'"
       * ) (as seen in your adminSettings method) – This checks both role and a
       * specific username.
       * 
       * @PreAuthorize("hasPermission(#bookId, 'com.alexcoder.bookstore.model.Book', 'write')"
       * ) – This example (if you had a permission evaluation system) could check if
       * the current user has write permission on a specific Book object identified by
       * bookId, which is a method argument.
       * 
       * @PreAuthorize("hasAuthority('book:write')") – If you use granular authorities
       * instead of roles.
       * 
       * @PreAuthorize("@someService.canUserEdit(#userId)") – You can even invoke
       * other Spring beans (@someService) to perform custom authorization logic based
       * on method arguments (#userId).
       * Limited Semantics:
       * 
       * @Secured: It primarily supports the equivalent of an "OR" operation for
       * multiple roles (e.g., {"ROLE_USER", "ROLE_ADMIN"} means ROLE_USER OR
       * ROLE_ADMIN). There's no built-in way to express "AND" conditions directly
       * within the annotation itself for multiple roles or other complex boolean
       * logic.
       * 
       * @PreAuthorize: With SpEL, you have full boolean logic (and, or, not),
       * comparisons (==, !=, <, >), collection operations, and more. This lets you
       * combine multiple conditions precisely.
       * No Pre or Post Execution Hooks:
       * 
       * @Secured: It's a "pre-invocation" check only. It runs before the method
       * executes, and that's it.
       * 
       * @PreAuthorize: This is a "pre-invocation" check.
       * 
       * @PostAuthorize: This is a "post-invocation" check that runs after the method
       * has executed but before the result is returned to the client. This is useful
       * if you need to make an authorization decision based on the method's return
       * value (e.g.,
       * "only allow a user to see their own data, even if they requested someone else's by ID"
       * ).
       * 
       * @PostFilter / @PreFilter: These annotations (though less common for simple
       * REST APIs) allow you to filter collections passed as arguments (@PreFilter)
       * or returned from methods (@PostFilter) based on authorization rules.
       * When to Use Which
       * Use @Secured when:
       * 
       * You need very simple, static role checks (e.g.,
       * "this method requires ROLE_ADMIN").
       * You prefer a concise, less expressive syntax.
       * You don't need any dynamic logic based on method arguments or other context.
       * Use @PreAuthorize when:
       * 
       * You need any level of complexity beyond a direct role match.
       * You want to use SpEL to express dynamic rules (e.g., checking user ID,
       * specific object properties, or custom service logic).
       * You need to combine multiple conditions with AND, OR, NOT.
       * You need to access arguments of the method (#argName).
       * You plan to use @PostAuthorize, @PreFilter, or @PostFilter.
       * For most modern Spring Security applications, @PreAuthorize is the preferred
       * and more powerful choice due to its flexibility with SpEL, even for simple
       * role checks (e.g., @PreAuthorize("hasRole('ADMIN')") is perfectly valid and
       * common). It provides a consistent approach to authorization across your
       * application.
       */

       /**
       * This endpoint demonstrates how to use @PreAuthorize with a complex
       * expression.
       */
      @GetMapping("/settings")
      @PreAuthorize("hasRole('ADMIN') and authentication.principal.username != 'temporary_admin'")
      public ResponseEntity<?> adminSettings() {
          Map<String, Object> response = new HashMap<>();
          response.put("message", "Admin Settings Page");
          response.put("status", "success");
          return ResponseEntity.ok(response);
      }
        
      /**
       * This endpoint shows how to get the current authenticated user.
       */
      @GetMapping("/profile")
      @PreAuthorize("isAuthenticated()")
      public ResponseEntity<?> adminProfile() {
          Authentication auth = SecurityContextHolder.getContext().getAuthentication();

          Map<String, Object> response = new HashMap<>();
          response.put("username", auth.getName());
          response.put("rolse", auth.getAuthorities());
          response.put("isAuthenticated", auth.isAuthenticated());

          return ResponseEntity.ok(response);
      }
}
