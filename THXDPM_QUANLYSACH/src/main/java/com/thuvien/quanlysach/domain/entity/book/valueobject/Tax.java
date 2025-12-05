package com.thuvien.quanlysach.domain.entity.book.valueobject;

import java.util.Objects;

public final class Tax {
    private final double value;

    private Tax(final double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Thuế không được âm");
        }
        this.value = value;
    }

    public static Tax of(final double value) {
        return new Tax(value);
    }

    public double value() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Tax tax = (Tax) o;
        return Double.compare(tax.value, value) == 0;
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

