package com.bookstore;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {
    public static void main(String[] args) throws Exception {
        String port = System.getenv("PORT") != null ? System.getenv("PORT") : "8080";
        Server server = new Server(Integer.parseInt(port));

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/api");
        server.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
        jerseyServlet.setInitParameter("javax.ws.rs.Application", BookstoreApplication.class.getName());

        try {
            server.start();
            System.out.println("Bookstore API started on port " + port);
            server.join();
        } finally {
            server.destroy();
        }
    }
}