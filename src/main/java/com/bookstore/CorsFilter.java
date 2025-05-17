package com.bookstore;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
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
@Priority(Priorities.HEADER_DECORATOR)
public class CorsFilter implements ContainerResponseFilter {
    private static final Logger LOGGER = Logger.getLogger(CorsFilter.class.getName());
    
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "https://libro-nest.vercel.app",
        "http://localhost:3000",
        "http://localhost:5173" // Added for Vite dev server
    );

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        String origin = requestContext.getHeaderString("Origin");
        
        LOGGER.info("Processing CORS for Origin: " + (origin != null ? origin : "null"));
        
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            headers.putSingle("Access-Control-Allow-Origin", origin);
            LOGGER.info("Allowed origin: " + origin);
        } else {
            headers.putSingle("Access-Control-Allow-Origin", "https://libro-nest.vercel.app");
            LOGGER.info("Fallback origin set: https://libro-nest.vercel.app");
        }
        
        headers.putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        headers.putSingle("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization, X-Requested-With");
        headers.putSingle("Access-Control-Allow-Credentials", "true");
        headers.putSingle("Access-Control-Max-Age", "86400");
        
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            responseContext.setStatus(200);
            LOGGER.info("OPTIONS preflight request processed");
        }
    }
}