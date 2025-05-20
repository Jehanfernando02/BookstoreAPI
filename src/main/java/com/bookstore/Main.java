package com.bookstore;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.Cart;
import com.bookstore.storage.InMemoryStorage;

import java.util.HashMap;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        seedInitialData();

        String port = System.getenv("PORT") != null ? System.getenv("PORT") : "8080";
        Server server = new Server(Integer.parseInt(port));

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHolder servletHolder = new ServletHolder(new ServletContainer(new AppConfig()));
        servletHolder.setInitOrder(0);
        context.addServlet(servletHolder, "/api/*");

        server.setHandler(context);

        try {
            server.start();
            logger.info("📚 Bookstore API running at http://0.0.0.0:{}", port);
            server.join();
        } catch (Exception e) {
            logger.error("❌ Failed to start Jetty server", e);
            throw new RuntimeException("Server startup failed", e);
        } finally {
            try {
                if (server.isStarted() || server.isStarting()) {
                    logger.info("🛑 Stopping Jetty server");
                    server.stop();
                }
                logger.info("🧹 Destroying Jetty server");
                server.destroy();
            } catch (Exception e) {
                logger.error("⚠️ Error during server shutdown", e);
            }
        }
    }

    private static void seedInitialData() {
        if (InMemoryStorage.getAuthors().isEmpty()) {
            Author author1 = new Author("author1", "Jane", "Austen");
            Author author2 = new Author("author2", "George", "Orwell");
            InMemoryStorage.getAuthors().put(author1.getId(), author1);
            InMemoryStorage.getAuthors().put(author2.getId(), author2);
            logger.info("Seeded authors: Jane Austen, George Orwell");
        }

        if (InMemoryStorage.getBooks().isEmpty()) {
            Book book1 = new Book("book1", "Pride and Prejudice", "author1", "978-0141439518", 1813, 14.99, 100);
            Book book2 = new Book("book2", "1984", "author2", "978-0451524935", 1949, 12.99, 50);
            InMemoryStorage.getBooks().put(book1.getId(), book1);
            InMemoryStorage.getBooks().put(book2.getId(), book2);
            logger.info("Seeded books: Pride and Prejudice, 1984");
        }

        if (InMemoryStorage.getCustomers().isEmpty()) {
            Customer customer1 = new Customer("customer1", "John", "Doe", "john.doe@example.com", "password123");
            Customer customer2 = new Customer("customer2", "Jane", "Smith", "jane.smith@example.com", "password456");
            InMemoryStorage.getCustomers().put(customer1.getId(), customer1);
            InMemoryStorage.getCustomers().put(customer2.getId(), customer2);
            logger.info("Seeded customers: John Doe, Jane Smith");
        }

        if (InMemoryStorage.getCarts().isEmpty()) {
            Cart cart1 = new Cart("customer1");
            cart1.setId("cart1");
            cart1.getItems().put("book1", 2);
            Cart cart2 = new Cart("customer2");
            cart2.setId("cart2");
            cart2.getItems().put("book2", 1);
            InMemoryStorage.getCarts().put("customer1", cart1);
            InMemoryStorage.getCarts().put("customer2", cart2);
            logger.info("Seeded carts for customers: customer1, customer2");
        }
    }
}