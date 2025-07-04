package com.example.fawry.cart;

import com.example.fawry.model.Product;
import java.util.HashMap;
import java.util.Map;
public class Cart {
    private final Map<Product, Integer> items = new HashMap<>();

    public void addProduct(Product product, int quantity) {
        // Sprint 2: Basic check, prints to console. Will be enhanced in Sprint 3.
        if (quantity <= 0) {
            System.out.println("Error: Quantity must be positive.");
            return;
        }
        if (product.getQuantity() < quantity) {
            System.out.println("Error: Not enough stock for " + product.getName());
            return;
        }
        items.put(product, items.getOrDefault(product, 0) + quantity);
    }

    public Map<Product, Integer> getItems() {
        return items;
    }

    public void clearCart() {
        items.clear();
    }
}


