package com.thuvien.quanlysach.domain.entity.book.valueobject;

import java.time.LocalDate;
import java.util.Objects;

public final class ImportDate {
    private final LocalDate date;

    private ImportDate(final LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Ngày nhập không được để trống");
        }
        this.date = date;
    }

    public static ImportDate of(final LocalDate date) {
        return new ImportDate(date);
    }

    public static ImportDate of(final int day, final int month, final int year) {
        try {
            return new ImportDate(LocalDate.of(year, month, day));
        } catch (final Exception e) {
            throw new IllegalArgumentException("Ngày nhập không hợp lệ: " + day + "/" + month + "/" + year, e);
        }
    }

    public LocalDate date() {
        return date;
    }

    public int day() {
        return date.getDayOfMonth();
    }

    public int month() {
        return date.getMonthValue();
    }

    public int year() {
        return date.getYear();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ImportDate that = (ImportDate) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", day(), month(), year());
    }
}

