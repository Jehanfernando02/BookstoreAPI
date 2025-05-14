package com.bookstore;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.storage.InMemoryStorage;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting Bookstore API server...");

        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer());
        jerseyServlet.setInitParameter("javax.ws.rs.Application", BookstoreApplication.class.getName());
        context.addServlet(jerseyServlet, "/api/*");

        server.setHandler(context);

        try {
            server.start();
            LOGGER.info("Server started on port 8080");
            initializeData();
            server.join();
        } catch (Exception e) {
            LOGGER.severe("Server failed: " + e.getMessage());
        } finally {
            try {
                server.stop();
                server.destroy();
            } catch (Exception e) {
                LOGGER.severe("Shutdown error: " + e.getMessage());
            }
        }
    }

    private static void initializeData() {
        String initData = System.getenv("INIT_SAMPLE_DATA");
        if (!"true".equalsIgnoreCase(initData)) {
            LOGGER.info("Skipping sample data initialization");
            return;
        }

        LOGGER.info("Initializing sample data...");
        try {
            // Authors
            Author author1 = new Author("author1", "F. Scott", "Fitzgerald");
            Author author2 = new Author("author2", "George", "Orwell");
            InMemoryStorage.getAuthors().put(author1.getId(), author1);
            InMemoryStorage.getAuthors().put(author2.getId(), author2);

            // Books
            Book book1 = new Book("book1", "The Great Gatsby", "author1", "978-0743273565", 1925, 9.99, 10);
            Book book2 = new Book("book2", "1984", "author2", "978-0451524935", 1949, 12.99, 5);
            InMemoryStorage.getBooks().put(book1.getId(), book1);
            InMemoryStorage.getBooks().put(book2.getId(), book2);

            // Customer
            Customer customer1 = new Customer("customer1", "Test", "User", "test@bookstore.com", "test123");
            InMemoryStorage.getCustomers().put(customer1.getId(), customer1);

            // Cart
            Map<String, Integer> cartItems = new HashMap<>();
            cartItems.put("book1", 1);
            InMemoryStorage.getCarts().put("customer1", cartItems);

            LOGGER.info("Sample data initialized");
        } catch (Exception e) {
            LOGGER.severe("Sample data failed: " + e.getMessage());
        }
    }
}