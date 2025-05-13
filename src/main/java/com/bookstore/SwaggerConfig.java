package com.bookstore;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Bookstore API",
        version = "1.0",
        description = "REST API for managing a bookstore"
    ),
    servers = @Server(url = "/api")
)
public class SwaggerConfig {
}