package com.thuvien.quanlysach.domain.entity.book.valueobject;

import java.util.Objects;

public final class Price {
    private final double value;
    public static final Price ZERO = new Price(0.0);

    private Price(final double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Đơn giá không được âm");
        }
        this.value = value;
    }

    public static Price of(final double value) {
        return new Price(value);
    }

    public double value() {
        return value;
    }

    public Price multiply(final double multiplier) {
        return new Price(value * multiplier);
    }

    public Price multiply(final int quantity) {
        return new Price(value * quantity);
    }

    public Price add(final Price other) {
        return new Price(this.value + other.value);
    }

    public Price add(final double amount) {
        return new Price(this.value + amount);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Price price = (Price) o;
        return Double.compare(price.value, value) == 0;
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

