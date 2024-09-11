package com.example.project.service;

import com.example.project.enums.BookStatus;
import com.example.project.error_handling.exception.*;
import com.example.project.model.Book;
import com.example.project.model.Library;

import com.example.project.model.User;
import com.example.project.repository.BookRepository;
import com.example.project.repository.LibraryRepository;
import org.springframework.stereotype.Service;

import java.nio.BufferOverflowException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final UserStatusService userStatusService;

    public LibraryService(LibraryRepository libraryRepository, BookRepository bookRepository, UserService userService, UserStatusService userStatusService) {
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    public List<Library> getAllLibraries(){
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        List<Library> libraries = libraryRepository.findAll();
        if (libraries == null || libraries.isEmpty()){
            throw new LibraryNotFoundException("No personal libraries found!");
        }
        return libraries;
    }

    public Library getLibraryById(int id){
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        Library library = libraryRepository.findById(id);
        if(library == null){
            throw new LibraryNotFoundException("Library with ID: "+id+" not found!");
        }
        return library;
    }

    public Library createLibrary(Library library) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }
        try {
            libraryRepository.insertLibrary(library);
        } catch (Exception e) {
            throw new UserOperationException("Failed to create user", e);
        }
        return library;
    }

    public void addBooksToLibrary(int libraryID, List<Integer> bookIDs) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        Library library = libraryRepository.findById(libraryID);

        List<Book> books = bookRepository.findBooksByIds(bookIDs);
        if(books == null || books.isEmpty()){
            throw new BookNotFoundException("Books with the given ID's not found!");
        }
        if (books.size() != bookIDs.size()) {
            List<Integer> foundBookIDs = books.stream()
                    .map(Book::getBookID)
                    .collect(Collectors.toList());

            List<Integer> missingBookIDs = bookIDs.stream()
                    .filter(id -> !foundBookIDs.contains(id))
                    .collect(Collectors.toList());

            throw new BookNotFoundException("The following books do not exist: " + missingBookIDs);
        }

        for (Book book : books) {
           // System.out.println("Adding Book to Library: " + book);
            library.addBook(book);
        }
        //System.out.println("Books found: " + books);
        //System.out.println("Book before insertLibraryBooks: " + books);
        try{
        libraryRepository.insertLibraryBooks(library);
        } catch (Exception e) {
            throw new LibraryOperationException("Failed to insert books in the library user");
        }
        //System.out.println("Adding Book to Library: " + library.getBooks().toString());

    }

    public List<Book> searchBooksByName(String name) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        List<Book> books = libraryRepository.findBooksByName(name);
        if(books == null || books.isEmpty()){
            throw new BookNotFoundException("Books with this name not found!");
        }
        return books;
    }

    public List<Book> searchBooksByAuthor(String author) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        List<Book> books = libraryRepository.findBooksByAuthor(author);
        if(books == null || books.isEmpty()){
            throw new BookNotFoundException("Books from this author not found!");
        }
        return books;
    }

    public List<Book> getBooksInLibrarySorted(int libraryId, String sortBy) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        return libraryRepository.findBooksInLibrarySorted(libraryId, sortBy);
    }

//    public boolean updateBookRating(int bookId, double rating) {
//        if (rating < 0 || rating > 5) { // Assuming rating should be between 0 and 5
//            throw new IllegalArgumentException("Rating must be between 0 and 5");
//        }
//        return libraryRepository.updateBookRating(bookId, rating) > 0;
//    }
//public boolean addBookRating(int bookId, double rating) {
//    if (rating < 0 || rating > 5) { // Assuming rating should be between 0 and 5
//        throw new IllegalArgumentException("Rating must be between 0 and 5");
//    }
//    if (libraryRepository.insertBookRating(bookId, rating) > 0) {
//        Double averageRating = libraryRepository.getAverageRating(bookId);
//        if (averageRating != null) {
//            return libraryRepository.updateBookRating(bookId, averageRating);
//        }
//    }
//    return false;
//}
public boolean updateBookRating(int bookId, double rating) {
    if (!userStatusService.isCurrentUserActive()) {
        throw new InactiveUserException("User is not active and cannot perform this action.");
    }
    // Call repository method to update the book rating
    return libraryRepository.updateBookRating(bookId, rating);
}

    public void changeBookStatus(int bookID, BookStatus status) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        int rowsAffected = libraryRepository.updateBookStatus(bookID, status);
        if (rowsAffected == 0) {
            throw new BookOperationException("Failed to update book status. Book ID not found: " + bookID);
        }
    }

    public int getBookOccurrenceCount(int bookID) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        return libraryRepository.countBookOccurrences(bookID);
    }
    public boolean insertBookRatings(int bookId, double rating) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }
        // Call repository method to update the book rating
       return libraryRepository.insertBookRating(bookId, rating);
    }
    public void addBookRatingAndComment(int bookId, double rating, String comment) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        // Validate rating
        if (rating < 0 || rating > 5) { // Assuming rating should be between 0 and 5
            throw new BookOperationException("Rating must be between 0 and 5.");
        }

        // Verify book exists
        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found.");
        }

        // Verify user exists
        int id = userService.getCurrentUserId();
        User user = userService.getUserById(id);  // Assuming a User repository or service exists
        if (user == null) {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }

        // Add rating and comment
        try {
            libraryRepository.insertBookRatingAndComment(bookId, id, rating, comment);
        } catch (Exception e) {
            throw new BookOperationException("Failed to add rating and comment for book ID: " + bookId);
        }
    }
    public void updateLibraryName(Library library){
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }
        try{
        libraryRepository.updateName(library);
        } catch (Exception e) {
            throw new LibraryOperationException("Failed to update library name");
        }
    }

    public void deleteLibraryById(int id){
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        int rowsAffected = libraryRepository.deleteLibraryBooksById(id) + libraryRepository.deleteLibraryById(id);
        if (rowsAffected == 0) {
            throw new LibraryNotFoundException("Failed to delete library with ID: " + id);
        }
    }
}
