package com.thuvien.quanlysach.application.service;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.CalculateTotalByTypeUseCase;
import com.thuvien.quanlysach.application.usecase.TotalByTypeResponse;
import com.thuvien.quanlysach.domain.Result;
import com.thuvien.quanlysach.domain.entity.book.SachGiaoKhoa;
import com.thuvien.quanlysach.domain.entity.book.SachThamKhao;

public class CalculateTotalByTypeService implements CalculateTotalByTypeUseCase {
    private final BookRepository bookRepository;

    public CalculateTotalByTypeService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Result<TotalByTypeResponse> execute() {
        try {
            double tongThanhTienSachGiaoKhoa = 0.0;
            double tongThanhTienSachThamKhao = 0.0;

            for (final var book : bookRepository.findAll()) {
                final double thanhTien = book.calculateTotal().value();
                if (book instanceof SachGiaoKhoa) {
                    tongThanhTienSachGiaoKhoa += thanhTien;
                } else if (book instanceof SachThamKhao) {
                    tongThanhTienSachThamKhao += thanhTien;
                }
            }

            final double tongThanhTienTatCa = tongThanhTienSachGiaoKhoa + tongThanhTienSachThamKhao;

            return Result.ok(new TotalByTypeResponse(
                    tongThanhTienSachGiaoKhoa,
                    tongThanhTienSachThamKhao,
                    tongThanhTienTatCa
            ));
        } catch (final Exception ex) {
            return Result.fail("Lỗi khi tính tổng thành tiền: " + ex.getMessage());
        }
    }
}

