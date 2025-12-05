package com.thuvien.quanlysach.application.usecase;

import com.thuvien.quanlysach.domain.entity.book.Book;

public record UpdateBookResponse(
        String bookId,
        String bookType,
        String message
) {
    public static UpdateBookResponse from(final Book book) {
        return new UpdateBookResponse(
                book.id().value(),
                book.getBookType(),
                "Đã cập nhật sách thành công"
        );
    }
}

