package com.example.project.controller;

import com.example.project.enums.BookStatus;
import com.example.project.model.Book;
import com.example.project.service.BookService;
import com.example.project.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/book")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Book Controller", description = "Operations related to book management")
public class BookController {
//    @Value("${file.upload-dir}")
//    private String uploadDir;

    private final BookService bookService;
    private final FileService fileService;

    public BookController(BookService bookService, FileService fileService) {
        this.bookService = bookService;
        this.fileService = fileService;
    }

    @GetMapping("/available")
    @Operation(summary = "See all available books", description = "Retrieve all books with status available from the database")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        List<Book> availableBooks = bookService.getBooksByStatus(BookStatus.AVAILABLE);
        return new ResponseEntity<>(availableBooks, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "See all books", description = "Retrieve all books from the database")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @PostMapping
    @Operation(summary = "Announce a book", description = "Set the common details for an upcoming book")
    public ResponseEntity<String> createBook(@RequestBody Book book) {
        bookService.createBook(book);
        return new ResponseEntity<>("Book created successfully!", HttpStatus.CREATED);
    }
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @PutMapping
    @Operation(summary = "Update book details", description = "Update the information for a book in the database")
    public ResponseEntity<String> updateBook(@RequestBody Book book) {
        bookService.updateBook(book);
        return new ResponseEntity<>("Book with ID: " + book.getBookID() + " has been modified successfully!", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book by ID", description = "Delete a book from the database by it's ID")
    public ResponseEntity<String> deleteBook(@PathVariable int id) {
        bookService.deleteById(id);
        return new ResponseEntity<>("Book with ID: " + id + " has been deleted successfully!", HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
   // @PostMapping(value = "/upload-file/{id}", consumes = "multipart/form-data")
    @RequestMapping(
            path = "/upload-file/{id}",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@PathVariable int id, @RequestParam("file") MultipartFile file) {
        try {
//            if (file == null || file.isEmpty()) {
//                throw new IllegalArgumentException("Uploaded file is empty or missing");
//            }
//
//            System.out.println("Upload Directory: " + uploadDir);
//
//            Path path = Paths.get(uploadDir);
//            if (!Files.exists(path)) {
//                Files.createDirectories(path);
//            }
//            System.out.println(uploadDir);
//            String filePath = Paths.get(uploadDir, file.getOriginalFilename()).toString();
//            System.out.println(filePath);
//            file.transferTo(new File(filePath));
//            bookService.updateBookFilePath(id, filePath); // Update the book with the file path

            bookService.uploadFile(id, file);

            return new ResponseEntity<>("File uploaded and path saved successfully!", HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }

    @GetMapping("/download-file/{id}")
    @Operation(summary = "Download a book", description = "Download a book from the database")
    public void downloadFile(@PathVariable int id) throws IOException {
//        try {
//            // Fetch the book by ID to get the file path
//            Book book = bookService.getBookById(id);
//            if (book == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//            }
//
//            // Get the file path from the book object
//            String filePath = book.getFilePath();
//            if (filePath == null || filePath.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//            }
//
//            // Load the file as a resource
//            Path path = Paths.get(filePath);
//            Resource resource = new UrlResource(path.toUri());
//
//            if (!resource.exists() || !resource.isReadable()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//            }
//
//            String contentType = Files.probeContentType(path);
//            if (contentType == null) {
//                contentType = "application/octet-stream"; // Fallback to binary type if unknown
//            }
//            // Set headers and return the file as a downloadable resource
//            return ResponseEntity.ok()
//                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"")
//                    .body(resource);
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
        bookService.downloadFile(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/staged")
    @Operation(summary = "See all staged books", description = "Retrieve all books with status staged from the database")
    public ResponseEntity<List<Book>> getStagedBooks() {
        List<Book> stagedBooks = bookService.getBooksByStatus(BookStatus.STAGED);
        return new ResponseEntity<>(stagedBooks, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/staged/download/{id}")
    @Operation(summary = "Download a staged books", description = "Download a staged from the database")
    public ResponseEntity<Resource> downloadStagedBook(@PathVariable int id) throws IOException {
        return bookService.downloadStagedFile(id);
//        Book book = bookService.getBookById(id);
//        if (book == null || !book.getStatus().equals(BookStatus.STAGED)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//
//        try {
//            Path filePath = Paths.get(book.getFilePath());
//            Resource resource = new UrlResource(filePath.toUri());
//
//            if (!resource.exists()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//            }
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
//                    .body(resource);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/verify/{id}")
    @Operation(summary = "Verify a staged book", description = "Verify a staged book and make it available for all users")
    public ResponseEntity<String> verifyBook(@PathVariable int id) {
//        Book book = bookService.getBookById(id);
//        if (book == null || !book.getStatus().equals(BookStatus.STAGED)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found or not in STAGED status.");
//        }
//
//        // Set the status to AVAILABLE
//        book.setStatus(BookStatus.AVAILABLE);
//        bookService.updateBook(book);
//
//        return new ResponseEntity<>("Book validated and made available for READER users!", HttpStatus.OK);
        return bookService.validateBook(id);
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Download a book from the library", description = "Download a book in .pdf format")
    public ResponseEntity<Resource> downloadAvailableBook(@PathVariable int id) {
        return bookService.downloadAvailableBooks(id);
//        Book book = bookService.getBookById(id);
//        if (book == null || !book.getStatus().equals(BookStatus.AVAILABLE)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//
//        try {
//            Path filePath = Paths.get(book.getFilePath());
//            Resource resource = new UrlResource(filePath.toUri());
//
//            if (!resource.exists()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//            }
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
//                    .body(resource);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
    }
}


