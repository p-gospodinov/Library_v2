//package com.example.project.repository;
//
//import com.example.project.enums.BookStatus;
//import com.example.project.model.Book;
//import com.example.project.model.Library;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.*;
//
//@Repository
//public class LibraryRepository {
//    private final JdbcTemplate jdbcTemplate;
//
//    public LibraryRepository(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    private static final class LibraryRowMapper implements RowMapper<Library> {
//        private final Map<Integer, Library> libraryMap = new HashMap<>();
//
//        @Override
//        public Library mapRow(ResultSet rs, int rowNum) throws SQLException {
//            int libraryID = rs.getInt("libraryID");
//
//            // Get or create the Library object
//            Library library = libraryMap.computeIfAbsent(libraryID, id -> {
//                Library lib = new Library();
//                lib.setLibraryID(id);
//                try {
//                    lib.setLibraryName(rs.getString("libraryName"));
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//                return lib;
//            });
//
//            // Create and map the Book object if bookID is valid
//            int bookID = rs.getInt("bookID");
//            if (bookID > 0) { // Ensure valid bookID
//                Book book = mapBook(rs);
//                library.addBook(book);
//            }
//
//            return library;
//        }
//
//        private Book mapBook(ResultSet rs) throws SQLException {
//            Book book = new Book();
//            book.setBookID(rs.getInt("bookID"));
//            book.setTitle(rs.getString("title"));
//            book.setAuthor(rs.getString("author"));
//            book.setISBN(rs.getString("isbn"));
//            book.setGenre(rs.getString("genre"));
//            book.setLanguage(rs.getString("language"));
//            book.setRating(rs.getDouble("rating"));
//
//            String statusStr = rs.getString("status");
//            book.setStatus(statusStr != null ? BookStatus.valueOf(statusStr) : BookStatus.AVAILABLE); // Default status if null
//
//            return book;
//        }
//
//        public Collection<Library> getLibraries() {
//            return libraryMap.values();
//        }
//    }
//
//    public List<Library> findAll() {
//        String sql = "SELECT l.libraryID, l.libraryName, b.bookID, b.title, b.author, b.isbn, b.genre, b.language, b.rating, b.status " +
//                "FROM library l " +
//                "LEFT JOIN libraryBooks lb ON l.libraryID = lb.libraryID " +
//                "LEFT JOIN book b ON b.bookID = lb.bookID";
//
//        LibraryRowMapper rowMapper = new LibraryRowMapper();
//        jdbcTemplate.query(sql, rowMapper);
//        return new ArrayList<>(rowMapper.getLibraries());
//    }
//
//    public Library findById(int id) {
//        String sql = "SELECT l.libraryID, l.libraryName, b.bookID, b.title, b.author, b.isbn, b.genre, b.language, b.rating, b.status " +
//                "FROM library l " +
//                "LEFT JOIN libraryBooks lb ON l.libraryID = lb.libraryID " +
//                "LEFT JOIN book b ON b.bookID = lb.bookID " +
//                "WHERE l.libraryID = ?";
//
//        LibraryRowMapper rowMapper = new LibraryRowMapper();
//        jdbcTemplate.query(sql, rowMapper, id);
//        return rowMapper.getLibraries().stream().findFirst().orElse(null);
//    }
//
//    public int insertLibrary(Library library) {
//        String sql = "INSERT INTO library (libraryName) VALUES (?)";
//        return jdbcTemplate.update(sql, library.getLibraryName());
//    }
//
//    public void insertLibraryBooks(Library library) {
//        String sql = "INSERT INTO libraryBooks (libraryID, bookID) VALUES (?, ?)";
//        if (library.getLibraryID() == 0 || library.getBooks().isEmpty()) {
//            throw new RuntimeException("Library ID or Books are missing");
//        }
//        for (Book book : library.getBooks()) {
//            // Debug output
//            System.out.println("Inserting into libraryBooks: LibraryID=" + library.getLibraryID() + ", BookID=" + book.getBookID());
//
//            // Ensure bookID is not 0
//            if (book.getBookID() <= 0) {
//                throw new RuntimeException("Book ID must be greater than 0");
//            }
//
//            jdbcTemplate.update(sql, library.getLibraryID(), book.getBookID());
//        }
//    }
//
//    public int updateName(Library library) {
//        String sql = "UPDATE library SET libraryName = ? WHERE libraryID = ?";
//        return jdbcTemplate.update(sql, library.getLibraryName(), library.getLibraryID());
//    }
//
//    public int deleteLibraryById(int id) {
//        String sql = "DELETE FROM library WHERE libraryID = ?";
//        return jdbcTemplate.update(sql, id);
//    }
//
//    public int deleteLibraryBooksById(int id) {
//        String sql = "DELETE FROM libraryBooks WHERE libraryID = ?";
//        return jdbcTemplate.update(sql, id);
//    }
//}
package com.example.project.repository;

