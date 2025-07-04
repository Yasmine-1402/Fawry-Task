package com.example.fawry.model;

import com.example.fawry.interfaces.Expirable;
import com.example.fawry.interfaces.Shippable;

import java.time.LocalDate;

public class FoodProduct extends Product implements Expirable , Shippable {
private LocalDate expirationDate;
    private double weight;

    public FoodProduct(String name, double price, int quantity, LocalDate expirationDate, double weight) {
        super(name, price, quantity);
        this.expirationDate = expirationDate;
        this.weight = weight;
    }

    @Override
    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

    @Override
    public double getWeight() {
        return weight;
    }
}

