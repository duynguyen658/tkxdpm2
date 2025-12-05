package com.thuvien.quanlysach.application.service;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.UpdateBookRequest;
import com.thuvien.quanlysach.application.usecase.UpdateBookResponse;
import com.thuvien.quanlysach.application.usecase.UpdateBookUseCase;
import com.thuvien.quanlysach.domain.Result;
import com.thuvien.quanlysach.domain.entity.book.Book;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookStatus;
import com.thuvien.quanlysach.domain.entity.book.SachGiaoKhoa;
import com.thuvien.quanlysach.domain.entity.book.SachThamKhao;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import com.thuvien.quanlysach.domain.entity.book.valueobject.ImportDate;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Price;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Publisher;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Quantity;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Tax;
import com.thuvien.quanlysach.domain.exception.BookNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UpdateBookService implements UpdateBookUseCase {
    private final BookRepository bookRepository;

    public UpdateBookService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Result<UpdateBookResponse> execute(final String bookIdString, final UpdateBookRequest request) {
        try {
            final BookId bookId = BookId.of(bookIdString);
            final Book existingBook = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException(bookId));

            // Parse các value objects
            final ImportDate importDate = parseImportDate(request.ngayNhap());
            final Price unitPrice = Price.of(request.donGia());
            final Quantity quantity = Quantity.of(request.soLuong());
            final Publisher publisher = Publisher.of(request.nhaXuatBan());

            // Cập nhật theo loại sách
            final Book updatedBook;
            if (existingBook instanceof SachGiaoKhoa) {
                if (request.tinhTrang() == null || request.tinhTrang().isBlank()) {
                    return Result.fail("Sách giáo khoa cần có tình trạng (mới/cũ)");
                }
                final BookStatus status = BookStatus.fromString(request.tinhTrang());
                final SachGiaoKhoa sachGiaoKhoa = (SachGiaoKhoa) existingBook;
                updatedBook = sachGiaoKhoa.update(importDate, unitPrice, quantity, publisher, status);
            } else if (existingBook instanceof SachThamKhao) {
                if (request.thue() == null) {
                    return Result.fail("Sách tham khảo cần có thuế");
                }
                final Tax tax = Tax.of(request.thue());
                final SachThamKhao sachThamKhao = (SachThamKhao) existingBook;
                updatedBook = sachThamKhao.update(importDate, unitPrice, quantity, publisher, tax);
            } else {
                return Result.fail("Loại sách không được hỗ trợ");
            }

            final Book saved = bookRepository.save(updatedBook);
            return Result.ok(UpdateBookResponse.from(saved));

        } catch (final BookNotFoundException ex) {
            return Result.fail(ex.getMessage());
        } catch (final IllegalArgumentException ex) {
            return Result.fail(ex.getMessage());
        } catch (final Exception ex) {
            return Result.fail("Lỗi khi cập nhật sách: " + ex.getMessage());
        }
    }

    private ImportDate parseImportDate(final String dateString) {
        if (dateString == null || dateString.isBlank()) {
            throw new IllegalArgumentException("Ngày nhập không được để trống");
        }

        try {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            final LocalDate date = LocalDate.parse(dateString, formatter);
            return ImportDate.of(date);
        } catch (final DateTimeParseException e1) {
            try {
                final LocalDate date = LocalDate.parse(dateString);
                return ImportDate.of(date);
            } catch (final DateTimeParseException e2) {
                throw new IllegalArgumentException("Ngày nhập không hợp lệ. Format: dd/MM/yyyy hoặc yyyy-MM-dd", e2);
            }
        }
    }
}

