package com.bookstore.storage;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryStorage {

    private static final Map<String, Book> books = new ConcurrentHashMap<>();
    private static final Map<String, Author> authors = new ConcurrentHashMap<>();
    private static final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private static final Map<String, Cart> carts = new ConcurrentHashMap<>();
    private static final Map<String, Order> orders = new ConcurrentHashMap<>();

    private static final AtomicLong authorCounter = new AtomicLong(0);
    private static final AtomicLong bookCounter = new AtomicLong(0);
    private static final AtomicLong customerCounter = new AtomicLong(0);
    private static final AtomicLong cartCounter = new AtomicLong(0);
    private static final AtomicLong orderCounter = new AtomicLong(0);

    public static Map<String, Book> getBooks() {
        return books;
    }

    public static Map<String, Author> getAuthors() {
        return authors;
    }

    public static Map<String, Customer> getCustomers() {
        return customers;
    }

    public static Map<String, Cart> getCarts() {
        return carts;
    }

    public static Map<String, Order> getOrders() {
        return orders;
    }

    public static String generateId(String entityType) {
        switch (entityType.toLowerCase()) {
            case "author":
                return "author" + authorCounter.incrementAndGet();
            case "book":
                return "book" + bookCounter.incrementAndGet();
            case "customer":
                return "customer" + customerCounter.incrementAndGet();
            case "cart":
                return "cart" + cartCounter.incrementAndGet();
            case "order":
                return "order" + orderCounter.incrementAndGet();
            default:
                throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
    }
}