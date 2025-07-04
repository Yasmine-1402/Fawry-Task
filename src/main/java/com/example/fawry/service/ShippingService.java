package com.example.fawry.service;
import com.example.fawry.interfaces.Shippable;
import java.util.List;
import java.util.stream.Collectors;


public class ShippingService {
public void sendShipment(List<Shippable> items) {
        if (items.isEmpty()) {
            return;
        }

        double totalWeight = items.stream().mapToDouble(Shippable::getWeight).sum();
        String itemNames = items.stream().map(Shippable::getName).collect(Collectors.joining(", "));

        System.out.println("\n--- SHIPPING NOTICE ---");
        System.out.println("Shipment has been processed for the following items:");
        System.out.println("Items: " + itemNames);
        System.out.println("Total Weight: " + String.format("%.2f", totalWeight) + " kg");
        System.out.println("-----------------------");
    }
}

