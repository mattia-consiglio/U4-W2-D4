package mattiaconsiglio;

import com.github.javafaker.Faker;
import mattiaconsiglio.shop.Customer;
import mattiaconsiglio.shop.Order;
import mattiaconsiglio.shop.Product;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) {
        int quantity = 20;

        Supplier<Product> productSupplier = () -> {
            String[] categories = {"Books", "Food", "Boys", "Baby"};
            Faker faker = new Faker();

            return new Product(faker.book().title(), categories[new Random().nextInt(0, 4)]);
        };


        List<Product> products = new ArrayList<Product>();

        for (int i = 0; i < quantity; i++) {
            products.add(productSupplier.get());
        }

        Supplier<Customer> customerSupplier = () -> {
            Faker faker = new Faker(Locale.ITALIAN);

            return new Customer(faker.name().name(), new Random().nextInt(1, 4));
        };


        List<Customer> customers = new ArrayList<Customer>();

        for (int i = 0; i < quantity; i++) {
            customers.add(customerSupplier.get());
        }

        Supplier<Order> orderSupplier = () -> {


            Order order = new Order("created", LocalDate.parse("2021-02-01"), LocalDate.parse("2021-02-05"), customers.get(new Random().nextInt(0, customers.size())));
            int n = new Random().nextInt(1, 5);
            for (int i = 0; i < n; i++) {

                order.addProduct(products.get(new Random().nextInt(0, products.size())));
            }
            return order;
        };


        List<Order> orders = new ArrayList<Order>();

        for (int i = 0; i < quantity; i++) {
            orders.add(orderSupplier.get());
        }

        System.out.println("Orders");
        orders.forEach(System.out::println);

        System.out.println("-------------------------------- EXERCISE 1 --------------------------------");

        OrdersByCustomer(orders).forEach((name, customerOrders) -> System.out.println("Customer: " + name + " " + customerOrders));

        System.out.println("-------------------------------- EXERCISE 2 --------------------------------");

        OrderTotalByCustomer(orders).forEach((name, total) -> System.out.println("Customer: " + name + ", Total: " + total));

        System.out.println("-------------------------------- EXERCISE 3 --------------------------------");

        TopTenExpesiveProducts(products).forEach(System.out::println);


        System.out.println("-------------------------------- EXERCISE 4 --------------------------------");

        System.out.println("Orders average: " + OrdersAverage(orders));

        System.out.println("-------------------------------- EXERCISE 5 --------------------------------");

        ProductsSumByCategory(products).forEach((category, total) -> System.out.println("Category: " + category + " Total: " + total));

        SaveProductsOnDisk(products);
        System.out.println("-------------------------------- EXERCISE 7 --------------------------------");
        List<Product> productsFromFile = ReadProductsOnDisk();
        productsFromFile.forEach(System.out::println);

    }

    public static Map<String, List<Order>> OrdersByCustomer(List<Order> orders) {
        return orders.stream().
                collect(
                        Collectors.groupingBy(order -> order.getCustomer().getName())
                );
    }

    public static Map<String, Double> OrderTotalByCustomer(List<Order> orders) {
        return orders.stream().
                collect(
                        Collectors.groupingBy(
                                order -> order.getCustomer().getName(),
                                Collectors.summingDouble(
                                        order -> order.getProducts().stream().mapToDouble(Product::getPrice).sum()
                                )
                        )
                );
    }

    public static List<Product> TopTenExpesiveProducts(List<Product> products) {
        return products.stream().sorted(
                Comparator.comparingDouble(Product::getPrice).reversed()
        ).limit(10).toList();
    }

    public static double OrdersAverage(List<Order> orders) {
        return orders.stream()
                .mapToDouble(
                        order ->
                                order.getProducts().stream().
                                        mapToDouble(Product::getPrice).sum()
                ).average().getAsDouble();
    }

    public static Map<String, Double> ProductsSumByCategory(List<Product> products) {
        return products.stream().collect(Collectors.groupingBy(Product::getCategory,
                Collectors.summingDouble(Product::getPrice)));
    }

    public static void SaveProductsOnDisk(List<Product> products) {
        File file = new File("src/products.txt");
        try {
            FileUtils.writeStringToFile(file, "", StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


        products.forEach(product -> {
            try {
                FileUtils.writeStringToFile(file, product.getName() + "@" + product.getCategory() + "@" + product.getPrice() + System.lineSeparator(), StandardCharsets.UTF_8, true);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });

    }

    public static List<Product> ReadProductsOnDisk() {
        List<Product> products = new ArrayList();
        File file = new File("src/products.txt");
        if (file.canRead()) {
            try {
                String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

                Arrays.stream(fileContent.split(System.lineSeparator())).forEach(string -> {
                    String[] productArray = string.split("@");
                    products.add(new Product(productArray[0], productArray[1], Double.parseDouble(productArray[2])));
                });
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return products;
    }
}
