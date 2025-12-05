package com.thuvien.quanlysach.domain.shared;

import java.util.Objects;
import java.util.UUID;

public abstract class Identifier {
    private final String value;

    protected Identifier(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ID không được để trống");
        }
        this.value = value;
    }

    public String value() {
        return value;
    }

    protected static String random() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Identifier that = (Identifier) o;
        return value.equals(that.value);
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

