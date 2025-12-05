package com.thuvien.quanlysach.application.service;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.BookResponse;
import com.thuvien.quanlysach.application.usecase.GetBooksByPublisherUseCase;
import com.thuvien.quanlysach.domain.Result;
import com.thuvien.quanlysach.domain.entity.book.SachGiaoKhoa;
import java.util.List;
import java.util.stream.Collectors;

public class GetBooksByPublisherService implements GetBooksByPublisherUseCase {
    private final BookRepository bookRepository;

    public GetBooksByPublisherService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Result<List<BookResponse>> execute(final String publisher) {
        try {
            if (publisher == null || publisher.isBlank()) {
                return Result.fail("Tên nhà xuất bản không được để trống");
            }

            final String publisherLower = publisher.toLowerCase().trim();
            final List<BookResponse> results = bookRepository.findAll().stream()
                    .filter(book -> book instanceof SachGiaoKhoa)
                    .filter(book -> book.publisher().value().toLowerCase().contains(publisherLower))
                    .map(BookResponse::from)
                    .collect(Collectors.toList());

            return Result.ok(results);
        } catch (final Exception ex) {
            return Result.fail("Lỗi khi lấy sách theo nhà xuất bản: " + ex.getMessage());
        }
    }
}

