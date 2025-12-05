package com.thuvien.quanlysach.application.usecase;

import com.thuvien.quanlysach.domain.entity.book.Book;

public record AddBookResponse(
        String bookId,
        String bookType,
        String message
) {
    public static AddBookResponse from(final Book book) {
        return new AddBookResponse(
                book.id().value(),
                book.getBookType(),
                "Đã thêm sách thành công"
        );
    }
}

