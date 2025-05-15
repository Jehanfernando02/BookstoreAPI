package com.bookstore;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHolder servletHolder = new ServletHolder(new ServletContainer());
        servletHolder.setInitParameter("javax.ws.rs.Application", BookstoreApplication.class.getName());
        context.addServlet(servletHolder, "/api/*");

        server.setHandler(context);

        try {
            server.start();
            logger.info("Bookstore API running at http://localhost:8080/api");
            server.join(); // Blocks until server is stopped
        } catch (Exception e) {
            logger.error("Failed to start Jetty server", e);
            throw new RuntimeException("Server startup failed", e);
        } finally {
            try {
                if (server.isStarted() || server.isStarting()) {
                    logger.info("Stopping Jetty server");
                    server.stop();
                }
                logger.info("Destroying Jetty server");
                server.destroy();
            } catch (Exception e) {
                logger.error("Error during server shutdown", e);
            }
        }
    }
}