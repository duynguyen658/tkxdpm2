package com.thuvien.quanlysach.domain.entity.book;

import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookStatus;
import com.thuvien.quanlysach.domain.entity.book.valueobject.ImportDate;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Price;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Publisher;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Quantity;
import com.thuvien.quanlysach.domain.entity.book.valueobject.TotalAmount;

/**
 * Sách giáo khoa
 * - Nếu tình trạng là mới: thành tiền = số lượng * đơn giá
 * - Nếu tình trạng là cũ: thành tiền = số lượng * đơn giá * 50%
 */
public final class SachGiaoKhoa implements Book {
    private final BookId id;
    private final ImportDate importDate;
    private final Price unitPrice;
    private final Quantity quantity;
    private final Publisher publisher;
    private final BookStatus status;

    private SachGiaoKhoa(
            final BookId id,
            final ImportDate importDate,
            final Price unitPrice,
            final Quantity quantity,
            final Publisher publisher,
            final BookStatus status) {
        this.id = id;
        this.importDate = importDate;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.publisher = publisher;
        this.status = status;
    }

    public static SachGiaoKhoa create(
            final BookId id,
            final ImportDate importDate,
            final Price unitPrice,
            final Quantity quantity,
            final Publisher publisher,
            final BookStatus status) {
        return new SachGiaoKhoa(id, importDate, unitPrice, quantity, publisher, status);
    }

    @Override
    public BookId id() {
        return id;
    }

    @Override
    public ImportDate importDate() {
        return importDate;
    }

    @Override
    public Price unitPrice() {
        return unitPrice;
    }

    @Override
    public Quantity quantity() {
        return quantity;
    }

    @Override
    public Publisher publisher() {
        return publisher;
    }

    public BookStatus status() {
        return status;
    }

    @Override
    public TotalAmount calculateTotal() {
        final double baseTotal = unitPrice.multiply(quantity.value()).value();
        if (status.isMoi()) {
            // Tình trạng mới: thành tiền = số lượng * đơn giá
            return TotalAmount.of(baseTotal);
        } else {
            // Tình trạng cũ: thành tiền = số lượng * đơn giá * 50%
            return TotalAmount.of(baseTotal * 0.5);
        }
    }

    @Override
    public String getBookType() {
        return "Sách giáo khoa";
    }

    public SachGiaoKhoa update(
            final ImportDate importDate,
            final Price unitPrice,
            final Quantity quantity,
            final Publisher publisher,
            final BookStatus status) {
        return new SachGiaoKhoa(this.id, importDate, unitPrice, quantity, publisher, status);
    }
}

