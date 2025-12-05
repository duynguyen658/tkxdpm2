package com.thuvien.quanlysach.application.usecase;

import com.thuvien.quanlysach.domain.entity.book.Book;
import com.thuvien.quanlysach.domain.entity.book.SachGiaoKhoa;
import com.thuvien.quanlysach.domain.entity.book.SachThamKhao;

public record BookResponse(
        String maSach,
        String loaiSach,
        String ngayNhap,
        double donGia,
        int soLuong,
        String nhaXuatBan,
        String tinhTrang, // null nếu là sách tham khảo
        Double thue, // null nếu là sách giáo khoa
        double thanhTien
) {
    public static BookResponse from(final Book book) {
        if (book instanceof SachGiaoKhoa sachGiaoKhoa) {
            return new BookResponse(
                    book.id().value(),
                    book.getBookType(),
                    book.importDate().toString(),
                    book.unitPrice().value(),
                    book.quantity().value(),
                    book.publisher().value(),
                    sachGiaoKhoa.status().displayName(),
                    null,
                    book.calculateTotal().value()
            );
        } else if (book instanceof SachThamKhao sachThamKhao) {
            return new BookResponse(
                    book.id().value(),
                    book.getBookType(),
                    book.importDate().toString(),
                    book.unitPrice().value(),
                    book.quantity().value(),
                    book.publisher().value(),
                    null,
                    sachThamKhao.tax().value(),
                    book.calculateTotal().value()
            );
        } else {
            throw new IllegalArgumentException("Loại sách không được hỗ trợ: " + book.getClass());
        }
    }
}

