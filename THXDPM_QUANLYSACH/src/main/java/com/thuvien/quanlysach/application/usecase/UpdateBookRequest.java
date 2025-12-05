package com.thuvien.quanlysach.application.usecase;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateBookRequest(
        @JsonProperty("maSach") String maSach,
        @JsonProperty("ngayNhap") String ngayNhap,
        @JsonProperty("donGia") double donGia,
        @JsonProperty("soLuong") int soLuong,
        @JsonProperty("nhaXuatBan") String nhaXuatBan,
        @JsonProperty("tinhTrang") String tinhTrang,
        @JsonProperty("thue") Double thue
) {
}

