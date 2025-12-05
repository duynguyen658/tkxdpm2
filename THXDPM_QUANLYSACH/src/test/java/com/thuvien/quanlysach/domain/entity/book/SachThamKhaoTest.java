package com.thuvien.quanlysach.domain.entity.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import com.thuvien.quanlysach.domain.entity.book.valueobject.ImportDate;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Price;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Publisher;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Quantity;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Tax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SachThamKhaoTest {

    private BookId bookId;
    private ImportDate importDate;
    private Price unitPrice;
    private Quantity quantity;
    private Publisher publisher;
    private Tax tax;

    @BeforeEach
    void setUp() {
        bookId = BookId.of("STK-001");
        importDate = ImportDate.of(15, 1, 2024);
        unitPrice = Price.of(80000);
        quantity = Quantity.of(5);
        publisher = Publisher.of("NXB Khoa Học");
        tax = Tax.of(5000);
    }

    @Test
    void kichBan1_shouldCalculateTotal_withTax() {
        // Kịch bản 1: Sách tham khảo tính thành tiền
        // Thành tiền = số lượng * đơn giá + thuế
        final SachThamKhao book = SachThamKhao.create(
                bookId, importDate, unitPrice, quantity, publisher, tax);

        final double expectedTotal = 5 * 80000 + 5000; // 405000
        assertEquals(expectedTotal, book.calculateTotal().value(), 0.01);
        assertEquals("Sách tham khảo", book.getBookType());
        assertEquals(tax, book.tax());
    }

    @Test
    void kichBan2_shouldCalculateTotal_whenTaxIsZero() {
        // Kịch bản 2: Sách tham khảo không có thuế (thuế = 0)
        final Tax zeroTax = Tax.of(0);
        final SachThamKhao book = SachThamKhao.create(
                bookId, importDate, unitPrice, quantity, publisher, zeroTax);

        final double expectedTotal = 5 * 80000 + 0; // 400000
        assertEquals(expectedTotal, book.calculateTotal().value(), 0.01);
    }

    @Test
    void kichBan3_shouldUpdateBook_whenUpdateMethodCalled() {
        // Kịch bản 3: Cập nhật thông tin sách tham khảo
        final SachThamKhao book = SachThamKhao.create(
                bookId, importDate, unitPrice, quantity, publisher, tax);

        final ImportDate newImportDate = ImportDate.of(20, 1, 2024);
        final Price newUnitPrice = Price.of(90000);
        final Quantity newQuantity = Quantity.of(8);
        final Publisher newPublisher = Publisher.of("NXB Khoa Học và Kỹ Thuật");
        final Tax newTax = Tax.of(8000);

        final SachThamKhao updated = book.update(newImportDate, newUnitPrice, newQuantity, newPublisher, newTax);

        assertEquals(bookId, updated.id()); // ID không đổi
        assertEquals(newImportDate, updated.importDate());
        assertEquals(newUnitPrice, updated.unitPrice());
        assertEquals(newQuantity, updated.quantity());
        assertEquals(newPublisher, updated.publisher());
        assertEquals(newTax, updated.tax());

        // Tính lại thành tiền với giá trị mới (8 * 90000 + 8000 = 728000)
        final double expectedTotal = 8 * 90000 + 8000;
        assertEquals(expectedTotal, updated.calculateTotal().value(), 0.01);
    }

    @Test
    void kichBan4_shouldCreateBook_whenAllFieldsValid() {
        // Kịch bản 4: Tạo sách tham khảo với tất cả thông tin hợp lệ
        final SachThamKhao book = SachThamKhao.create(
                bookId, importDate, unitPrice, quantity, publisher, tax);

        assertNotNull(book);
        assertEquals(bookId, book.id());
        assertEquals(importDate, book.importDate());
        assertEquals(unitPrice, book.unitPrice());
        assertEquals(quantity, book.quantity());
        assertEquals(publisher, book.publisher());
        assertEquals(tax, book.tax());
    }
}

