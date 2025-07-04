package com.example.fawry.model;

public class Customer {private String name;
    private double balance;
    private String username;
    private String password; // In a real app, this should be hashed!


public Customer(String name, String username, String password, double balance) {
    this.name = name;
    this.username = username;
    this.password = password; // <-- THIS LINE IS THE FIX
    this.balance = balance;
}

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

