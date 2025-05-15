package com.bookstore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "https://libro-nest.vercel.app",
        "http://localhost:3000"
    );

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        String origin = requestContext.getHeaderString("Origin");

        // Set CORS headers for allowed origins
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            headers.add("Access-Control-Allow-Origin", origin);
        } else {
            headers.add("Access-Control-Allow-Origin", "https://libro-nest.vercel.app");
        }

        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        headers.add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Max-Age", "86400");

        // Handle preflight OPTIONS requests
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            responseContext.setStatus(200); // OK for preflight
        }
    }
}