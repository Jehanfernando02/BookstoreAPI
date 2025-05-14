package com.bookstore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    private static final Logger LOGGER = Logger.getLogger(CorsFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        LOGGER.info("Applying CORS filter for request: " + requestContext.getMethod() + " " + requestContext.getUriInfo().getPath());
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Max-Age", "1209600");

        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            LOGGER.info("Handling OPTIONS preflight request");
            responseContext.setStatus(200);
        }
        LOGGER.info("CORS headers added: " + headers.toString());
    }
}