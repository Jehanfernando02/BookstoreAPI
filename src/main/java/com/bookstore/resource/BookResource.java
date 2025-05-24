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
    @ApiResponse(responseCode = "500", description = "Server error")
    public Response createBook(Book book) {
        LOGGER.info("Creating book: " + (book != null && book.getTitle() != null ? book.getTitle() : "null"));
        LOGGER.fine("Received book data: " + book);
        try {
            if (book == null) {
                LOGGER.warning("Received null book object");
                throw new InvalidInputException("Book data is required");
            }
            validateBook(book);
            checkUniqueIsbn(book.getIsbn(), null);
            if (book.getId() == null || book.getId().isEmpty()) {
                book.setId(InMemoryStorage.generateId("book"));
            }
            InMemoryStorage.getBooks().put(book.getId(), book);
            LOGGER.info("Book created successfully: " + book.getId());
            return Response.status(Response.Status.CREATED).entity(book).build();
        } catch (InvalidInputException | AuthorNotFoundException e) {
            LOGGER.warning("Validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error creating book: " + e.getMessage());
            throw new WebApplicationException("Failed to create book: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Operation(summary = "Get all books", description = "Retrieves a list of all books")
    @ApiResponse(responseCode = "200", description = "List of books")
    public List<Book> getAllBooks() {
        LOGGER.info("Fetching all books");
        try {
            return new ArrayList<>(InMemoryStorage.getBooks().values());
        } catch (Exception e) {
            LOGGER.severe("Error fetching books: " + e.getMessage());
            throw new WebApplicationException("Failed to fetch books", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a book by ID", description = "Retrieves a book by its ID")
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public Book getBook(@PathParam("id") String id) {
        LOGGER.info("Fetching book with ID: " + id);
        try {
            Book book = InMemoryStorage.getBooks().get(id);
            if (book == null) {
                throw new BookNotFoundException("Book with ID " + id + " does not exist.");
            }
            return book;
        } catch (BookNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Error fetching book: " + e.getMessage());
            throw new WebApplicationException("Failed to fetch book", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a book", description = "Updates an existing book by ID")
    @ApiResponse(responseCode = "200", description = "Book updated")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public Book updateBook(@PathParam("id") String id, Book book) {
        LOGGER.info("Updating book with ID: " + id);
        try {
            if (!InMemoryStorage.getBooks().containsKey(id)) {
                throw new BookNotFoundException("Book with ID " + id + " does not exist.");
            }
            validateBook(book);
            checkUniqueIsbn(book.getIsbn(), id);
            book.setId(id);
            InMemoryStorage.getBooks().put(id, book);
            return book;
        } catch (InvalidInputException | BookNotFoundException | AuthorNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Error updating book: " + e.getMessage());
            throw new WebApplicationException("Failed to update book", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a book", description = "Deletes a book by ID")
    @ApiResponse(responseCode = "204", description = "Book deleted")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public Response deleteBook(@PathParam("id") String id) {
        LOGGER.info("Deleting book with ID: " + id);
        try {
            if (!InMemoryStorage.getBooks().containsKey(id)) {
                throw new BookNotFoundException("Book with ID " + id + " does not exist.");
            }
            InMemoryStorage.getBooks().remove(id);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (BookNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Error deleting book: " + e.getMessage());
            throw new WebApplicationException("Failed to delete book", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateBook(Book book) {
        LOGGER.fine("Validating book: " + book);
        try {
            if (book == null) {
                LOGGER.warning("Validation failed: Book is null");
                throw new InvalidInputException("Book data is required");
            }
            if (book.getTitle() == null || book.getTitle().isEmpty()) {
                LOGGER.warning("Validation failed: Book title is required");
                throw new InvalidInputException("Book title is required.");
            }
            if (book.getAuthorId() == null || book.getAuthorId().isEmpty()) {
                LOGGER.warning("Validation failed: Author ID is required");
                throw new InvalidInputException("Author ID is required.");
            }
            if (!InMemoryStorage.getAuthors().containsKey(book.getAuthorId())) {
                LOGGER.warning("Validation failed: Author with ID " + book.getAuthorId() + " does not exist");
                throw new AuthorNotFoundException("Author with ID " + book.getAuthorId() + " does not exist.");
            }
            if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
                LOGGER.warning("Validation failed: ISBN is required");
                throw new InvalidInputException("ISBN is required.");
            }
            if (!book.getIsbn().matches("^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$)[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$")) {
                LOGGER.warning("Validation failed: Invalid ISBN format for " + book.getIsbn());
                throw new InvalidInputException("Invalid ISBN format.");
            }
            if (book.getPublicationYear() <= 0 || book.getPublicationYear() > 2025) {
                LOGGER.warning("Validation failed: Publication year " + book.getPublicationYear() + " is invalid or exceeds 2025");
                throw new InvalidInputException("Publication year must be a valid number and not exceed 2025.");
            }
            if (book.getPrice() <= 0) {
                LOGGER.warning("Validation failed: Price " + book.getPrice() + " must be positive");
                throw new InvalidInputException("Price must be positive.");
            }
            if (book.getStock() < 0) {
                LOGGER.warning("Validation failed: Stock " + book.getStock() + " cannot be negative");
                throw new InvalidInputException("Stock cannot be negative.");
            }
        } catch (Exception e) {
            LOGGER.severe("Unexpected error validating book: " + e.getMessage());
            throw new InvalidInputException("Validation failed: " + e.getMessage());
        }
    }

    private void checkUniqueIsbn(String isbn, String excludeId) {
        try {
            if (isbn == null) {
                LOGGER.warning("ISBN is null");
                throw new InvalidInputException("ISBN cannot be null");
            }
            for (Book existing : InMemoryStorage.getBooks().values()) {
                if (existing.getIsbn().equals(isbn) && (excludeId == null || !existing.getId().equals(excludeId))) {
                    LOGGER.warning("Validation failed: ISBN " + isbn + " is already in use");
                    throw new InvalidInputException("ISBN " + isbn + " is already in use.");
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error checking ISBN uniqueness: " + e.getMessage());
            throw new InvalidInputException("Failed to check ISBN uniqueness: " + e.getMessage());
        }
    }
}