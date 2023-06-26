import java.time.LocalDate;
import java.util.*;

class User {
    private String name;
    private int age;

    private User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static User createUser(String name, int age) {
        return new User(name, age);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", age=" + age + "]";
    }
}

abstract class Product {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product [name=" + name + ", price=" + price + "]";
    }
}

class RealProduct extends Product {
    private int size;
    private int weight;

    public RealProduct(String name, double price, int size, int weight) {
        super(name, price);
        this.size = size;
        this.weight = weight;
    }

    public int getSize() {
        return size;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "RealProduct [name=" + getName() + ", price=" + getPrice() + ", size=" + size + ", weight=" + weight
                + "]";
    }
}

class VirtualProduct extends Product {
    private String code;
    private LocalDate expirationDate;

    public VirtualProduct(String name, double price, String code, LocalDate expirationDate) {
        super(name, price);
        this.code = code;
        this.expirationDate = expirationDate;
    }

    public String getCode() {
        return code;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public String toString() {
        return "VirtualProduct [name=" + getName() + ", price=" + getPrice() + ", code=" + code + ", expirationDate="
                + expirationDate + "]";
    }
}

class ProductFactory {
    public static RealProduct createRealProduct(String name, double price, int size, int weight) {
        return new RealProduct(name, price, size, weight);
    }

    public static VirtualProduct createVirtualProduct(String name, double price, String code, LocalDate expirationDate) {
        return new VirtualProduct(name, price, code, expirationDate);
    }
}

class Order {
    private User user;
    private List<Product> products;

    private Order(User user, List<Product> products) {
        this.user = user;
        this.products = products;
    }

    public static Order createOrder(User user, List<Product> products) {
        return new Order(user, products);
    }

    public User getUser() {
        return user;
    }

    public List<Product> getProducts() {
        return products;
    }

    @Override
    public String toString() {
        return "Order [user=" + user + ", products=" + products + "]";
    }
}

class VirtualProductCodeManager {
    private static VirtualProductCodeManager instance;
    private Set<String> usedCodes;

    private VirtualProductCodeManager() {
        usedCodes = new HashSet<>();
    }

    public static VirtualProductCodeManager getInstance() {
        if (instance == null) {
            instance = new VirtualProductCodeManager();
        }
        return instance;
    }

    public void useCode(String code) {
        usedCodes.add(code);
    }

    public boolean isCodeUsed(String code) {
        return usedCodes.contains(code);
    }
}


public class Main {
    public static void main(String[] args) {
        User user1 = User.createUser("Alice", 32);
        User user2 = User.createUser("Bob", 19);
        User user3 = User.createUser("Charlie", 20);
        User user4 = User.createUser("John", 27);

        Product realProduct1 = ProductFactory.createRealProduct("Product A", 20.50, 10, 25);
        Product realProduct2 = ProductFactory.createRealProduct("Product B", 50, 6, 17);

        Product virtualProduct1 = ProductFactory.createVirtualProduct("Product C", 100, "xxx",
                LocalDate.of(2023, 5, 12));
        Product virtualProduct2 = ProductFactory.createVirtualProduct("Product D", 81.25, "yyy",
                LocalDate.of(2024, 6, 20));

        List<Order> orders = new ArrayList<>();
        try {
            orders.add(Order.createOrder(user1, List.of(realProduct1, virtualProduct1, virtualProduct2)));
            orders.add(Order.createOrder(user2, List.of(realProduct1, realProduct2)));
            orders.add(Order.createOrder(user3, List.of(realProduct1, virtualProduct2)));
            orders.add(Order.createOrder(user4, List.of(virtualProduct1, virtualProduct2, realProduct1, realProduct2)));
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating an order: " + e.getMessage());
        }

        VirtualProductCodeManager virtualProductCodeManager = VirtualProductCodeManager.getInstance();
        System.out.println("1. Create singleton class VirtualProductCodeManager \n");
        virtualProductCodeManager.useCode("xxx");
        boolean isCodeUsed1 = virtualProductCodeManager.isCodeUsed("xxx");
        boolean isCodeUsed2 = virtualProductCodeManager.isCodeUsed("yyy");
        System.out.println("Is code used: " + isCodeUsed1);
        System.out.println("Is code used: " + isCodeUsed2);

        Product mostExpensive = getMostExpensiveProduct(orders);
        System.out.println("2. Most expensive product: " + mostExpensive);

        Product mostPopular = getMostPopularProduct(orders);
        System.out.println("3. Most popular product: " + mostPopular);

        double averageAge = calculateAverageAge(realProduct2, orders);
        System.out.println("4. Average age of users who bought realProduct2 is: " + averageAge);

        Map<Product, List<User>> productUserMap = getProductUserMap(orders);
        System.out.println("5. Map with products as keys and list of users as value \n");
        productUserMap.forEach((key, value) -> System.out.println("key: " + key + " " + "value: " + value));

        List<Product> productsByPrice = sortProductsByPrice(List.of(realProduct1, realProduct2, virtualProduct1,
                virtualProduct2));
        System.out.println("6. a) List of products sorted by price: " + productsByPrice);

        List<Order> ordersByUserAgeDesc = sortOrdersByUserAgeDesc(orders);
        System.out.println("6. b) List of orders sorted by user age in descending order: " + ordersByUserAgeDesc);

        Map<Order, Integer> result = calculateWeightOfEachOrder(orders);
        System.out.println("7. Calculate the total weight of each order \n");
        result.forEach((key, value) -> System.out.println("order: " + key + " " + "total weight: " + value));
    }

