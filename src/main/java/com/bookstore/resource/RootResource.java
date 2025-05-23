package com.bookstore.resource;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RootResource {

    @GET
    public Response getRoot() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello , Welcome to the Bookstore API.");
        return Response.ok(response).build();
    }

    @OPTIONS
    public Response handlePreflight() {
        // Respond with 200 OK and let CorsFilter add headers
        return Response.ok().build();
    }
}