import com.example.project.enums.BookStatus;
import com.example.project.error_handling.exception.BookNotFoundException;
import com.example.project.model.Book;
import com.example.project.model.Library;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class LibraryRepository {
    private final JdbcTemplate jdbcTemplate;

    public LibraryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final class LibraryRowMapper implements RowMapper<Library> {
        private final Map<Integer, Library> libraryMap = new HashMap<>();

        @Override
        public Library mapRow(ResultSet rs, int rowNum) throws SQLException {
            int libraryID = rs.getInt("libraryID");

            // Get or create the Library object
            Library library = libraryMap.computeIfAbsent(libraryID, id -> {
                Library lib = new Library();
                lib.setLibraryID(id);
                try {
                    lib.setLibraryName(rs.getString("libraryName"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return lib;
            });

            // Create and map the Book object if bookID is valid
            int bookID = rs.getInt("bookID");
            if (bookID > 0) { // Ensure valid bookID
                Book book = mapBook(rs);
                library.addBook(book);
            }

            return library;
        }

        private Book mapBook(ResultSet rs) throws SQLException {
            Book book = new Book();
            book.setBookID(rs.getInt("bookID"));
            book.setTitle(rs.getString("title"));
            book.setAuthor(rs.getString("author"));
            book.setISBN(rs.getString("isbn"));
            book.setGenre(rs.getString("genre"));
            book.setLanguage(rs.getString("language"));
            book.setRating(rs.getDouble("rating"));

            String statusStr = rs.getString("status");
            book.setStatus(statusStr != null ? BookStatus.valueOf(statusStr) : BookStatus.AVAILABLE); // Default status if null

            return book;
        }

        private static final class BookRowMapper implements RowMapper<Book> {
            @Override
            public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
                Book book = new Book();
                book.setBookID(rs.getInt("bookID"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setISBN(rs.getString("isbn"));
                book.setGenre(rs.getString("genre"));
                book.setLanguage(rs.getString("language"));
                book.setRating(rs.getDouble("rating"));

                String statusStr = rs.getString("status");
                book.setStatus(statusStr != null ? BookStatus.valueOf(statusStr) : BookStatus.AVAILABLE); // Default status if null

                return book;
            }
        }

        public Collection<Library> getLibraries() {
            return libraryMap.values();
        }
    }

    public List<Library> findAll() {
        String sql = "SELECT l.libraryID, l.libraryName, b.bookID, b.title, b.author, b.isbn, b.genre, b.language, b.rating, b.status " +
                "FROM library l " +
                "LEFT JOIN libraryBooks lb ON l.libraryID = lb.libraryID " +
                "LEFT JOIN book b ON b.bookID = lb.bookID";

        LibraryRowMapper rowMapper = new LibraryRowMapper();
        jdbcTemplate.query(sql, rowMapper);
        return new ArrayList<>(rowMapper.getLibraries());
    }

    public Library findById(int id) {
        String sql = "SELECT l.libraryID, l.libraryName, b.bookID, b.title, b.author, b.isbn, b.genre, b.language, b.rating, b.status " +
                "FROM library l " +
                "LEFT JOIN libraryBooks lb ON l.libraryID = lb.libraryID " +
                "LEFT JOIN book b ON b.bookID = lb.bookID " +
                "WHERE l.libraryID = ?";

        LibraryRowMapper rowMapper = new LibraryRowMapper();
        jdbcTemplate.query(sql, rowMapper, id);
        return rowMapper.getLibraries().stream().findFirst().orElse(null);
    }

    public int insertLibrary(Library library) {
        String sql = "INSERT INTO library (libraryName) VALUES (?)";
        return jdbcTemplate.update(sql, library.getLibraryName());
    }

    public void insertLibraryBooks(Library library) {
        String checkSql = "SELECT COUNT(*) FROM libraryBooks WHERE libraryID = ? AND bookID = ?";
        String insertSql = "INSERT INTO libraryBooks (libraryID, bookID) VALUES (?, ?)";

        if (library.getLibraryID() == 0 || library.getBooks().isEmpty()) {
            throw new RuntimeException("Library ID or Books are missing");
        }

        for (Book book : library.getBooks()) {
            // Check if the book already exists in the library
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, library.getLibraryID(), book.getBookID());

            // Only insert if not already present
            if (count != null && count == 0) {
                System.out.println("Inserting into libraryBooks: LibraryID=" + library.getLibraryID() + ", BookID=" + book.getBookID());
                jdbcTemplate.update(insertSql, library.getLibraryID(), book.getBookID());
            } else {
                System.out.println("Skipping insertion for LibraryID=" + library.getLibraryID() + ", BookID=" + book.getBookID() + " as it already exists");
            }
        }
    }

    public List<Book> findBooksByName(String name) {
        String sql = "SELECT b.bookID, b.title, b.author, b.isbn, b.genre, b.language, b.rating, b.status " +
                "FROM book b " +
                "WHERE b.title LIKE ?";

        return jdbcTemplate.query(sql, new LibraryRowMapper.BookRowMapper(), "%" + name + "%");
    }

    public List<Book> findBooksByAuthor(String author) {
        String sql = "SELECT b.bookID, b.title, b.author, b.isbn, b.genre, b.language, b.rating, b.status " +
                "FROM book b " +
                "WHERE b.author LIKE ?";

        return jdbcTemplate.query(sql, new LibraryRowMapper.BookRowMapper(), "%" + author + "%");
    }

    public List<Book> findBooksInLibrarySorted(int libraryId, String sortBy) {
        // Sanitize the sortBy input to prevent SQL injection
        String sortColumn;
        switch (sortBy.toLowerCase()) {
            case "genre":
                sortColumn = "b.genre";
                break;
            case "author":
                sortColumn = "b.author";
                break;
            case "title":
                sortColumn = "b.title";
                break;
            default:
                sortColumn = "b.title"; // Default sorting
        }

        String sql = "SELECT b.bookID, b.title, b.author, b.isbn, b.genre, b.language, b.rating, b.status " +
                "FROM libraryBooks lb " +
                "JOIN book b ON b.bookID = lb.bookID " +
                "WHERE lb.libraryID = ? " +
                "ORDER BY " + sortColumn;

        return jdbcTemplate.query(sql, new LibraryRowMapper.BookRowMapper(), libraryId);
    }

//    public int updateBookRating(int bookId, double rating) {
//        String sql = "UPDATE book SET rating = ? WHERE bookID = ?";
//        return jdbcTemplate.update(sql, rating, bookId);
//    }
//public int insertBookRating(int bookId, double rating) {
//    String sql = "INSERT INTO bookRatings (bookID, rating) VALUES (?, ?)";
//    return jdbcTemplate.update(sql, bookId, rating);
//}
//
//    public Double getAverageRating(int bookId) {
//        String sql = "SELECT AVG(rating) FROM bookRatings WHERE bookID = ?";
//        return jdbcTemplate.queryForObject(sql, Double.class, bookId);
//    }
//
//    public boolean updateBookRating(int bookId, double rating) {
//        // This will be used to check if a rating exists
//        String checkSql = "SELECT COUNT(*) FROM bookRatings WHERE bookID = ? AND rating = ?";
//        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, bookId, rating);
//
//        if (count == null || count == 0) {
//            return insertBookRating(bookId, rating) > 0;
//        }
//
//        // Updating the rating is not required in this case, as we are inserting ratings only.
//        return true;
//    }
public void insertBookRatingAndComment(int bookId, int userId, double rating, String comment) {
    String sql = "INSERT INTO bookRatings (bookID, userID, rating, comment) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(sql, bookId, userId, rating, comment);
    Double averageRating = calculateAverageRating(bookId);

    // Update the average rating in the book table
    updateBookRatingInTable(bookId, averageRating);
}
private boolean bookExists(int bookId) {
    String sql = "SELECT COUNT(*) FROM book WHERE bookID = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, bookId);
    return count != null && count > 0;
}

    // Calculate the average rating for a book
    private Double calculateAverageRating(int bookId) {
        String sql = "SELECT AVG(rating) FROM bookRatings WHERE bookID = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, bookId);
    }

    // Update the average rating in the book table
    private int updateBookRatingInTable(int bookId, Double averageRating) {
        String sql = "UPDATE book SET rating = ? WHERE bookID = ?";
        return jdbcTemplate.update(sql, averageRating, bookId);
    }

    // Insert a new book rating and update the average rating
    public boolean insertBookRating(int bookId, double rating) {
        if (!bookExists(bookId)) {
            throw new IllegalArgumentException("Book with ID " + bookId + " does not exist");
        }

        // Insert the new rating into bookRatings
        String insertSql = "INSERT INTO bookRatings (bookID, rating) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, bookId, rating);

        // Calculate the new average rating
        Double averageRating = calculateAverageRating(bookId);

        // Update the average rating in the book table
        return updateBookRatingInTable(bookId, averageRating) > 0;
    }

    // Update an existing book rating and recalculate the average rating
    public boolean updateBookRating(int bookId, double rating) {
        if (!bookExists(bookId)) {
            throw new BookNotFoundException("Book with ID " + bookId + " does not exist");
        }

        // Insert or update the rating in bookRatings
        String updateSql = "REPLACE INTO bookRatings (bookID, rating) VALUES (?, ?)";
        jdbcTemplate.update(updateSql, bookId, rating);

        // Calculate the new average rating
        Double averageRating = calculateAverageRating(bookId);

        // Update the average rating in the book table
        return updateBookRatingInTable(bookId, averageRating) > 0;
    }

    public int updateBookStatus(int bookID, BookStatus status) {
        String sql = "UPDATE book SET status = ? WHERE bookID = ?";
        return jdbcTemplate.update(sql, status.name(), bookID);
    }

    public int countBookOccurrences(int bookID) {
        String sql = "SELECT COUNT(*) FROM libraryBooks WHERE bookID = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, bookID);
    }

    public int updateName(Library library) {
        String sql = "UPDATE library SET libraryName = ? WHERE libraryID = ?";
        return jdbcTemplate.update(sql, library.getLibraryName(), library.getLibraryID());
    }

    public int deleteLibraryById(int id) {
        String sql = "DELETE FROM library WHERE libraryID = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int deleteLibraryBooksById(int id) {
        String sql = "DELETE FROM libraryBooks WHERE libraryID = ?";
        return jdbcTemplate.update(sql, id);
    }
}
