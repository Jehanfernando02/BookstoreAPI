package com.bookstore;

import com.bookstore.resource.*;
import com.bookstore.exception.GenericExceptionMapper;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class BookstoreApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        // Register all JAX-RS resource and provider classes for the application
        Set<Class<?>> classes = new HashSet<>();
        classes.add(RootResource.class);
        classes.add(BookResource.class);
        classes.add(AuthorResource.class);
        classes.add(CustomerResource.class);
        classes.add(CartResource.class);
        classes.add(OrderResource.class);
        classes.add(GenericExceptionMapper.class);
        return classes;
    }
}