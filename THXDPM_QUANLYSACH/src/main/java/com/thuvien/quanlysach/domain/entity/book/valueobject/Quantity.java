package com.thuvien.quanlysach.domain.entity.book.valueobject;

import java.util.Objects;

public final class Quantity {
    private final int value;

    private Quantity(final int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        this.value = value;
    }

    public static Quantity of(final int value) {
        return new Quantity(value);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Quantity quantity = (Quantity) o;
        return value == quantity.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

