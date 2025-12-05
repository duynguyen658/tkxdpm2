package com.thuvien.quanlysach.domain.entity.book.valueobject;

public enum BookStatus {
    MOI("mới"),
    CU("cũ");

    private final String displayName;

    BookStatus(final String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static BookStatus fromString(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Tình trạng sách không được để trống");
        }
        final String normalized = value.trim().toLowerCase();
        for (final BookStatus status : values()) {
            if (status.displayName.equals(normalized) || status.name().equalsIgnoreCase(normalized)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Tình trạng sách không hợp lệ: " + value + ". Chỉ chấp nhận: mới, cũ");
    }

    public boolean isMoi() {
        return this == MOI;
    }

    public boolean isCu() {
        return this == CU;
    }
}

