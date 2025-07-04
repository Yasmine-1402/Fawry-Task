package com.example.fawry.model;

import com.example.fawry.interfaces.Shippable;

public class ElectronicProduct extends Product implements  Shippable {
    private double weight;

    public ElectronicProduct(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.weight = weight;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}

