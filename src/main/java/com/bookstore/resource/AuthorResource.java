package com.bookstore.resource;

import com.bookstore.exception.InvalidInputException;
import com.bookstore.model.Author;
import com.bookstore.storage.InMemoryStorage;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

    @GET
    public List<Author> getAuthors() {
        return InMemoryStorage.getAuthors().values().stream().collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response getAuthor(@PathParam("id") String id) {
        Author author = InMemoryStorage.getAuthors().get(id);
        if (author == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(author).build();
    }

    @POST
    public Response createAuthor(Author author) {
        validateAuthor(author);
        String id = InMemoryStorage.generateId("author");
        author.setId(id);
        InMemoryStorage.addAuthor(author);
        return Response.status(Response.Status.CREATED).entity(author).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateAuthor(@PathParam("id") String id, Author author) {
        if (!InMemoryStorage.getAuthors().containsKey(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        validateAuthor(author);
        author.setId(id);
        InMemoryStorage.addAuthor(author);
        return Response.ok(author).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") String id) {
        if (InMemoryStorage.getAuthors().remove(id) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private void validateAuthor(Author author) {
        if (author.getFirstName() == null || author.getFirstName().trim().isEmpty()) {
            throw new InvalidInputException("First name is required");
        }
        if (author.getLastName() == null || author.getLastName().trim().isEmpty()) {
            throw new InvalidInputException("Last name is required");
        }
    }
}