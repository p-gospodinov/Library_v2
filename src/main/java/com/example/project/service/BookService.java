package com.example.project.service;

import com.example.project.enums.BookStatus;
import com.example.project.error_handling.exception.*;
import com.example.project.model.Book;
import com.example.project.repository.BookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class BookService {
    @Value("${file.upload-dir}")
    private String uploadDir;
    private final BookRepository bookRepository;
    private final FileService fileService;
    private final UserStatusService userStatusService;

    public BookService(BookRepository bookRepository, FileService fileService, UserStatusService userStatusService) {
        this.bookRepository = bookRepository;
        this.fileService = fileService;
        this.userStatusService = userStatusService;
    }

    public List<Book> getAllBooks() {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        List<Book> books = bookRepository.findAll();
        if (books == null || books.isEmpty()){
            throw new BookNotFoundException("Books not found!");
        }
        return books;
    }

    public Book getBookById(int id) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        Book book = bookRepository.findById(id);
        if(book == null){
            throw new BookNotFoundException("Book with ID: "+id+" not found!");
        }
        return book;
    }

    public void createBook(Book book) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        try{
        book.setStatus(BookStatus.ANNOUNCED);
        book.setRating(3);
        bookRepository.insert(book);
        } catch (Exception e) {
            throw new BookOperationException("Failed to create a book");
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        String directory = "uploads/"; // Directory where files will be stored
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(directory + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());
        return filePath.toString();  // Return the file path to store in the database
    }

    public void updateBookFilePath(int bookId, String filePath) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID: "+bookId+" not found!");
        }
            book.setFilePath(filePath);
            book.setStatus(BookStatus.STAGED);
            try{
            bookRepository.update(book);
            } catch (Exception e) {
                throw new BookOperationException("Failed to update book file path!");
            }

    }

    public void updateBook(Book book) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        try{
            bookRepository.update(book);
        } catch (Exception e) {
            throw new BookOperationException("Failed to update book details!");
        }
    }

    public void deleteById(int id) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        try{
        bookRepository.deleteById(id);
        } catch (Exception e) {
            throw new BookOperationException("Failed to delete book with ID: "+id+"!" );
        }
    }

    public List<Book> getBooksByStatus(BookStatus status) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }
        List<Book> books = bookRepository.findByStatus(status);
        if(books == null || books.isEmpty()){
            throw new BookNotFoundException("Books with this status not found!");
        }
        return books;
    }

    public void uploadFile(int id, MultipartFile file) throws IOException {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("Uploaded file is empty or missing");
        }

        //System.out.println("Upload Directory: " + uploadDir);

        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        //System.out.println(uploadDir);
        String filePath = Paths.get(uploadDir, file.getOriginalFilename()).toString();
        //System.out.println(filePath);
        file.transferTo(new File(filePath));
        updateBookFilePath(id, filePath); // Update the book with the file path

    }

    public ResponseEntity<Resource> downloadFile(int id) throws IOException {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }
        try {
            Book book = getBookById(id);
            if (book == null) {
               throw new BookNotFoundException("Book with ID: "+id+" not found to be downloaded!");
            }

            // Get the file path from the book object
            String filePath = book.getFilePath();
            if (filePath == null || filePath.isEmpty()) {
                throw new EmptyFileException("File path not found!");
            }

            // Load the file as a resource
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new EmptyFileException("The file of the book doesn't exist or is not readable!");
            }

            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Fallback to binary type if unknown
            }
            // Set headers and return the file as a downloadable resource

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"")
                    .body(resource);

        } catch (Exception e) {
            throw new BookOperationException("Couldn't download book with ID: "+id+"!");
        }
    }

    public ResponseEntity<Resource> downloadStagedFile(int id) throws IOException {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }
        Book book = getBookById(id);
        if (book == null || !book.getStatus().equals(BookStatus.STAGED)) {
            throw new BookNotFoundException("Book not found with ID: "+id+" and status STAGED!");
        }

        try {
            Path filePath = Paths.get(book.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new EmptyFileException("File not found!");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new BookOperationException("Couldn't download staged book!");
        }
    }

    public ResponseEntity<String> validateBook(int id){
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }
        Book book = getBookById(id);
        if (book == null || !book.getStatus().equals(BookStatus.STAGED)) {
            throw new BookNotFoundException("Book with ID: "+id+" and status STAGED not found!");
        }

        // Set the status to AVAILABLE
        book.setStatus(BookStatus.AVAILABLE);
        updateBook(book);
        return new ResponseEntity<>("Book validated and made available for READER users!", HttpStatus.OK);
    }

    public ResponseEntity<Resource> downloadAvailableBooks(int id){
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }
        Book book = getBookById(id);
        if (book == null || !book.getStatus().equals(BookStatus.AVAILABLE)) {
            throw new BookNotFoundException("Book with ID: "+id+" and status AVAILABLE not found!");
        }

        try {
            Path filePath = Paths.get(book.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new EmptyFileException("File not found!");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new BookOperationException("Couldn't download available book!");
        }
    }
}
//    public void saveFile(MultipartFile file, Book book) throws IOException {
//        book.setFileData(file.getBytes());
//        book.setFileName(file.getOriginalFilename());
//        book.setFileType(file.getContentType());
//
//        bookRepository.insert(book);
//    }

