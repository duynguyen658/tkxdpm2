package com.thuvien.quanlysach.application.service;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.DeleteBookUseCase;
import com.thuvien.quanlysach.domain.Result;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import com.thuvien.quanlysach.domain.exception.BookNotFoundException;

public class DeleteBookService implements DeleteBookUseCase {
    private final BookRepository bookRepository;

    public DeleteBookService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Result<String> execute(final String bookIdString) {
        try {
            final BookId bookId = BookId.of(bookIdString);
            final boolean exists = bookRepository.existsById(bookId);
            
            if (!exists) {
                return Result.fail("Không tìm thấy sách với mã: " + bookIdString);
            }

            bookRepository.delete(bookId);
            return Result.ok("Đã xóa sách thành công: " + bookIdString);

        } catch (final IllegalArgumentException ex) {
            return Result.fail(ex.getMessage());
        } catch (final Exception ex) {
            return Result.fail("Lỗi khi xóa sách: " + ex.getMessage());
        }
    }
}

