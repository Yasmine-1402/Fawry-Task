package com.example.fawry.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.fawry.interfaces.Expirable;
import com.example.fawry.interfaces.Shippable;
import com.example.fawry.model.Customer;
import com.example.fawry.model.Product;
import com.example.fawry.exception.*;
import com.example.fawry.cart.Cart;


public class CheckoutService {

    // Shipping fee constant for simplicity
    private static final double SHIPPING_FEE_PER_KG = 1.5;
    private final ShippingService shippingService;

    public CheckoutService(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    public double calculateSubtotal(Cart cart) {
        double subtotal = 0.0;
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            subtotal += entry.getKey().getPrice() * entry.getValue();
        }
        return subtotal;
    }

    public double calculateShippingFee(Cart cart) {
        double totalWeight = 0;
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            if (entry.getKey() instanceof Shippable) {
                totalWeight += ((Shippable) entry.getKey()).getWeight() * entry.getValue();
            }
        }
        return totalWeight * SHIPPING_FEE_PER_KG;
    }

    public String processCheckout(Customer customer, Cart cart) throws InsufficientBalanceException, InsufficientStockException, EmptyCartException, ProductExpiredException {
        // Sprint 3: Validations
        validateCart(cart);
        validateCustomerBalance(customer, cart);

        double subtotal = calculateSubtotal(cart);
        double shippingFee = calculateShippingFee(cart);
        double total = subtotal + shippingFee;

        // Process payment and update inventory
        customer.setBalance(customer.getBalance() - total);

        List<Shippable> shippableItems = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            Product product = entry.getKey();
            int quantityPurchased = entry.getValue();
            product.setQuantity(product.getQuantity() - quantityPurchased);
            if (product instanceof Shippable) {
                shippableItems.add((Shippable) product);
            }
        }

        // Sprint 3: Shipping Integration
        shippingService.sendShipment(shippableItems);

        // Build and return receipt string
        return buildReceipt(subtotal, shippingFee, total);
    }

    // Sprint 3: Validation logic
    private void validateCart(Cart cart) throws EmptyCartException, InsufficientStockException, ProductExpiredException {
        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot checkout with an empty cart.");
        }

        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            Product product = entry.getKey();
            int requestedQuantity = entry.getValue();

            if (product.getQuantity() < requestedQuantity) {
                throw new InsufficientStockException("Not enough stock for " + product.getName() + ". Available: " + product.getQuantity() + ", Requested: " + requestedQuantity);
            }

            if (product instanceof Expirable && ((Expirable) product).isExpired()) {
                throw new ProductExpiredException("Cannot purchase " + product.getName() + " as it has expired.");
            }
        }
    }

    // Sprint 3: Balance validation
    private void validateCustomerBalance(Customer customer, Cart cart) throws InsufficientBalanceException {
        double subtotal = calculateSubtotal(cart);
        double shippingFee = calculateShippingFee(cart);
        double total = subtotal + shippingFee;

        if (customer.getBalance() < total) {
            throw new InsufficientBalanceException("Insufficient balance. Required: $" + String.format("%.2f", total) + ", Available: $" + String.format("%.2f", customer.getBalance()));
        }
    }

    private String buildReceipt(double subtotal, double shippingFee, double total) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("----- RECEIPT -----\n");
        receipt.append(String.format("Subtotal: $%.2f\n", subtotal));
        receipt.append(String.format("Shipping: $%.2f\n", shippingFee));
        receipt.append("-------------------\n");
        receipt.append(String.format("TOTAL PAID: $%.2f\n", total));
        return receipt.toString();
    }
}
