package com.bookstore;

import org.glassfish.jersey.server.ResourceConfig;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class AppConfig extends ResourceConfig {
    
    public AppConfig() {
        // Register resource packages
        packages("com.bookstore.resource");
        
        // Register Swagger/OpenAPI
        register(OpenApiResource.class);
        
        // Register CORS filter - this is crucial
        register(CorsFilter.class);
        
        // Register other application components
        register(BookstoreApplication.HealthResource.class);
        
        // Enable logging for debugging
        property("jersey.config.server.tracing", "ALL");
        property("jersey.config.server.tracing.threshold", "VERBOSE");
    }
}