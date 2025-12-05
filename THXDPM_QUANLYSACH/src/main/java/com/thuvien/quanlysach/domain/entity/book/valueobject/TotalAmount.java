package com.thuvien.quanlysach.domain.entity.book.valueobject;

import java.util.Objects;

public final class TotalAmount {
    private final double value;

    private TotalAmount(final double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Thành tiền không được âm");
        }
        this.value = value;
    }

    public static TotalAmount of(final double value) {
        return new TotalAmount(value);
    }

    public double value() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TotalAmount that = (TotalAmount) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("%.2f", value);
    }
}

