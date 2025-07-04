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
import java.util.Optional; // Needed for the confirmation dialog

public class ECommerceApp extends Application {

    // --- UI Style Constants (Unchanged) ---
    private static final String FONT_FAMILY = "System";
    private static final Color PRIMARY_BRAND_COLOR = Color.web("#003366");
    private static final Color ACCENT_BRAND_COLOR = Color.web("#FFC107");
    private static final Color LIGHT_BACKGROUND_COLOR = Color.web("#F4F6F8");
    private static final Color TEXT_COLOR = Color.web("#333333");

    private static final String BUTTON_STYLE_PRIMARY = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-family: '%s'; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15;",
            toHex(ACCENT_BRAND_COLOR), FONT_FAMILY
    );

    private static final String BUTTON_STYLE_SECONDARY = String.format(
            "-fx-background-color: transparent; -fx-text-fill: %s; -fx-font-family: '%s'; -fx-font-weight: bold; -fx-border-color: %s; -fx-border-width: 2; -fx-border-radius: 20; -fx-padding: 6 15 6 15;",
            toHex(PRIMARY_BRAND_COLOR), FONT_FAMILY, toHex(PRIMARY_BRAND_COLOR)
    );

    // Unchanged sections...
    private static final String TEXT_FIELD_STYLE = String.format(
            "-fx-font-family: '%s'; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-padding: 8;", FONT_FAMILY);
    private static final String CARD_STYLE = "-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);";
    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }
    private AuthenticationService authService;
    private CheckoutService checkoutService;
    private List<Product> inventory;
    private Cart cart;
    private Stage primaryStage;
    private Customer currentCustomer;
    private VBox productListView;
    private VBox cartView;
    private Label subtotalLabel;
    private Label customerInfoLabel;

    public static void main(String[] args) {
        launch(args);
    }

    // All methods from start() to showStoreScreen() are unchanged...
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeData();

        primaryStage.setTitle("Fawry Store");
        showLoginScreen();
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
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.setStyle("-fx-background-color: '" + toHex(LIGHT_BACKGROUND_COLOR) + "';");

        Label sceneTitle = new Label("Welcome to the Store");
        sceneTitle.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 28));
        sceneTitle.setTextFill(PRIMARY_BRAND_COLOR);
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font(FONT_FAMILY, FontWeight.MEDIUM, 14));
        grid.add(userLabel, 0, 1);
        TextField userTextField = new TextField();
        userTextField.setPromptText("john_doe");
        userTextField.setStyle(TEXT_FIELD_STYLE);
        grid.add(userTextField, 1, 1);

        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font(FONT_FAMILY, FontWeight.MEDIUM, 14));
        grid.add(passLabel, 0, 2);
        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("password123");
        pwBox.setStyle(TEXT_FIELD_STYLE);
        grid.add(pwBox, 1, 2);

        Label nameLabel = new Label("Full Name (for signup):");
        nameLabel.setFont(Font.font(FONT_FAMILY, FontWeight.MEDIUM, 14));
        grid.add(nameLabel, 0, 3);
        TextField nameField = new TextField();
        nameField.setPromptText("John Doe");
        nameField.setStyle(TEXT_FIELD_STYLE);
        grid.add(nameField, 1, 3);

        Button loginBtn = new Button("Login");
        loginBtn.setStyle(BUTTON_STYLE_PRIMARY);
        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setStyle(BUTTON_STYLE_SECONDARY);
        addHoverEffect(loginBtn, BUTTON_STYLE_PRIMARY);
        addHoverEffect(signUpBtn, BUTTON_STYLE_SECONDARY);

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(signUpBtn, loginBtn);
        grid.add(hbBtn, 1, 4);

        final Label actionTarget = new Label();
        actionTarget.setFont(Font.font(FONT_FAMILY));
        grid.add(actionTarget, 0, 6, 2, 1);

        loginBtn.setOnAction(e -> {
            String username = userTextField.getText().trim();
            String password = pwBox.getText().trim();
            Customer customer = authService.login(username, password);
            if (customer != null) {
                this.currentCustomer = customer;
                showStoreScreen();
            } else {
                actionTarget.setTextFill(Color.FIREBRICK);
                actionTarget.setText("Invalid username or password.");
            }
        });

        signUpBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String username = userTextField.getText().trim();
            String password = pwBox.getText().trim();
            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                actionTarget.setTextFill(Color.FIREBRICK);
                actionTarget.setText("All fields are required for signup.");
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

        Scene scene = new Scene(grid, 500, 450);
        primaryStage.setScene(scene);
    }

    private void showStoreScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: '" + toHex(LIGHT_BACKGROUND_COLOR) + "';");

        customerInfoLabel = new Label();
        updateCustomerInfo();
        customerInfoLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 16));
        customerInfoLabel.setTextFill(PRIMARY_BRAND_COLOR);
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(BUTTON_STYLE_SECONDARY);
        addHoverEffect(logoutButton, BUTTON_STYLE_SECONDARY);

        logoutButton.setOnAction(e -> {
            this.currentCustomer = null;
            this.cart.clearCart();
            showLoginScreen();
        });

        HBox topBar = new HBox(20, customerInfoLabel, logoutButton);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        root.setTop(topBar);

        ScrollPane productScrollPane = new ScrollPane();
        productScrollPane.setFitToWidth(true);
        productScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        productListView = new VBox(15);
        productListView.setPadding(new Insets(20));
        productScrollPane.setContent(productListView);
        root.setCenter(productScrollPane);

        VBox cartPane = createCartPane();
        root.setRight(cartPane);

        refreshProductList();
        updateCartView();

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }

    // --- createCartPane() is UPDATED ---
    private VBox createCartPane() {
        VBox cartPane = new VBox(15);
        cartPane.setPadding(new Insets(20));
        cartPane.setStyle("-fx-background-color: white;");
        cartPane.setMinWidth(300);

        Label cartTitle = new Label("Shopping Cart");
        cartTitle.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 22));
        cartTitle.setTextFill(PRIMARY_BRAND_COLOR);

        cartView = new VBox(8);
        cartView.getChildren().add(new Label("Your cart is empty."));

        subtotalLabel = new Label("Subtotal: $0.00");
        subtotalLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 16));
        subtotalLabel.setTextFill(TEXT_COLOR);

        // --- NEW: Clear Cart Button ---
        Button clearCartButton = new Button("Clear Cart");
        clearCartButton.setStyle(BUTTON_STYLE_SECONDARY);
        addHoverEffect(clearCartButton, BUTTON_STYLE_SECONDARY);
        clearCartButton.setMaxWidth(Double.MAX_VALUE);

        // --- NEW: Event handler with confirmation ---
        clearCartButton.setOnAction(e -> {
            if (cart.getItems().isEmpty()) return; // Do nothing if cart is already empty

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Clear Cart");
            confirmation.setHeaderText("Remove all items from cart?");
            confirmation.setContentText("This action cannot be undone.");
            styleAlert(confirmation);

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                cart.clearCart();
                updateCartView();
            }
        });

        Button checkoutButton = new Button("Checkout");
        checkoutButton.setStyle(BUTTON_STYLE_PRIMARY);
        addHoverEffect(checkoutButton, BUTTON_STYLE_PRIMARY);
        checkoutButton.setMaxWidth(Double.MAX_VALUE);
        checkoutButton.setOnAction(e -> handleCheckout());

        // Add the new button to the layout
        cartPane.getChildren().addAll(
            cartTitle, new Separator(), cartView, new VBox(), subtotalLabel, clearCartButton, checkoutButton
        );
        VBox.setVgrow(cartView, Priority.ALWAYS);

        return cartPane;
    }

    // Unchanged methods...
    private void refreshProductList() {
        productListView.getChildren().clear();
        for (Product product : inventory) {
            productListView.getChildren().add(createProductView(product));
        }
    }

    private Node createProductView(Product product) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(15));
        grid.setStyle(CARD_STYLE);

        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 18));
        nameLabel.setTextFill(TEXT_COLOR);

        Label detailsLabel = new Label(String.format("$%.2f - Stock: %d", product.getPrice(), product.getQuantity()));
        detailsLabel.setFont(Font.font(FONT_FAMILY, 14));
        detailsLabel.setTextFill(TEXT_COLOR);

        Spinner<Integer> quantitySpinner = new Spinner<>(1, product.getQuantity() > 0 ? product.getQuantity() : 1, 1);
        quantitySpinner.setPrefWidth(75);
        quantitySpinner.setDisable(product.getQuantity() <= 0);
        quantitySpinner.setStyle("-fx-font-family: '" + FONT_FAMILY + "';");

        Button addButton = new Button("Add to Cart");
        addButton.setStyle(BUTTON_STYLE_PRIMARY);
        addHoverEffect(addButton, BUTTON_STYLE_PRIMARY);
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

        HBox actionBox = new HBox(10, quantitySpinner, addButton);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        grid.add(actionBox, 0, 2, 2, 1);

        return grid;
    }

    private void updateCartView() {
        cartView.getChildren().clear();
        Map<Product, Integer> items = cart.getItems();

        if (items.isEmpty()) {
            Label emptyLabel = new Label("Your cart is empty.");
            emptyLabel.setFont(Font.font(FONT_FAMILY));
            cartView.getChildren().add(emptyLabel);
        } else {
            for (Map.Entry<Product, Integer> entry : items.entrySet()) {
                String itemText = String.format("%s (x%d)", entry.getKey().getName(), entry.getValue());
                double itemTotal = entry.getKey().getPrice() * entry.getValue();
                Label itemLabel = new Label(itemText);
                itemLabel.setFont(Font.font(FONT_FAMILY, 14));
                Label priceLabel = new Label(String.format("$%.2f", itemTotal));
                priceLabel.setFont(Font.font(FONT_FAMILY));
                BorderPane itemPane = new BorderPane(null, null, priceLabel, null, itemLabel);
                cartView.getChildren().add(itemPane);
            }
        }

        double subtotal = checkoutService.calculateSubtotal(cart);
        subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
    }

    private void handleCheckout() {
        try {
            String receipt = checkoutService.processCheckout(this.currentCustomer, cart);
            showAlert(Alert.AlertType.INFORMATION, "Checkout Successful", receipt);
            cart.clearCart();
            updateCartView();
            refreshProductList();
            updateCustomerInfo();
        } catch (EmptyCartException | InsufficientStockException | ProductExpiredException | InsufficientBalanceException e) {
            showAlert(Alert.AlertType.ERROR, "Checkout Failed", e.getMessage());
        }
    }

    private void updateCustomerInfo() {
        if (this.currentCustomer != null) {
            customerInfoLabel.setText(String.format("Welcome, %s! | Balance: $%.2f", this.currentCustomer.getName(), this.currentCustomer.getBalance()));
        }
    }

    // --- NEW: Renamed showAlert to styleAlert for reuse ---
    private void styleAlert(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-font-family: '" + FONT_FAMILY + "';");
        dialogPane.getButtonTypes().stream()
                .map(dialogPane::lookupButton)
                .forEach(button -> {
                    if (button instanceof Button) {
                       // Style the main action button as primary
                       if (((Button) button).isDefaultButton()) {
                           button.setStyle(BUTTON_STYLE_PRIMARY);
                       } else {
                           button.setStyle(BUTTON_STYLE_SECONDARY);
                       }
                    }
                });
    }

    // --- showAlert now uses the styleAlert helper ---
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    private void addHoverEffect(Button button, String baseStyle) {
        button.setOnMouseEntered(e -> button.setStyle(baseStyle + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 1);"));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
    }
}