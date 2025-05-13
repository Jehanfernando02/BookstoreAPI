package com.bookstore;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.storage.InMemoryStorage;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {
    public static void main(String[] args) throws Exception {
        // Configure Jetty server
        Server server = new Server(8080);

        // Set up servlet context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Configure Jersey servlet
        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer());
        jerseyServlet.setInitParameter("javax.ws.rs.Application", BookstoreApplication.class.getName());
        context.addServlet(jerseyServlet, "/api/*");

        server.setHandler(context);

        // Start the server
        try {
            server.start();
            // Pre-populate InMemoryStorage with sample data
            initializeData();
            server.join();
        } finally {
            server.destroy();
        }
    }

    private static void initializeData() {
        // Pre-populate sample data to avoid empty state
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
    }
}