    public static Product getMostExpensiveProduct(List<Order> orders) {
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("Orders list is empty");
        }

        Product mostExpensiveProduct = null;
        double maxPrice = Double.MIN_VALUE;

        for (Order order : orders) {
            List<Product> products = order.getProducts();
            for (Product product : products) {
                if (product.getPrice() > maxPrice) {
                    maxPrice = product.getPrice();
                    mostExpensiveProduct = product;
                }
            }
        }

        if (mostExpensiveProduct == null) {
            throw new IllegalArgumentException("No products found in the orders");
        }

        return mostExpensiveProduct;
    }

    public static Product getMostPopularProduct(List<Order> orders) {
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("Orders list is empty");
        }

        Map<Product, Integer> productCountMap = new HashMap<>();

        for (Order order : orders) {
            List<Product> products = order.getProducts();
            for (Product product : products) {
                productCountMap.put(product, productCountMap.getOrDefault(product, 0) + 1);
            }
        }

        int maxCount = 0;
        Product mostPopularProduct = null;

        for (Map.Entry<Product, Integer> entry : productCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostPopularProduct = entry.getKey();
            }
        }

        if (mostPopularProduct == null) {
            throw new IllegalArgumentException("No products found in the orders");
        }

        return mostPopularProduct;
    }

    public static double calculateAverageAge(Product product, List<Order> orders) {
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("Orders list is empty");
        }

        int sumAge = 0;
        int count = 0;

        for (Order order : orders) {
            User user = order.getUser();
            List<Product> products = order.getProducts();
            if (products.contains(product)) {
                sumAge += user.getAge();
                count++;
            }
        }

        if (count == 0) {
            throw new IllegalArgumentException("Product not found in any order");
        }

        return (double) sumAge / count;
    }

    public static Map<Product, List<User>> getProductUserMap(List<Order> orders) {
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("Orders list is empty");
        }

        Map<Product, List<User>> productUserMap = new HashMap<>();

        for (Order order : orders) {
            User user = order.getUser();
            List<Product> products = order.getProducts();

            for (Product product : products) {
                List<User> userList = productUserMap.getOrDefault(product, new ArrayList<>());
                userList.add(user);
                productUserMap.put(product, userList);
            }
        }

        return productUserMap;
    }

    public static List<Product> sortProductsByPrice(List<Product> products) {
        if (products.isEmpty()) {
            throw new IllegalArgumentException("Products list is empty");
        }

        List<Product> sortedProducts = new ArrayList<>(products);
        sortedProducts.sort(Comparator.comparingDouble(Product::getPrice));

        return sortedProducts;
    }

    public static List<Order> sortOrdersByUserAgeDesc(List<Order> orders) {
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("Orders list is empty");
        }

        List<Order> sortedOrders = new ArrayList<>(orders);
        sortedOrders.sort((o1, o2) -> Integer.compare(o2.getUser().getAge(), o1.getUser().getAge()));

        return sortedOrders;
    }

    public static Map<Order, Integer> calculateWeightOfEachOrder(List<Order> orders) {
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("Orders list is empty");
        }

        Map<Order, Integer> orderWeightMap = new HashMap<>();

        for (Order order : orders) {
            List<Product> products = order.getProducts();
            int totalWeight = 0;

            for (Product product : products) {
                if (product instanceof RealProduct) {
                    totalWeight += ((RealProduct) product).getWeight();
                }
            }

            orderWeightMap.put(order, totalWeight);
        }

        return orderWeightMap;
    }
}
