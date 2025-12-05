package com.thuvien.quanlysach.infrastructure.repository;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.domain.entity.book.Book;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBookRepository implements BookRepository {
    private final Map<String, Book> books = new ConcurrentHashMap<>();

    @Override
    public Optional<Book> findById(final BookId bookId) {
        return Optional.ofNullable(books.get(bookId.value()));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    @Override
    public Book save(final Book book) {
        books.put(book.id().value(), book);
        return book;
    }

    @Override
    public void delete(final BookId bookId) {
        books.remove(bookId.value());
    }

    @Override
    public boolean existsById(final BookId bookId) {
        return books.containsKey(bookId.value());
    }
}

