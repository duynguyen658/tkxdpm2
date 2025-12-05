package com.thuvien.quanlysach.domain.entity.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookStatus;
import com.thuvien.quanlysach.domain.entity.book.valueobject.ImportDate;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Price;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Publisher;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Quantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SachGiaoKhoaTest {

    private BookId bookId;
    private ImportDate importDate;
    private Price unitPrice;
    private Quantity quantity;
    private Publisher publisher;

    @BeforeEach
    void setUp() {
        bookId = BookId.of("SGK-001");
        importDate = ImportDate.of(15, 1, 2024);
        unitPrice = Price.of(50000);
        quantity = Quantity.of(10);
        publisher = Publisher.of("NXB Giáo Dục");
    }

    @Test
    void kichBan1_shouldCalculateTotal_whenStatusIsMoi() {
        // Kịch bản 1: Sách giáo khoa tình trạng mới
        // Thành tiền = số lượng * đơn giá
        final SachGiaoKhoa book = SachGiaoKhoa.create(
                bookId, importDate, unitPrice, quantity, publisher, BookStatus.MOI);

        final double expectedTotal = 10 * 50000; // 500000
        assertEquals(expectedTotal, book.calculateTotal().value(), 0.01);
        assertEquals("Sách giáo khoa", book.getBookType());
        assertEquals(BookStatus.MOI, book.status());
    }

    @Test
    void kichBan2_shouldCalculateTotal_whenStatusIsCu() {
        // Kịch bản 2: Sách giáo khoa tình trạng cũ
        // Thành tiền = số lượng * đơn giá * 50%
        final SachGiaoKhoa book = SachGiaoKhoa.create(
                bookId, importDate, unitPrice, quantity, publisher, BookStatus.CU);

        final double expectedTotal = 10 * 50000 * 0.5; // 250000
        assertEquals(expectedTotal, book.calculateTotal().value(), 0.01);
        assertEquals("Sách giáo khoa", book.getBookType());
        assertEquals(BookStatus.CU, book.status());
    }

    @Test
    void kichBan3_shouldUpdateBook_whenUpdateMethodCalled() {
        // Kịch bản 3: Cập nhật thông tin sách giáo khoa
        final SachGiaoKhoa book = SachGiaoKhoa.create(
                bookId, importDate, unitPrice, quantity, publisher, BookStatus.MOI);

        final ImportDate newImportDate = ImportDate.of(20, 1, 2024);
        final Price newUnitPrice = Price.of(55000);
        final Quantity newQuantity = Quantity.of(15);
        final Publisher newPublisher = Publisher.of("NXB Giáo Dục Việt Nam");
        final BookStatus newStatus = BookStatus.CU;

        final SachGiaoKhoa updated = book.update(newImportDate, newUnitPrice, newQuantity, newPublisher, newStatus);

        assertEquals(bookId, updated.id()); // ID không đổi
        assertEquals(newImportDate, updated.importDate());
        assertEquals(newUnitPrice, updated.unitPrice());
        assertEquals(newQuantity, updated.quantity());
        assertEquals(newPublisher, updated.publisher());
        assertEquals(newStatus, updated.status());

        // Tính lại thành tiền với giá trị mới (cũ: 15 * 55000 * 0.5 = 412500)
        final double expectedTotal = 15 * 55000 * 0.5;
        assertEquals(expectedTotal, updated.calculateTotal().value(), 0.01);
    }

    @Test
    void kichBan4_shouldCreateBook_whenAllFieldsValid() {
        // Kịch bản 4: Tạo sách giáo khoa với tất cả thông tin hợp lệ
        final SachGiaoKhoa book = SachGiaoKhoa.create(
                bookId, importDate, unitPrice, quantity, publisher, BookStatus.MOI);

        assertNotNull(book);
        assertEquals(bookId, book.id());
        assertEquals(importDate, book.importDate());
        assertEquals(unitPrice, book.unitPrice());
        assertEquals(quantity, book.quantity());
        assertEquals(publisher, book.publisher());
        assertEquals(BookStatus.MOI, book.status());
    }
}

