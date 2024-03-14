package mattiaconsiglio;

import com.github.javafaker.Faker;
import mattiaconsiglio.shop.Customer;
import mattiaconsiglio.shop.Order;
import mattiaconsiglio.shop.Product;

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
    }

    public static Map<String, List<Order>> OrdersByCustomer(List<Order> orders) {
        return orders.stream().collect(Collectors.groupingBy(order -> order.getCustomer().getName()));
    }

    public static Map<String, Double> OrderTotalByCustomer(List<Order> orders) {
        return orders.stream().collect(Collectors.groupingBy(order -> order.getCustomer().getName(), Collectors.summingDouble(order -> order.getProducts().stream().mapToDouble(Product::getPrice).sum())));
    }

    public static List<Product> TopTenExpesiveProducts(List<Product> products) {
        return products.stream().sorted(Comparator.comparingDouble(Product::getPrice).reversed()).limit(10).toList();
    }

    public static double OrdersAverage(List<Order> orders) {
        return orders.stream().mapToDouble(order -> order.getProducts().stream().mapToDouble(Product::getPrice).sum()).average().getAsDouble();
    }

}
