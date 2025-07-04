
package com.example.fawry;
import com.example.fawry.exception.*;
import com.example.fawry.model.*;
import com.example.fawry.service.CheckoutService;
import com.example.fawry.service.ShippingService;
import com.example.fawry.cart.Cart;


import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // --- Setup ---
        ShippingService shippingService = new ShippingService();
        CheckoutService checkoutService = new CheckoutService(shippingService);

        // Create products
        Product laptop = new ElectronicProduct("Laptop", 1200.00, 10, 2.5);
        Product cheese = new FoodProduct("Gouda Cheese", 15.50, 20, LocalDate.now().plusMonths(3), 0.5);
        Product expiredBread = new FoodProduct("Expired Bread", 2.00, 5, LocalDate.now().minusDays(1), 0.4);

        // Create a customer
        Customer customer = new Customer("John Doe", "johndoe", "password123", 1500.00);

        System.out.println("Welcome, " + customer.getName() + "! Your balance is $" + customer.getBalance());
        System.out.println("Available products:");
        System.out.println(laptop);
        System.out.println(cheese);
        System.out.println(expiredBread);

        // --- Simulation ---
        Cart cart = new Cart();
        cart.addProduct(laptop, 1);
        cart.addProduct(cheese, 2);
        
        System.out.println("\n--- Cart Contents ---");
        cart.getItems().forEach((product, quantity) ->
                System.out.println(product.getName() + " x" + quantity));

        System.out.println("\n--- Attempting Checkout ---");
        try {
            String receipt = checkoutService.processCheckout(customer, cart);
            System.out.println(receipt);
            System.out.println("Checkout successful!");
            System.out.println(customer.getName() + "'s new balance: $" + String.format("%.2f", customer.getBalance()));
            System.out.println("Remaining stock for " + laptop.getName() + ": " + laptop.getQuantity());

        } catch (InsufficientBalanceException | InsufficientStockException | EmptyCartException | ProductExpiredException e) {
            System.err.println("Checkout failed: " + e.getClass());
        }
        // --- Test Error Case: Expired Product ---
        System.out.println("\n--- Test Case: Adding Expired Product ---");
        Cart badCart = new Cart();
        badCart.addProduct(expiredBread, 1);
        try {
            checkoutService.processCheckout(customer, badCart);
        } catch (Exception e) {
            System.err.println("Error as expected: " + e.getMessage());
        }
    }
}