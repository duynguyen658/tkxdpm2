package com.thuvien.quanlysach.domain.entity.book.valueobject;

import java.util.Objects;

public final class Publisher {
    private final String value;

    private Publisher(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Nhà xuất bản không được để trống");
        }
        this.value = value.trim();
    }

    public static Publisher of(final String value) {
        return new Publisher(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Publisher publisher = (Publisher) o;
        return Objects.equals(value, publisher.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

