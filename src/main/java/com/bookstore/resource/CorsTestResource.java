package com.bookstore.resource;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/cors-test")
public class CorsTestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response testGet() {
        return Response.ok("{\"message\": \"CORS test successful\"}").build();
    }
    
    @OPTIONS
    public Response testOptions() {
        // The CORS filter should handle this, but we can add it here as well for redundancy
        return Response.ok()
            .build();
    }
}