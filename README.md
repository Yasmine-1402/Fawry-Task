# JavaFX E-Commerce Store Application

This is a comprehensive desktop e-commerce application built with Java and JavaFX. It simulates the core functionalities of an online store, including user authentication, product browsing, shopping cart management, and a complete checkout process with robust error handling.

The project was developed following an agile sprint methodology, evolving from a basic console application to a fully interactive and modern-looking Graphical User Interface (GUI).



## Features

-   **User Authentication**: Secure signup and login system for customers.
-   **Product Catalog**: Displays a list of available products with name, price, and stock quantity.
-   **Diverse Product Types**:
    -   **Expirable Products**: Items with an expiration date (e.g., `FoodProduct`).
    -   **Shippable Products**: Items with a weight for calculating shipping costs (e.g., `ElectronicProduct`).
-   **Interactive Shopping Cart**:
    -   Add products with a specified quantity.
    -   View all items in the cart with a running subtotal.
    -   Clear all items from the cart with a confirmation dialog.
-   **Complete Checkout Process**:
    -   Calculates subtotal and shipping fees.
    -   Deducts the final amount from the customer's balance.
    -   Decreases the quantity of purchased products from inventory.
    -   Generates a final receipt.
-   **Robust Error Handling**: Prevents invalid operations and provides clear feedback for:
    -   Insufficient customer balance.
    -   Out-of-stock products.
    -   Attempting to purchase expired products.
    -   Checking out with an empty cart.
-   **Shipping Service Simulation**: After a successful checkout, all shippable items are sent to a simulated shipping service, which prints a notice to the console.
-   **Modern UI**: A clean, modern user interface built with JavaFX, featuring a blue and yellow theme.

## Technology Stack

-   **Language**: Java 11 (or higher)
-   **UI Framework**: JavaFX
-   **Build Tool**: Maven or Gradle (project is compatible with both)
-   **IDE**: IntelliJ IDEA 

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine.

### Prerequisites

-   **JDK 11 or higher**: Make sure you have a compatible Java Development Kit installed.
-   **An IDE**: IntelliJ IDEA (Community or Ultimate) or Eclipse with JavaFX support.
-   **Maven or Gradle**: Integrated into your IDE for automatic dependency management.

### Installation & Setup

1.  **Clone the repository:**
    ```sh
    git clone <your-repository-url>
    ```
2.  **Open the project in your IDE:**
    -   Open IntelliJ IDEA or Eclipse.
    -   Select `File > Open...` and navigate to the cloned project's root folder.
    -   The IDE should automatically detect it as a Maven/Gradle project and download the necessary dependencies (like JavaFX).

3.  **Configure the JavaFX Module (`module-info.java`):**
    The project uses the Java Platform Module System. The `module-info.java` file is already configured, but ensure your IDE recognizes it. It should look like this:
    ```java
    module com.example.fawry {
        requires javafx.controls;
        requires javafx.fxml;

        opens com.example.fawry.ui to javafx.graphics, javafx.fxml;

        exports com.example.fawry.model;
        exports com.example.fawry.cart;
        exports com.example.fawry.interfaces;
    }
    ```
4.  **Run the Application:**
    -   Navigate to `src/main/java/com/example/fawry/ui/ECommerceApp.java`.
    -   Right-click on the file and select `Run ECommerceApp.main()`.
    -   The application window should launch, starting with the login screen.

### Test Credentials

You can use the pre-defined user to log in immediately:
-   **Username**: `john_doe`
-   **Password**: `password123`

Alternatively, you can sign up for a new account directly from the login screen.

 Project Structure

The project is organized into packages based on functionality, promoting a clean and maintainable architecture.

```
/src
└── main
    └── java
        └── com
            └── example
                └── fawry
                    ├── cart/         # Contains the Cart class
                    ├── exception/    # Custom exception classes for error handling
                    ├── interfaces/   # Shippable and Expirable interfaces
                    ├── model/        # Data models (Product, Customer, etc.)
                    ├── service/      # Business logic (Checkout, Auth, Shipping)
                    └── ui/           # The main JavaFX application class (ECommerceApp)
```

# Architectural Concepts

-   **Object-Oriented Programming**: The project heavily utilizes OOP principles.
    -   **Abstraction**: `Product` is an abstract class, defining a template for all product types.
    -   **Inheritance**: `FoodProduct` and `ElectronicProduct` extend `Product`.
    -   **Interfaces**: `Shippable` and `Expirable` define contracts for specific behaviors that classes can implement.
-   **Service-Oriented Structure**: Business logic is decoupled from the UI and encapsulated in service classes (`AuthenticationService`, `CheckoutService`, `ShippingService`).
-   **Custom Exception Handling**: The application uses custom, checked exceptions to handle specific business rule violations gracefully.
