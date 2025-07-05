import java.util.*;

// Interface for shippable items
interface Shippable {
    String getName();
    double getWeight();
}

// Base Product class
abstract class Product {
    protected String name;
    protected double price;
    protected int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void reduceQuantity(int amount) { this.quantity -= amount; }
    public abstract boolean isExpired();
    public abstract boolean isShippable();
}

// Expirable and shippable product (e.g., Cheese)
class ExpirableShippableProduct extends Product implements Shippable {
    private boolean expired;
    private double weight;

    public ExpirableShippableProduct(String name, double price, int quantity, boolean expired, double weight) {
        super(name, price, quantity);
        this.expired = expired;
        this.weight = weight;
    }

    @Override
    public boolean isExpired() { return expired; }
    @Override
    public boolean isShippable() { return true; }
    @Override
    public double getWeight() { return weight; }
}

// Non-expirable, shippable product (e.g., TV)
class ShippableProduct extends Product implements Shippable {
    private double weight;

    public ShippableProduct(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.weight = weight;
    }

    @Override
    public boolean isExpired() { return false; }
    @Override
    public boolean isShippable() { return true; }
    @Override
    public double getWeight() { return weight; }
}

// Expirable, shippable product (e.g., Biscuits)
class ExpirableProduct extends Product implements Shippable {
    private boolean expired;
    private double weight;

    public ExpirableProduct(String name, double price, int quantity, boolean expired, double weight) {
        super(name, price, quantity);
        this.expired = expired;
        this.weight = weight;
    }

    @Override
    public boolean isExpired() { return expired; }
    @Override
    public boolean isShippable() { return true; }
    @Override
    public double getWeight() { return weight; }
}

// Non-expirable, non-shippable product (e.g., Mobile scratch card)
class SimpleProduct extends Product {
    public SimpleProduct(String name, double price, int quantity) {
        super(name, price, quantity);
    }

    @Override
    public boolean isExpired() { return false; }
    @Override
    public boolean isShippable() { return false; }
}

class CartItem {
    public Product product;
    public int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}

class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void addProduct(Product product, int quantity) throws Exception {
        if (quantity > product.getQuantity()) throw new Exception("Not enough stock.");
        items.add(new CartItem(product, quantity));
    }

    public List<CartItem> getItems() { return items; }
    public boolean isEmpty() { return items.isEmpty(); }
}

class Customer {
    private String name;
    private double balance;
    private Cart cart = new Cart();

    public Customer(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public Cart getCart() { return cart; }
    public double getBalance() { return balance; }
    public void deductBalance(double amount) { balance -= amount; }
}

class ShippingService {
    public static void shipItems(List<CartItem> cartItems) {
        System.out.println("** Shipment notice **");
        double totalWeight = 0;
        for (CartItem item : cartItems) {
            Product p = item.product;
            if (p.isShippable()) {
                double totalItemWeight = ((Shippable)p).getWeight() * item.quantity;
                System.out.println(item.quantity + "x " + p.getName() + " " + (int)(totalItemWeight * 1000) + "g");
                totalWeight += totalItemWeight;
            }
        }
        System.out.println("Total package weight " + String.format("%.1f", totalWeight) + "kg");
    }
}

class CheckoutService {
    private static final double SHIPPING_RATE_PER_KG = 30.0;
    public static void checkout(Customer customer) throws Exception {
        Cart cart = customer.getCart();
        if (cart.isEmpty()) throw new Exception("Cart is empty.");
        double subtotal = 0;
        double shippingWeight = 0;
        boolean hasShippable = false;
        for (CartItem item : cart.getItems()) {
            Product p = item.product;
            if (item.quantity > p.getQuantity()) throw new Exception("Product out of stock: " + p.getName());
            if (p.isExpired()) throw new Exception("Product expired: " + p.getName());
            subtotal += p.getPrice() * item.quantity;
            if (p.isShippable()) {
                shippingWeight += ((Shippable)p).getWeight() * item.quantity;
                hasShippable = true;
            }
        }
        double shippingFees = shippingWeight * SHIPPING_RATE_PER_KG;
        double total = subtotal + shippingFees;
        if (customer.getBalance() < total) throw new Exception("Insufficient balance.");
        for (CartItem item : cart.getItems()) {
            item.product.reduceQuantity(item.quantity);
        }
        customer.deductBalance(total);
        // Ship items first if needed
        if (hasShippable) {
            ShippingService.shipItems(cart.getItems());
        }
        System.out.println("** Checkout receipt **");
        for (CartItem item : cart.getItems()) {
            System.out.println(item.quantity + "x " + item.product.getName() + " " + (int)(item.product.getPrice() * item.quantity));
        }
        System.out.println("----------------------");
        System.out.println("Subtotal " + (int)subtotal);
        System.out.println("Shipping " + (int)shippingFees);
        System.out.println("Amount " + (int)total);
    }
}

public class ECommerceApp {
    public static void main(String[] args) {
        // Sample products with correct weights and prices
        Product cheese = new ExpirableShippableProduct("Cheese", 100, 10, false, 0.2); // 200g, 100 per unit
        Product biscuits = new ExpirableProduct("Biscuits", 150, 20, false, 0.7); // 150 per unit, 700g, shippable
        Product tv = new ShippableProduct("TV", 1500, 1, 10.0); // 150 per unit, 700g, shippable
        Product mobileCard = new SimpleProduct("Mobile Scratch Card", 5, 2); // 100g, shippable
        
        try {
            // Test 1: Valid order
            System.out.println("=== Test 1: Valid Order ===");
            Customer customer = new Customer("Alice", 5000);
            customer.getCart().addProduct(cheese, 2);
            customer.getCart().addProduct(biscuits, 1);
            customer.getCart().addProduct(tv, 1);
            CheckoutService.checkout(customer);
            
            System.out.println("\n=== Test 2: Invalid Order - Quantity Exceeds Stock ===");
            // Create new customer for second test
            Customer customer2 = new Customer("Bob", 5000);
            
            // Try to order more TV than available (only 1 in stock, trying to order 3)
            customer2.getCart().addProduct(tv, 3);
            CheckoutService.checkout(customer2);

        } catch (Exception e) {
            System.out.println("Checkout error: " + e.getMessage());
        }
        
        // Test 3: Another invalid order scenario
        try {
            System.out.println("\n=== Test 3: Invalid Order - Product Out of Stock During Checkout ===");
            Customer customer3 = new Customer("Charlie", 5000);
            
            // Add valid quantity to cart
            customer3.getCart().addProduct(cheese, 5);
            
            // But another customer buys the remaining stock first
            Customer customer4 = new Customer("David", 1000);
            customer4.getCart().addProduct(cheese, 6); // This will leave only 4 in stock
            CheckoutService.checkout(customer4);
            
            // Now try to checkout with customer3 (who has 5 in cart but only 4 left in stock)
            CheckoutService.checkout(customer3);
            
        } catch (Exception e) {
            System.out.println("Checkout error: " + e.getMessage());
        }
    }
} 