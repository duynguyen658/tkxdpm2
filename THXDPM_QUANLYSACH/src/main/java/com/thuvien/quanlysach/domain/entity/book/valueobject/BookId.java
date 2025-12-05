package com.thuvien.quanlysach.domain.entity.book.valueobject;

import com.thuvien.quanlysach.domain.shared.Identifier;

public final class BookId extends Identifier {
    private BookId(final String value) {
        super(value);
    }

    public static BookId of(final String value) {
        return new BookId(value);
    }

    public static BookId newId() {
        return new BookId(random());
    }
}

