package com.bookstore;

import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {
    public AppConfig() {
        // Scan the package for @Provider, @Path classes like CorsFilter, controllers
        packages("com.bookstore");

        // OPTIONAL: If using Swagger
        // register(io.swagger.jaxrs.listing.ApiListingResource.class);
        // register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
    }
}
