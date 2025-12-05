package com.thuvien.quanlysach.application.port.output;

import com.thuvien.quanlysach.domain.entity.book.Book;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Optional<Book> findById(BookId bookId);
    List<Book> findAll();
    Book save(Book book);
    void delete(BookId bookId);
    boolean existsById(BookId bookId);
}

