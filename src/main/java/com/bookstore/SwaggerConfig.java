package com.bookstore;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Bookstore API",
        version = "1.0",
        description = "API for managing bookstore data"
    ),
    servers = @Server(url = "https://bookstoreapi-c176.onrender.com/api")
)
public class SwaggerConfig {
}