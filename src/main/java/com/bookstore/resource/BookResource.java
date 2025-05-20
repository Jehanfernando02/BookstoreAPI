package com.bookstore.resource;

import com.bookstore.model.Book;
import com.bookstore.storage.InMemoryStorage;
import com.bookstore.exception.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
    private static final Logger LOGGER = Logger.getLogger(BookResource.class.getName());

    @POST
    @Operation(summary = "Create a new book", description = "Adds a new book to the bookstore")
    @ApiResponse(responseCode = "201", description = "Book created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public Response createBook(Book book) {
        LOGGER.info("Creating book: " + (book.getTitle() != null ? book.getTitle() : "null"));
        try {
            validateBook(book);
            checkUniqueIsbn(book.getIsbn(), null);
            if (book.getId() == null || book.getId().isEmpty()) {
                book.setId(InMemoryStorage.generateId("book"));
            }
            InMemoryStorage.getBooks().put(book.getId(), book);
            LOGGER.info("Book created successfully: " + book.getId());
            return Response.status(Response.Status.CREATED).entity(book).build();
        } catch (Exception e) {
            LOGGER.severe("Error creating book: " + e.getMessage());
            throw e;
        }
    }

    @GET
    @Operation(summary = "Get all books", description = "Retrieves a list of all books")
    @ApiResponse(responseCode = "200", description = "List of books")
    public List<Book> getAllBooks() {
        LOGGER.info("Fetching all books");
        return new ArrayList<>(InMemoryStorage.getBooks().values());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a book by ID", description = "Retrieves a book by its ID")
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public Book getBook(@PathParam("id") String id) {
        LOGGER.info("Fetching book with ID: " + id);
        Book book = InMemoryStorage.getBooks().get(id);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }
        return book;
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a book", description = "Updates an existing book by ID")
    @ApiResponse(responseCode = "200", description = "Book updated")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public Book updateBook(@PathParam("id") String id, Book book) {
        LOGGER.info("Updating book with ID: " + id);
        if (!InMemoryStorage.getBooks().containsKey(id)) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }
        validateBook(book);
        checkUniqueIsbn(book.getIsbn(), id);
        book.setId(id);
        InMemoryStorage.getBooks().put(id, book);
        return book;
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a book", description = "Deletes a book by ID")
    @ApiResponse(responseCode = "204", description = "Book deleted")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public Response deleteBook(@PathParam("id") String id) {
        LOGGER.info("Deleting book with ID: " + id);
        if (!InMemoryStorage.getBooks().containsKey(id)) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }
        InMemoryStorage.getBooks().remove(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private void validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            throw new InvalidInputException("Book title is required.");
        }
        if (book.getAuthorId() == null || !InMemoryStorage.getAuthors().containsKey(book.getAuthorId())) {
            throw new AuthorNotFoundException("Author with ID " + book.getAuthorId() + " does not exist.");
        }
        if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
            throw new InvalidInputException("ISBN is required.");
        }
        if (!book.getIsbn().matches("^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$)[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$")) {
            throw new InvalidInputException("Invalid ISBN format.");
        }
        if (book.getPublicationYear() > 2025) {
            throw new InvalidInputException("Publication year must not exceed the system's current year (2025).");
        }
        if (book.getPrice() <= 0) {
            throw new InvalidInputException("Price must be positive.");
        }
        if (book.getStock() < 0) {
            throw new InvalidInputException("Stock cannot be negative.");
        }
    }

    private void checkUniqueIsbn(String isbn, String excludeId) {
        for (Book existing : InMemoryStorage.getBooks().values()) {
            if (existing.getIsbn().equals(isbn) && (excludeId == null || !existing.getId().equals(excludeId))) {
                throw new InvalidInputException("ISBN " + isbn + " is already in use.");
            }
        }
    }
}