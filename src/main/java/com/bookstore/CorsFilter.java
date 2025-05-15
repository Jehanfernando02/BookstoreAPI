package com.bookstore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    private static final Logger LOGGER = Logger.getLogger(CorsFilter.class.getName());
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "https://libro-nest.vercel.app",
        "http://localhost:3000"
    );

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        try {
            MultivaluedMap<String, Object> headers = responseContext.getHeaders();
            String origin = requestContext.getHeaderString("Origin");
            LOGGER.info("Processing CORS for Origin: " + (origin != null ? origin : "null"));

            // Set CORS headers
            if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
                headers.add("Access-Control-Allow-Origin", origin);
                LOGGER.info("Allowed origin: " + origin);
            } else {
                headers.add("Access-Control-Allow-Origin", "https://libro-nest.vercel.app");
                LOGGER.info("Default origin set: https://libro-nest.vercel.app");
            }

            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            headers.add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
            headers.add("Access-Control-Allow-Credentials", "true");
            headers.add("Access-Control-Max-Age", "86400");

            // Handle OPTIONS preflight requests
            if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
                responseContext.setStatus(200);
                LOGGER.info("Handled OPTIONS request");
            }
        } catch (Exception e) {
            LOGGER.severe("CORS filter error: " + e.getMessage());
            throw new IOException("CORS filter failed", e);
        }
    }
}