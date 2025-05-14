package com.bookstore.resource;

import com.bookstore.exception.InvalidInputException;
import com.bookstore.model.Book;
import com.bookstore.model.Order;
import com.bookstore.storage.InMemoryStorage;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @POST
    public Response createOrder(Order order) {
        if (order.getCustomerId() == null || order.getCustomerId().trim().isEmpty()) {
            throw new InvalidInputException("Customer ID is required");
        }
        if (!InMemoryStorage.getCustomers().containsKey(order.getCustomerId())) {
            return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
        }
        Map<String, Integer> cart = InMemoryStorage.getCarts().get(order.getCustomerId());
        if (cart == null || cart.isEmpty()) {
            throw new InvalidInputException("Cart is empty");
        }

        for (Map.Entry<String, Integer> item : cart.entrySet()) {
            String bookId = item.getKey();
            int quantity = item.getValue();
            if (!InMemoryStorage.getBooks().containsKey(bookId)) {
                throw new InvalidInputException("Book " + bookId + " does not exist");
            }
            if (InMemoryStorage.getBooks().get(bookId).getStock() < quantity) {
                throw new InvalidInputException("Insufficient stock for book " + bookId);
            }
        }

        String orderId = InMemoryStorage.generateId("order");
        order.setId(orderId);
        InMemoryStorage.addOrder(order);

        for (Map.Entry<String, Integer> item : cart.entrySet()) {
            String bookId = item.getKey();
            int quantity = item.getValue();
            Book book = InMemoryStorage.getBooks().get(bookId);
            book.setStock(book.getStock() - quantity);
            InMemoryStorage.addBook(book);
        }

        InMemoryStorage.getCarts().remove(order.getCustomerId());
        return Response.status(Response.Status.CREATED).entity(order).build();
    }
}