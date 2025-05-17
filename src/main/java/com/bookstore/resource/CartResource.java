package com.bookstore.resource;

import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.storage.InMemoryStorage;
import com.bookstore.exception.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.logging.Logger;

@Path("/customers/{customerId}/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {
    private static final Logger LOGGER = Logger.getLogger(CartResource.class.getName());

    @POST
    public Response createCart(@PathParam("customerId") String customerId) {
        LOGGER.info("Creating cart for customer: " + customerId);
        validateCustomer(customerId);
        if (InMemoryStorage.getCarts().containsKey(customerId)) {
            throw new InvalidInputException("Cart already exists for customer ID " + customerId);
        }
        Cart cart = new Cart(customerId);
        cart.setId(InMemoryStorage.generateId("cart"));
        InMemoryStorage.getCarts().put(customerId, cart);
        return Response.status(Response.Status.CREATED).entity(cart).build();
    }

    @POST
    @Path("/items")
    public Response addItemToCart(@PathParam("customerId") String customerId, Map<String, Object> item) {
        LOGGER.info("Adding item to cart for customer: " + customerId);
        validateCustomer(customerId);

        Object bookIdObj = item.get("bookId");
        Object quantityObj = item.get("quantity");
        if (bookIdObj == null || !(bookIdObj instanceof String) || quantityObj == null || !(quantityObj instanceof Integer)) {
            throw new InvalidInputException("Book ID (string) and quantity (integer) are required.");
        }

        String bookId = (String) bookIdObj;
        Integer quantity = (Integer) quantityObj;
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be positive.");
        }

        Book book = InMemoryStorage.getBooks().get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " does not exist.");
        }
        if (book.getStock() < quantity) {
            throw new OutOfStockException("Insufficient stock for book ID " + bookId);
        }

        Cart cart = InMemoryStorage.getCarts().get(customerId);
        if (cart == null) {
            cart = new Cart(customerId);
            cart.setId(InMemoryStorage.generateId("cart"));
            InMemoryStorage.getCarts().put(customerId, cart);
        }
        cart.getItems().put(bookId, cart.getItems().getOrDefault(bookId, 0) + quantity);
        book.setStock(book.getStock() - quantity);
        return Response.status(Response.Status.CREATED).entity(cart).build();
    }

    @GET
    public Cart getCart(@PathParam("customerId") String customerId) {
        LOGGER.info("Fetching cart for customer: " + customerId);
        validateCustomer(customerId);
        Cart cart = InMemoryStorage.getCarts().get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " does not exist.");
        }
        return cart;
    }

    @PUT
    @Path("/items/{bookId}")
    public Response updateItemInCart(@PathParam("customerId") String customerId, @PathParam("bookId") String bookId, Map<String, Object> item) {
        LOGGER.info("Updating item in cart for customer: " + customerId);
        validateCustomer(customerId);

        Object quantityObj = item.get("quantity");
        if (quantityObj == null || !(quantityObj instanceof Integer)) {
            throw new InvalidInputException("Quantity (integer) is required.");
        }

        Integer quantity = (Integer) quantityObj;
        if (quantity < 0) {
            throw new InvalidInputException("Quantity cannot be negative.");
        }

        Cart cart = InMemoryStorage.getCarts().get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " does not exist.");
        }

        Book book = InMemoryStorage.getBooks().get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " does not exist.");
        }

        int currentQuantity = cart.getItems().getOrDefault(bookId, 0);
        int stockAdjustment = quantity - currentQuantity;
        if (book.getStock() < stockAdjustment) {
            throw new OutOfStockException("Insufficient stock for book ID " + bookId);
        }

        if (quantity == 0) {
            cart.getItems().remove(bookId);
        } else {
            cart.getItems().put(bookId, quantity);
        }
        book.setStock(book.getStock() - stockAdjustment);
        return Response.ok(cart).build();
    }

    @DELETE
    @Path("/items/{bookId}")
    public Response removeItemFromCart(@PathParam("customerId") String customerId, @PathParam("bookId") String bookId) {
        LOGGER.info("Removing item from cart for customer: " + customerId);
        validateCustomer(customerId);
        Cart cart = InMemoryStorage.getCarts().get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " does not exist.");
        }
        if (!cart.getItems().containsKey(bookId)) {
            throw new BookNotFoundException("Book with ID " + bookId + " is not in the cart.");
        }
        int quantity = cart.getItems().get(bookId);
        cart.getItems().remove(bookId);
        Book book = InMemoryStorage.getBooks().get(bookId);
        book.setStock(book.getStock() + quantity);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private void validateCustomer(String customerId) {
        if (!InMemoryStorage.getCustomers().containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
    }
}