package com.bookstore;

import org.glassfish.jersey.server.ResourceConfig;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class BookstoreApplication extends ResourceConfig {
    public BookstoreApplication() {
        packages("com.bookstore.resource");
        register(OpenApiResource.class);
        register(HealthResource.class);
        register(CorsFilter.class); // Register CORS filter
    }

    @Path("/health")
    public static class HealthResource {
        @GET
        public Response healthCheck() {
            return Response.ok("Healthy").build();
        }
    }
}