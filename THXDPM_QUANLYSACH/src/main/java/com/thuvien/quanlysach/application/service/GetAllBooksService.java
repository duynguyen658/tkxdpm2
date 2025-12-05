package com.thuvien.quanlysach.application.service;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.BookResponse;
import com.thuvien.quanlysach.application.usecase.GetAllBooksUseCase;
import com.thuvien.quanlysach.domain.Result;
import java.util.List;
import java.util.stream.Collectors;

public class GetAllBooksService implements GetAllBooksUseCase {
    private final BookRepository bookRepository;

    public GetAllBooksService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Result<List<BookResponse>> execute() {
        try {
            final List<BookResponse> books = bookRepository.findAll().stream()
                    .map(BookResponse::from)
                    .collect(Collectors.toList());
            return Result.ok(books);
        } catch (final Exception ex) {
            return Result.fail("Lỗi khi lấy danh sách sách: " + ex.getMessage());
        }
    }
}

