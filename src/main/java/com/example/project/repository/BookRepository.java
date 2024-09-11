package com.example.project.repository;

import com.example.project.enums.BookStatus;
import com.example.project.model.Book;
import com.example.project.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class BookRepository {
    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
            book.setStatus(BookStatus.valueOf(rs.getString("status")));
            book.setFilePath(rs.getString("file_path"));

            System.out.println("Mapped Book: " + book);

            return book;
        }
    }

    public List<Book> findAll() {
        String sql = "SELECT * FROM book";
        return jdbcTemplate.query(sql, new BookRepository.BookRowMapper());
    }

    public Book findById(int id) {
        String sql = "SELECT bookID, " +
                "    title," +
                "    author," +
                "    isbn, " +
                "    genre, " +
                "    language, " +
                "    rating, " +
                "    status, " +
                "    file_path " +
                "FROM book " +
                "WHERE bookID = ?";
        return jdbcTemplate.queryForObject(sql, new BookRepository.BookRowMapper(), id);
    }

    public List<Book> findBooksByIds(List<Integer> bookIDs) {
//        String idList = bookIDs.stream()
//                .map(String::valueOf)
//                .collect(Collectors.joining(", "));
//        String sql = "SELECT bookID, title, author, isbn, genre, language, rating, status " +
//                "FROM book WHERE bookID IN (" + idList + ")";
//        return jdbcTemplate.query(sql, new BookRowMapper());
        if (bookIDs == null || bookIDs.isEmpty()) {
            // Return an empty list if no book IDs are provided
            return new ArrayList<>();
        }

        // Prepare a SQL query using placeholders to avoid SQL injection
        String sql = "SELECT bookID, title, author, isbn, genre, language, rating, status " +
                "FROM book WHERE bookID IN (" +
                bookIDs.stream().map(id -> "?").collect(Collectors.joining(", ")) + ")";

        // Convert the List of Integers to an array of Objects to pass to the query method
        Object[] params = bookIDs.toArray();

        return jdbcTemplate.query(sql, params, new BookRowMapper());
    }

    public int insert(Book book) {
        String sql = "INSERT INTO book (title, author, isbn, genre, language, rating, status, file_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, book.getTitle(), book.getAuthor(), book.getISBN(), book.getGenre(), book.getLanguage(), book.getRating(), book.getStatus().name(), book.getFilePath());
    }

    public int update(Book book) {
        String sql = "UPDATE book SET title = ?, author = ?, isbn = ?, genre = ?, language = ?, rating = ?, status = ?, file_path = ? WHERE bookID = ?";
        return jdbcTemplate.update(sql,book.getTitle(), book.getAuthor(), book.getISBN(), book.getGenre(), book.getLanguage(), book.getRating(), book.getStatus().name(), book.getFilePath(), book.getBookID());
    }

    public int deleteById(int id) {
        String sql = "DELETE FROM book WHERE bookID = ?";
        return jdbcTemplate.update(sql, id);
    }

    public List<Book> findByStatus(BookStatus status) {
        String sql = "SELECT * FROM book WHERE status = ?";
        return jdbcTemplate.query(sql, new BookRepository.BookRowMapper(), status.toString());
    }

}
