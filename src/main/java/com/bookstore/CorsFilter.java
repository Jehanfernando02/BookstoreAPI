package com.bookstore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    private static final Logger LOGGER = Logger.getLogger(CorsFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept");
        responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");

        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            LOGGER.fine("Handling OPTIONS preflight for: " + requestContext.getUriInfo().getPath());
            responseContext.setStatus(200);
        }
    }
}