package com.example.project.controller;

import com.example.project.enums.BookStatus;
import com.example.project.model.Book;
import com.example.project.model.Library;
import com.example.project.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/library")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Library Controller", description = "Operations related to personal library management")
public class LibraryController {
    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all libraries", description = "Retrieve all personal libraries from the database")
    public List<Library> getAllLibraries(){
        return libraryService.getAllLibraries();
    }

    @GetMapping("/{libraryID}")
    @Operation(summary = "Get a library by ID", description = "Retrieve a personal library from the database by it's ID")
    public Library getLibraryWithBooks(@PathVariable int libraryID) {
        return libraryService.getLibraryById(libraryID);
    }


    @GetMapping("/search/by-name")
    @Operation(summary = "Search book by name", description = "Search a book by it's name")
    public ResponseEntity<List<Book>> searchBooksByName(@RequestParam("name") String name) {
        List<Book> books = libraryService.searchBooksByName(name);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/search/by-author")
    @Operation(summary = "Search book by author name", description = "Search a book by it's author's name")
    public ResponseEntity<List<Book>> searchBooksByAuthor(@RequestParam("author") String author) {
        List<Book> books = libraryService.searchBooksByAuthor(author);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{libraryId}/books/sorted")
    @Operation(summary = "Sort books", description = "Sort books by a desired parameter")
    public List<Book> getBooksInLibrarySorted(@PathVariable int libraryId, @RequestParam String sortBy) {
        return libraryService.getBooksInLibrarySorted(libraryId, sortBy);
    }

    @PreAuthorize("hasRole('AUTHOR')")
    @GetMapping("/{bookID}/count")
    @Operation(summary = "See book popularity", description = "See how many times a book have been added to a personal library")
    public ResponseEntity<Integer> getBookOccurrences(@PathVariable int bookID) {
        int count = libraryService.getBookOccurrenceCount(bookID);
        return ResponseEntity.ok(count);
    }

//    @PutMapping("/books/{bookId}/rating")
//    public void updateBookRating(@PathVariable int bookId, @RequestParam double rating) {
//        if (!libraryService.updateBookRating(bookId, rating)) {
//            throw new RuntimeException("Failed to update book rating");
//        }
//    }
//@PostMapping("/books/{bookId}/rating")
//public void addBookRating(@PathVariable int bookId, @RequestParam double rating) {
//    if (!libraryService.addBookRating(bookId, rating)) {
//        throw new RuntimeException("Failed to add book rating");
//    }
//}
@PostMapping("/{bookId}/rating")
@Operation(summary = "Rate a book", description = "Give a rating to a book")
public ResponseEntity<String> updateBookRating(
        @PathVariable int bookId,
        @RequestParam double rating) {

   // try {
        /*boolean success =*/ libraryService.insertBookRatings(bookId, rating);
//        if (success) {
//            return ResponseEntity.ok("Book rating updated successfully.");
//        } else {
//            return ResponseEntity.status(400).body("Failed to update book rating.");
//        }
//    } catch (IllegalArgumentException e) {
//        return ResponseEntity.status(400).body(e.getMessage());
//    } catch (Exception e) {
//        return ResponseEntity.status(500).body("An error occurred while updating the book rating.");
//    }
    return new ResponseEntity<>("Book with ID: "+bookId+" rated successfully!", HttpStatus.CREATED);
}

    @PostMapping
    @Operation(summary = "Create a personal library", description = "Create a personal library where you can add the book you like")
    public ResponseEntity<String> createLibrary(@RequestBody Library library){
        libraryService.createLibrary(library);
        return new ResponseEntity<>("Library with ID: "+library.getLibraryID()+" created successfully!", HttpStatus.CREATED);
    }

    @PostMapping("/{libraryID}/books")
    @Operation(summary = "Add books", description = "Add books to your personal library")
    public void addBooksToLibrary(@PathVariable int libraryID, @RequestBody List<Integer> bookIDs) {
        System.out.println("Given parameters from controller: "+libraryID);
        System.out.println(bookIDs);
        libraryService.addBooksToLibrary(libraryID, bookIDs);
    }

    @PutMapping
    @Operation(summary = "Update personal library", description = "Update the details of a personal library")
    public ResponseEntity<String> updateLibrary(@RequestBody Library library){
        libraryService.updateLibraryName(library);
        return new ResponseEntity<>("Library with ID: " + library.getLibraryID()+" has been modified successfully!", HttpStatus.OK);
    }

    @PutMapping("/{bookID}/status")
    @Operation(summary = "Update book status", description = "Update book status")
    public ResponseEntity<String> updateBookStatus(@PathVariable int bookID, @RequestParam BookStatus status) {
        libraryService.changeBookStatus(bookID, status);
        return ResponseEntity.ok("Book status updated successfully");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Delete a book by ID")
    public ResponseEntity<String> deleteLibrary(@PathVariable int id){
        libraryService.deleteLibraryById(id);
        return new ResponseEntity<>("Library with ID: " +id+" has been deleted successfully!", HttpStatus.OK);
    }

}
