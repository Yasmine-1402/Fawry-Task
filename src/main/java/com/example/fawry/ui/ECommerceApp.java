package com.example.fawry.ui;

import javafx.application.Application;
import com.example.fawry.cart.Cart;
import com.example.fawry.exception.*;
import com.example.fawry.model.*;
import com.example.fawry.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ECommerceApp extends Application {

 private List<Product> inventory;
    private Customer customer;
    private Cart cart;
    private CheckoutService checkoutService;

    // UI Components
    private VBox productListView;
    private VBox cartView;
    private Label subtotalLabel;
    private Label customerInfoLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialize backend data and services
        initializeData();

        primaryStage.setTitle("E-Commerce Store");

        // 2. Setup main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top: Customer Info
        customerInfoLabel = new Label();
        updateCustomerInfo();
        customerInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        BorderPane.setAlignment(customerInfoLabel, Pos.CENTER);
        root.setTop(customerInfoLabel);

        // Center: Product List
        ScrollPane productScrollPane = new ScrollPane();
        productListView = new VBox(10);
        productListView.setPadding(new Insets(10));
        productScrollPane.setContent(productListView);
        root.setCenter(productScrollPane);

        // Right: Shopping Cart
        VBox cartPane = createCartPane();
        root.setRight(cartPane);

        // 3. Populate UI with initial data
        refreshProductList();

        // 4. Show the stage
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeData() {
        ShippingService shippingService = new ShippingService();
        checkoutService = new CheckoutService(shippingService);
        cart = new Cart();
        customer = new Customer("Jane Smith", 2000.00);

        inventory = new ArrayList<>();
        inventory.add(new ElectronicProduct("Smart TV", 899.99, 15, 15.5));
        inventory.add(new ElectronicProduct("Laptop Pro", 1499.50, 8, 2.2));
        inventory.add(new FoodProduct("Artisan Cheese", 25.00, 30, LocalDate.now().plusMonths(2), 0.5));
        inventory.add(new FoodProduct("Organic Milk", 4.50, 50, LocalDate.now().plusDays(10), 1.0));
        inventory.add(new FoodProduct("Stale Crackers (Expired)", 1.99, 10, LocalDate.now().minusDays(1), 0.3));
    }

    private VBox createCartPane() {
        VBox cartPane = new VBox(15);
        cartPane.setPadding(new Insets(10));
        cartPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");
        cartPane.setMinWidth(250);

        Label cartTitle = new Label("Shopping Cart");
        cartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        cartView = new VBox(5);
        cartView.getChildren().add(new Label("Cart is empty."));

        subtotalLabel = new Label("Subtotal: $0.00");
        subtotalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button checkoutButton = new Button("Checkout");
        checkoutButton.setMaxWidth(Double.MAX_VALUE);
        checkoutButton.setOnAction(e -> handleCheckout());

        cartPane.getChildren().addAll(cartTitle, new Separator(), cartView, new Separator(), subtotalLabel, checkoutButton);
        return cartPane;
    }

    private void refreshProductList() {
        productListView.getChildren().clear();
        for (Product product : inventory) {
            productListView.getChildren().add(createProductView(product));
        }
    }

    private Node createProductView(Product product) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));
        grid.setStyle("-fx-border-color: #dcdcdc; -fx-border-width: 1; -fx-border-radius: 5;");

        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label detailsLabel = new Label(
            String.format("$%.2f - Stock: %d", product.getPrice(), product.getQuantity())
        );

        Spinner<Integer> quantitySpinner = new Spinner<>(1, product.getQuantity() > 0 ? product.getQuantity() : 1, 1);
        quantitySpinner.setPrefWidth(70);
        quantitySpinner.setDisable(product.getQuantity() <= 0);

        Button addButton = new Button("Add to Cart");
        addButton.setDisable(product.getQuantity() <= 0);

        addButton.setOnAction(e -> {
            int quantityToAdd = quantitySpinner.getValue();
            // Basic validation for UI
            if (product.getQuantity() >= quantityToAdd) {
                cart.addProduct(product, quantityToAdd);
                updateCartView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Stock Error", "Not enough stock available.");
            }
        });

        grid.add(nameLabel, 0, 0, 2, 1); // Span 2 columns
        grid.add(detailsLabel, 0, 1);
        grid.add(quantitySpinner, 0, 2);
        grid.add(addButton, 1, 2);

        return grid;
    }

    private void updateCartView() {
        cartView.getChildren().clear();
        Map<Product, Integer> items = cart.getItems();

        if (items.isEmpty()) {
            cartView.getChildren().add(new Label("Cart is empty."));
        } else {
            for (Map.Entry<Product, Integer> entry : items.entrySet()) {
                String itemText = String.format("%s x%d", entry.getKey().getName(), entry.getValue());
                cartView.getChildren().add(new Label(itemText));
            }
        }

        double subtotal = checkoutService.calculateSubtotal(cart);
        subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
    }

    private void handleCheckout() {
        try {
            String receipt = checkoutService.processCheckout(customer, cart);
            showAlert(Alert.AlertType.INFORMATION, "Checkout Successful", receipt);

            // Reset and refresh UI
            cart.clearCart();
            updateCartView();
            refreshProductList();
            updateCustomerInfo();

        } catch (EmptyCartException | InsufficientStockException | ProductExpiredException | InsufficientBalanceException e) {
            showAlert(Alert.AlertType.ERROR, "Checkout Failed", e.getMessage());
        }
    }

    private void updateCustomerInfo() {
        customerInfoLabel.setText(String.format("Customer: %s | Balance: $%.2f", customer.getName(), customer.getBalance()));
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

