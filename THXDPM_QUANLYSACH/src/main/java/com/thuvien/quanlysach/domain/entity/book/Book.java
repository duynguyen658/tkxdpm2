package com.thuvien.quanlysach.domain.entity.book;

import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import com.thuvien.quanlysach.domain.entity.book.valueobject.ImportDate;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Price;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Publisher;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Quantity;
import com.thuvien.quanlysach.domain.entity.book.valueobject.TotalAmount;

/**
 * Base interface cho tất cả các loại sách
 */
public interface Book {
    BookId id();
    ImportDate importDate();
    Price unitPrice();
    Quantity quantity();
    Publisher publisher();
    TotalAmount calculateTotal();
    String getBookType();
}

