package com.thuvien.quanlysach.application.service;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.AddBookRequest;
import com.thuvien.quanlysach.application.usecase.AddBookResponse;
import com.thuvien.quanlysach.application.usecase.AddBookUseCase;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddBookService implements AddBookUseCase {
    private final BookRepository bookRepository;

    public AddBookService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Result<AddBookResponse> execute(final AddBookRequest request) {
        try {
            // Parse và validate các value objects
            final BookId bookId = request.maSach() != null && !request.maSach().isBlank()
                    ? BookId.of(request.maSach())
                    : BookId.newId();
            
            final ImportDate importDate = parseImportDate(request.ngayNhap());
            final Price unitPrice = Price.of(request.donGia());
            final Quantity quantity = Quantity.of(request.soLuong());
            final Publisher publisher = Publisher.of(request.nhaXuatBan());

            // Kiểm tra mã sách đã tồn tại chưa
            if (bookRepository.existsById(bookId)) {
                return Result.fail("Mã sách đã tồn tại: " + bookId.value());
            }

            // Tạo sách theo loại
            final Book book;
            if ("SACH_GIAO_KHOA".equalsIgnoreCase(request.bookType()) 
                    || "sách giáo khoa".equalsIgnoreCase(request.bookType())) {
                if (request.tinhTrang() == null || request.tinhTrang().isBlank()) {
                    return Result.fail("Sách giáo khoa cần có tình trạng (mới/cũ)");
                }
                final BookStatus status = BookStatus.fromString(request.tinhTrang());
                book = SachGiaoKhoa.create(bookId, importDate, unitPrice, quantity, publisher, status);
            } else if ("SACH_THAM_KHAO".equalsIgnoreCase(request.bookType())
                    || "sách tham khảo".equalsIgnoreCase(request.bookType())) {
                if (request.thue() == null) {
                    return Result.fail("Sách tham khảo cần có thuế");
                }
                final Tax tax = Tax.of(request.thue());
                book = SachThamKhao.create(bookId, importDate, unitPrice, quantity, publisher, tax);
            } else {
                return Result.fail("Loại sách không hợp lệ. Chỉ chấp nhận: SACH_GIAO_KHOA hoặc SACH_THAM_KHAO");
            }

            final Book saved = bookRepository.save(book);
            return Result.ok(AddBookResponse.from(saved));

        } catch (final IllegalArgumentException ex) {
            return Result.fail(ex.getMessage());
        } catch (final Exception ex) {
            return Result.fail("Lỗi khi thêm sách: " + ex.getMessage());
        }
    }

    private ImportDate parseImportDate(final String dateString) {
        if (dateString == null || dateString.isBlank()) {
            throw new IllegalArgumentException("Ngày nhập không được để trống");
        }

        // Thử parse theo format dd/MM/yyyy
        try {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            final LocalDate date = LocalDate.parse(dateString, formatter);
            return ImportDate.of(date);
        } catch (final DateTimeParseException e1) {
            // Thử parse theo format yyyy-MM-dd
            try {
                final LocalDate date = LocalDate.parse(dateString);
                return ImportDate.of(date);
            } catch (final DateTimeParseException e2) {
                throw new IllegalArgumentException("Ngày nhập không hợp lệ. Format: dd/MM/yyyy hoặc yyyy-MM-dd", e2);
            }
        }
    }
}

