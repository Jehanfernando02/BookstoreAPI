package com.bookstore.storage;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryStorage {
    private static final Map<String, Author> authors = new HashMap<>();
    private static final Map<String, Book> books = new HashMap<>();
    private static final Map<String, Customer> customers = new HashMap<>();
    private static final Map<String, Map<String, Integer>> carts = new HashMap<>();
    private static final Map<String, Order> orders = new HashMap<>();
    private static final AtomicLong counter = new AtomicLong(0);

    public static Map<String, Author> getAuthors() {
        return authors;
    }

    public static Map<String, Book> getBooks() {
        return books;
    }

    public static Map<String, Customer> getCustomers() {
        return customers;
    }

    public static Map<String, Map<String, Integer>> getCarts() {
        return carts;
    }

    public static Map<String, Order> getOrders() {
        return orders;
    }

    public static void addAuthor(Author author) {
        authors.put(author.getId(), author);
    }

    public static void addBook(Book book) {
        books.put(book.getId(), book);
    }

    public static void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    public static void addOrder(Order order) {
        orders.put(order.getId(), order);
    }

    public static synchronized String generateId(String entityType) {
        return entityType + (counter.incrementAndGet());
    }
}