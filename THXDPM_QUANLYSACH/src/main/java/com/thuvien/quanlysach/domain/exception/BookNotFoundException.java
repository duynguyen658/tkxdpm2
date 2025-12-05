package com.thuvien.quanlysach.domain.exception;

import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;

public class BookNotFoundException extends DomainException {
    public BookNotFoundException(final BookId bookId) {
        super("Không tìm thấy sách với mã: " + bookId.value());
    }

    public BookNotFoundException(final String bookId) {
        super("Không tìm thấy sách với mã: " + bookId);
    }
}

