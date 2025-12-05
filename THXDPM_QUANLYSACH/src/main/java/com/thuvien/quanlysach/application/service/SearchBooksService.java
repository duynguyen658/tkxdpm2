package com.thuvien.quanlysach.application.service;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.BookResponse;
import com.thuvien.quanlysach.application.usecase.SearchBooksUseCase;
import com.thuvien.quanlysach.domain.Result;
import java.util.List;
import java.util.stream.Collectors;

public class SearchBooksService implements SearchBooksUseCase {
    private final BookRepository bookRepository;

    public SearchBooksService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Result<List<BookResponse>> execute(final String keyword) {
        try {
            if (keyword == null || keyword.isBlank()) {
                return Result.fail("Từ khóa tìm kiếm không được để trống");
            }

            final String lowerKeyword = keyword.toLowerCase().trim();
            final List<BookResponse> results = bookRepository.findAll().stream()
                    .filter(book -> matchesKeyword(book, lowerKeyword))
                    .map(BookResponse::from)
                    .collect(Collectors.toList());

            return Result.ok(results);
        } catch (final Exception ex) {
            return Result.fail("Lỗi khi tìm kiếm sách: " + ex.getMessage());
        }
    }

    private boolean matchesKeyword(final com.thuvien.quanlysach.domain.entity.book.Book book, final String keyword) {
        // Tìm theo mã sách
        if (book.id().value().toLowerCase().contains(keyword)) {
            return true;
        }
        // Tìm theo nhà xuất bản
        if (book.publisher().value().toLowerCase().contains(keyword)) {
            return true;
        }
        // Tìm theo loại sách
        if (book.getBookType().toLowerCase().contains(keyword)) {
            return true;
        }
        return false;
    }
}

