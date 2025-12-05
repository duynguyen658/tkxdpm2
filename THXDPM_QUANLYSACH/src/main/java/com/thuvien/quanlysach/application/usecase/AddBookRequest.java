package com.thuvien.quanlysach.application.usecase;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddBookRequest(
        @JsonProperty("bookType") String bookType, // "SACH_GIAO_KHOA" hoặc "SACH_THAM_KHAO"
        @JsonProperty("maSach") String maSach,
        @JsonProperty("ngayNhap") String ngayNhap, // Format: "dd/MM/yyyy" hoặc "yyyy-MM-dd"
        @JsonProperty("donGia") double donGia,
        @JsonProperty("soLuong") int soLuong,
        @JsonProperty("nhaXuatBan") String nhaXuatBan,
        // Cho sách giáo khoa
        @JsonProperty("tinhTrang") String tinhTrang, // "mới" hoặc "cũ"
        // Cho sách tham khảo
        @JsonProperty("thue") Double thue
) {
}

