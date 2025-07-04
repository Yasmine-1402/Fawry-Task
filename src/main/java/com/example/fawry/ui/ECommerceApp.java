package com.example.fawry.ui;

import com.example.fawry.cart.*;
import com.example.fawry.exception.*;
import com.example.fawry.model.*;
import com.example.fawry.service.AuthenticationService;
import com.example.fawry.service.CheckoutService;
import com.example.fawry.service.ShippingService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ECommerceApp extends Application {

    // Services and Data
    private AuthenticationService authService;
    private CheckoutService checkoutService;
    private List<Product> inventory;
    private Cart cart;

    // UI State
    private Stage primaryStage;
    private Customer currentCustomer; // The currently logged-in customer

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
        this.primaryStage = primaryStage;
        initializeData();

        primaryStage.setTitle("E-Commerce Store");
        showLoginScreen(); // Start with the login screen
        primaryStage.show();
    }

    private void initializeData() {
        this.authService = new AuthenticationService();
        ShippingService shippingService = new ShippingService();
        this.checkoutService = new CheckoutService(shippingService);
        this.cart = new Cart();

        inventory = new ArrayList<>();
        inventory.add(new ElectronicProduct("Smart TV", 899.99, 15, 15.5));
        inventory.add(new ElectronicProduct("Laptop Pro", 1499.50, 8, 2.2));
        inventory.add(new FoodProduct("Artisan Cheese", 25.00, 30, LocalDate.now().plusMonths(2), 0.5));
        inventory.add(new FoodProduct("Organic Milk", 4.50, 50, LocalDate.now().plusDays(10), 1.0));
        inventory.add(new FoodProduct("Stale Crackers (Expired)", 1.99, 10, LocalDate.now().minusDays(1), 0.3));
    }

    private void showLoginScreen() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label sceneTitle = new Label("Welcome! Please Login or Sign Up");
        sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        // --- Fields ---
        grid.add(new Label("Username:"), 0, 1);
        TextField userTextField = new TextField();
        userTextField.setPromptText("jdoe");
        grid.add(userTextField, 1, 1);

        grid.add(new Label("Password:"), 0, 2);
        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("pass123");
        grid.add(pwBox, 1, 2);

        grid.add(new Label("Full Name (for signup):"), 0, 3);
        TextField nameField = new TextField();
        nameField.setPromptText("John Doe");
        grid.add(nameField, 1, 3);

        // --- Buttons ---
        Button loginBtn = new Button("Login");
        Button signUpBtn = new Button("Sign Up");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(signUpBtn, loginBtn);
        grid.add(hbBtn, 1, 4);

        final Label actionTarget = new Label();
        grid.add(actionTarget, 1, 6);

        // --- Event Handlers ---
        loginBtn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            Customer customer = authService.login(username, password);

            if (customer != null) {
                this.currentCustomer = customer;
                showStoreScreen(); // Transition to main store
            } else {
                actionTarget.setTextFill(Color.FIREBRICK);
                actionTarget.setText("Invalid username or password.");
            }
        });

        signUpBtn.setOnAction(e -> {
            String name = nameField.getText();
            String username = userTextField.getText();
            String password = pwBox.getText();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                actionTarget.setTextFill(Color.FIREBRICK);
                actionTarget.setText("All fields required for signup.");
                return;
            }

            boolean success = authService.register(name, username, password);
            if (success) {
                actionTarget.setTextFill(Color.GREEN);
                actionTarget.setText("Signup successful! Please log in.");
            } else {
                actionTarget.setTextFill(Color.FIREBRICK);
                actionTarget.setText("Username '" + username + "' is already taken.");
            }
        });


        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
    }

    private void showStoreScreen() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top: Customer Info with Logout
        customerInfoLabel = new Label();
        updateCustomerInfo();
        customerInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            this.currentCustomer = null;
            this.cart.clearCart();
            showLoginScreen();
        });
        HBox topBar = new HBox(20, customerInfoLabel, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        root.setTop(topBar);

        // Center: Product List
        ScrollPane productScrollPane = new ScrollPane();
        productListView = new VBox(10);
        productListView.setPadding(new Insets(10));
        productScrollPane.setContent(productListView);
        root.setCenter(productScrollPane);

        // Right: Shopping Cart
        VBox cartPane = createCartPane();
        root.setRight(cartPane);

        // Populate UI with data
        refreshProductList();
        updateCartView();

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
    }

    // This method creates the cart view. It's unchanged from the previous version.
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

    // Unchanged from previous version
    private void refreshProductList() {
        productListView.getChildren().clear();
        for (Product product : inventory) {
            productListView.getChildren().add(createProductView(product));
        }
    }

    // Unchanged from previous version
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
            if (product.getQuantity() >= quantityToAdd) {
                cart.addProduct(product, quantityToAdd);
                updateCartView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Stock Error", "Not enough stock available.");
            }
        });

        grid.add(nameLabel, 0, 0, 2, 1);
        grid.add(detailsLabel, 0, 1);
        grid.add(quantitySpinner, 0, 2);
        grid.add(addButton, 1, 2);

        return grid;
    }

    // Unchanged from previous version
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

    // Updated to use this.currentCustomer
    private void handleCheckout() {
        try {
            // Pass the currently logged-in customer to the service
            String receipt = checkoutService.processCheckout(this.currentCustomer, cart);
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

    // Updated to use this.currentCustomer
    private void updateCustomerInfo() {
        if (this.currentCustomer != null) {
            customerInfoLabel.setText(String.format("Customer: %s | Balance: $%.2f", this.currentCustomer.getName(), this.currentCustomer.getBalance()));
        }
    }

    // Unchanged from previous version
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}