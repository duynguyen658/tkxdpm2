package com.thuvien.quanlysach.domain.entity.book;

import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import com.thuvien.quanlysach.domain.entity.book.valueobject.ImportDate;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Price;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Publisher;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Quantity;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Tax;
import com.thuvien.quanlysach.domain.entity.book.valueobject.TotalAmount;

/**
 * Sách tham khảo
 * Thành tiền = số lượng * đơn giá + thuế
 */
public final class SachThamKhao implements Book {
    private final BookId id;
    private final ImportDate importDate;
    private final Price unitPrice;
    private final Quantity quantity;
    private final Publisher publisher;
    private final Tax tax;

    private SachThamKhao(
            final BookId id,
            final ImportDate importDate,
            final Price unitPrice,
            final Quantity quantity,
            final Publisher publisher,
            final Tax tax) {
        this.id = id;
        this.importDate = importDate;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.publisher = publisher;
        this.tax = tax;
    }

    public static SachThamKhao create(
            final BookId id,
            final ImportDate importDate,
            final Price unitPrice,
            final Quantity quantity,
            final Publisher publisher,
            final Tax tax) {
        return new SachThamKhao(id, importDate, unitPrice, quantity, publisher, tax);
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

    public Tax tax() {
        return tax;
    }

    @Override
    public TotalAmount calculateTotal() {
        // Thành tiền = số lượng * đơn giá + thuế
        final double baseTotal = unitPrice.multiply(quantity.value()).value();
        return TotalAmount.of(baseTotal + tax.value());
    }

    @Override
    public String getBookType() {
        return "Sách tham khảo";
    }

    public SachThamKhao update(
            final ImportDate importDate,
            final Price unitPrice,
            final Quantity quantity,
            final Publisher publisher,
            final Tax tax) {
        return new SachThamKhao(this.id, importDate, unitPrice, quantity, publisher, tax);
    }
}

