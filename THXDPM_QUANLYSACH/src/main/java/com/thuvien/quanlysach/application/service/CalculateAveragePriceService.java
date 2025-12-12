package com.thuvien.quanlysach.application.service;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.AveragePriceResponse;
import com.thuvien.quanlysach.application.usecase.CalculateAveragePriceUseCase;
import com.thuvien.quanlysach.domain.Result;
import com.thuvien.quanlysach.domain.entity.book.SachThamKhao;
import java.util.List;
import java.util.stream.Collectors;

public class CalculateAveragePriceService implements CalculateAveragePriceUseCase {
    private final BookRepository bookRepository;

    public CalculateAveragePriceService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Result<AveragePriceResponse> execute() {
        try {
            final List<SachThamKhao> sachThamKhaos = bookRepository.findAll().stream()
                    .filter(book -> book instanceof SachThamKhao)
                    .map(book -> (SachThamKhao) book)
                    .collect(Collectors.toList());

            if (sachThamKhaos.isEmpty()) {
                return Result.fail("Không có sách tham khảo trong hệ thống");
            }

            final double tongDonGia = sachThamKhaos.stream()
                    .mapToDouble(book -> book.unitPrice().value())
                    .sum();

            final double trungBinhCongDonGia = tongDonGia / sachThamKhaos.size();

            return Result.ok(new AveragePriceResponse(
                    trungBinhCongDonGia,
                    sachThamKhaos.size()
            ));
        } catch (final Exception ex) {
            return Result.fail("Lỗi khi tính trung bình cộng đơn giá: " + ex.getMessage());
        }
    }
}

