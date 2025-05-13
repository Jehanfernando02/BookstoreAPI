package com.bookstore;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.storage.InMemoryStorage;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting Bookstore API server...");

        // Configure Jetty server
        Server server = new Server(8080);
        LOGGER.info("Jetty server configured on port 8080");

        // Set up servlet context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        LOGGER.info("Servlet context set with root path /");

        // Configure Jersey servlet for /api/*
        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer());
        jerseyServlet.setInitParameter("javax.ws.rs.Application", BookstoreApplication.class.getName());
        context.addServlet(jerseyServlet, "/api/*");
        LOGGER.info("Jersey servlet registered for /api/*");

        server.setHandler(context);

        // Start the server
        try {
            server.start();
            LOGGER.info("Server started successfully");
            initializeData();
            LOGGER.info("Sample data initialized");
            server.join();
        } catch (Exception e) {
            LOGGER.severe("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (server.isStarted()) {
                    server.stop();
                    LOGGER.info("Server stopped");
                }
                server.destroy();
                LOGGER.info("Server destroyed");
            } catch (Exception e) {
                LOGGER.severe("Error during server shutdown: " + e.getMessage());
            }
        }
    }

    private static void initializeData() {
        LOGGER.info("Initializing sample data...");
        try {
            Author author1 = new Author("author1", "John", "Doe");
            Author author2 = new Author("author2", "Jane", "Smith");
            Author author3 = new Author("author3", "Emily", "Brown");
            InMemoryStorage.getAuthors().put(author1.getId(), author1);
            InMemoryStorage.getAuthors().put(author2.getId(), author2);
            InMemoryStorage.getAuthors().put(author3.getId(), author3);

            Book book1 = new Book("book1", "The Great Gatsby", "author1", "978-0743273565", 1925, 9.99, 10);
            Book book2 = new Book("book2", "1984", "author2", "978-0451524935", 1949, 12.99, 5);
            Book book3 = new Book("book3", "Pride and Prejudice", "author3", "978-0141439518", 1813, 7.99, 8);
            InMemoryStorage.getBooks().put(book1.getId(), book1);
            InMemoryStorage.getBooks().put(book2.getId(), book2);
            InMemoryStorage.getBooks().put(book3.getId(), book3);

            Customer customer1 = new Customer("customer1", "Test", "User", "test@bookstore.com", "test12345678");
            InMemoryStorage.getCustomers().put(customer1.getId(), customer1);
            LOGGER.info("Sample data initialized successfully");
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize data: " + e.getMessage());
            throw e;
        }
    }
}