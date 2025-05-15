package com.bookstore.resource;

import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.Order;
import com.bookstore.storage.InMemoryStorage;
import com.bookstore.exception.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/customers/{customerId}/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    private static final Logger LOGGER = Logger.getLogger(OrderResource.class.getName());

    @POST
    public Response createOrder(@PathParam("customerId") String customerId) {
        LOGGER.info("Creating order for customer: " + customerId);
        validateCustomer(customerId);

        // Fetch the customer's cart
        Cart cart = InMemoryStorage.getCarts().get(customerId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new InvalidInputException("Cart is empty or does not exist for customer ID " + customerId);
        }

        // Calculate total price and validate stock
        double totalPrice = 0.0;
        for (Map.Entry<String, Integer> entry : cart.getItems().entrySet()) {
            String bookId = entry.getKey();
            int quantity = entry.getValue();
            Book book = InMemoryStorage.getBooks().get(bookId);
            if (book == null) {
                throw new BookNotFoundException("Book with ID " + bookId + " does not exist.");
            }
            if (book.getStock() < quantity) {
                throw new OutOfStockException("Insufficient stock for book ID " + bookId);
            }
            totalPrice += book.getPrice() * quantity;
        }

        // Create order
        Order order = new Order();
        order.setId(InMemoryStorage.generateId("order"));
        order.setCustomerId(customerId);
        order.setItems(new HashMap<>(cart.getItems()));
        order.setTotalPrice(totalPrice);

        // Clear cart and update stock
        for (Map.Entry<String, Integer> entry : cart.getItems().entrySet()) {
            String bookId = entry.getKey();
            int quantity = entry.getValue();
            Book book = InMemoryStorage.getBooks().get(bookId);
            book.setStock(book.getStock() - quantity);
        }
        InMemoryStorage.getCarts().remove(customerId);

        // Save order
        InMemoryStorage.getOrders().put(order.getId(), order);
        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    @GET
    public List<Order> getOrders(@PathParam("customerId") String customerId) {
        LOGGER.info("Fetching orders for customer: " + customerId);
        validateCustomer(customerId);
        return InMemoryStorage.getOrders().values().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    private void validateCustomer(String customerId) {
        if (!InMemoryStorage.getCustomers().containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
    }
}
