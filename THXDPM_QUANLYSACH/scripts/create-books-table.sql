-- Script SQL để tạo bảng books thủ công
-- Chạy script này nếu Hibernate không thể tự tạo bảng

-- Kết nối với database quanlysach
\c quanlysach

-- Tạo bảng books
CREATE TABLE IF NOT EXISTS books (
    id VARCHAR(100) NOT NULL,
    book_type VARCHAR(20) NOT NULL CHECK (book_type IN ('SACH_GIAO_KHOA', 'SACH_THAM_KHAO')),
    ngay_nhap DATE NOT NULL,
    don_gia DOUBLE PRECISION NOT NULL,
    so_luong INTEGER NOT NULL,
    nha_xuat_ban VARCHAR(255) NOT NULL,
    tinh_trang VARCHAR(10) CHECK (tinh_trang IN ('MOI', 'CU')),
    thue DOUBLE PRECISION,
    PRIMARY KEY (id)
);

-- Cấp quyền cho users
GRANT ALL PRIVILEGES ON TABLE books TO postgres;
GRANT ALL PRIVILEGES ON TABLE books TO iot_user;
GRANT ALL PRIVILEGES ON TABLE books TO public;

-- Tạo index (tùy chọn, để tối ưu performance)
CREATE INDEX IF NOT EXISTS idx_books_book_type ON books(book_type);
CREATE INDEX IF NOT EXISTS idx_books_nha_xuat_ban ON books(nha_xuat_ban);

-- Kiểm tra bảng đã được tạo
\d books

