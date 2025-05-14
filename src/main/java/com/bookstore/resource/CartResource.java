package com.bookstore.resource;

import com.bookstore.exception.InvalidInputException;
import com.bookstore.storage.InMemoryStorage;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/customers/{customerId}/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    @GET
    public Response getCart(@PathParam("customerId") String customerId) {
        if (!InMemoryStorage.getCustomers().containsKey(customerId)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
        }
        Map<String, Integer> cart = InMemoryStorage.getCarts().getOrDefault(customerId, new HashMap<>());
        return Response.ok(cart).build();
    }

    @POST
    @Path("/items")
    public Response addToCart(@PathParam("customerId") String customerId, Map<String, Integer> item) {
        if (!InMemoryStorage.getCustomers().containsKey(customerId)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
        }
        if (item == null || item.size() != 1) {
            throw new InvalidInputException("Exactly one book ID and quantity required");
        }
        Map.Entry<String, Integer> entry = item.entrySet().iterator().next();
        String bookId = entry.getKey();
        Integer quantity = entry.getValue();

        if (!InMemoryStorage.getBooks().containsKey(bookId)) {
            throw new InvalidInputException("Book does not exist");
        }
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be positive");
        }

        Map<String, Integer> cart = InMemoryStorage.getCarts().computeIfAbsent(customerId, k -> new HashMap<>());
        cart.compute(bookId, (k, v) -> v == null ? quantity : v + quantity);
        InMemoryStorage.getCarts().put(customerId, cart);
        return Response.status(Response.Status.CREATED).entity(cart).build();
    }

    @PUT
    @Path("/items/{bookId}")
    public Response updateCartItem(@PathParam("customerId") String customerId, @PathParam("bookId") String bookId, Map<String, Integer> update) {
        if (!InMemoryStorage.getCustomers().containsKey(customerId)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
        }
        if (!InMemoryStorage.getBooks().containsKey(bookId)) {
            throw new InvalidInputException("Book does not exist");
        }
        if (update == null || !update.containsKey("quantity")) {
            throw new InvalidInputException("Quantity required");
        }
        int quantity = update.get("quantity");
        if (quantity < 0) {
            throw new InvalidInputException("Quantity cannot be negative");
        }

        Map<String, Integer> cart = InMemoryStorage.getCarts().computeIfAbsent(customerId, k -> new HashMap<>());
        if (quantity == 0) {
            cart.remove(bookId);
        } else {
            cart.put(bookId, quantity);
        }
        InMemoryStorage.getCarts().put(customerId, cart);
        return Response.ok(cart).build();
    }

    @DELETE
    @Path("/items/{bookId}")
    public Response removeCartItem(@PathParam("customerId") String customerId, @PathParam("bookId") String bookId) {
        if (!InMemoryStorage.getCustomers().containsKey(customerId)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
        }
        Map<String, Integer> cart = InMemoryStorage.getCarts().get(customerId);
        if (cart == null || !cart.containsKey(bookId)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Item not in cart").build();
        }
        cart.remove(bookId);
        if (cart.isEmpty()) {
            InMemoryStorage.getCarts().remove(customerId);
        } else {
            InMemoryStorage.getCarts().put(customerId, cart);
        }
        return Response.noContent().build();
    }
}