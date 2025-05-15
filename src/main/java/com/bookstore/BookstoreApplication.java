package com.bookstore;

import org.glassfish.jersey.server.ResourceConfig;
import com.bookstore.exception.GenericExceptionMapper;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class BookstoreApplication extends ResourceConfig {
    public BookstoreApplication() {
        packages("com.bookstore.resource");
        register(CorsFilter.class);
        register(GenericExceptionMapper.class);
        register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }
}