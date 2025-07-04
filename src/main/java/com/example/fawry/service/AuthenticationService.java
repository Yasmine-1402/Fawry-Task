package com.example.fawry.service;

import com.example.fawry.model.Customer;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {

    // In-memory user database (username -> Customer)
    private final Map<String, Customer> users = new HashMap<>();

    public AuthenticationService() {
        // Add a pre-existing user for testing
        users.put("john_doe", new Customer("John Doe", "john_doe", "password123", 500.00));
    } // <-- The constructor now closes correctly here.

    /**
     * Attempts to log in a user.
     * @param username The username.
     * @param password The password.
     * @return The Customer object on successful login, otherwise null.
     */
    public Customer login(String username, String password) {
        // --- START DEBUGGING PRINTS ---
        System.out.println("--- Attempting Login ---");
        System.out.println("Username provided: [" + username + "]");
        System.out.println("Password provided: [" + password + "]");
        // --- END DEBUGGING PRINTS ---

        Customer customer = users.get(username);

        // --- MORE DEBUGGING ---
        if (customer == null) {
            System.out.println("Result: No customer found with that username.");
            return null; // Login failed
        } else {
            System.out.println("Result: Found customer: " + customer.getName());
            System.out.println("Stored password is: [" + customer.getPassword() + "]");
            // Now check the password
            if (password.equals(customer.getPassword())) {
                System.out.println("Password MATCHES. Login successful.");
                return customer;
            } else {
                System.out.println("Password DOES NOT MATCH. Login failed.");
                return null;
            }
        }
    }

    /**
     * Registers a new user.
     * @param name The user's full name.
     * @param username The desired username.
     * @param password The password.
     * @return true if registration is successful, false if the username is already taken.
     */
    public boolean register(String name, String username, String password) {
        if (users.containsKey(username)) {
            return false; // Username is already taken
        }

        // This line is now correct and uses the right constructor.
        Customer newCustomer = new Customer(name, username, password, 500.00);

        users.put(username, newCustomer);
        return true;
    }

} // <-- The final brace closes the class.