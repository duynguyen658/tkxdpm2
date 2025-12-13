package com.thuvien.quanlysach.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * JPA Entity cho Book
 * Sử dụng Single Table Inheritance để lưu cả SachGiaoKhoa và SachThamKhao
 */
@Entity
@Table(name = "books")
public class BookEntity {
    
    @Id
    @Column(name = "id", length = 100)
    private String id;
    
    @Column(name = "book_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BookType bookType;
    
    @Column(name = "ngay_nhap", nullable = false)
    private LocalDate ngayNhap;
    
    @Column(name = "don_gia", nullable = false)
    private Double donGia;
    
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;
    
    @Column(name = "nha_xuat_ban", nullable = false, length = 255)
    private String nhaXuatBan;
    
    // Fields cho SachGiaoKhoa
    @Column(name = "tinh_trang", length = 10)
    @Enumerated(EnumType.STRING)
    private BookStatus tinhTrang;
    
    // Fields cho SachThamKhao
    @Column(name = "thue")
    private Double thue;
    
    public enum BookType {
        SACH_GIAO_KHOA,
        SACH_THAM_KHAO
    }
    
    public enum BookStatus {
        MOI,
        CU
    }
    
    // Default constructor for JPA
    public BookEntity() {
    }
    
    // Constructor
    public BookEntity(
            String id,
            BookType bookType,
            LocalDate ngayNhap,
            Double donGia,
            Integer soLuong,
            String nhaXuatBan,
            BookStatus tinhTrang,
            Double thue) {
        this.id = id;
        this.bookType = bookType;
        this.ngayNhap = ngayNhap;
        this.donGia = donGia;
        this.soLuong = soLuong;
        this.nhaXuatBan = nhaXuatBan;
        this.tinhTrang = tinhTrang;
        this.thue = thue;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public BookType getBookType() {
        return bookType;
    }
    
    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }
    
    public LocalDate getNgayNhap() {
        return ngayNhap;
    }
    
    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }
    
    public Double getDonGia() {
        return donGia;
    }
    
    public void setDonGia(Double donGia) {
        this.donGia = donGia;
    }
    
    public Integer getSoLuong() {
        return soLuong;
    }
    
    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }
    
    public String getNhaXuatBan() {
        return nhaXuatBan;
    }
    
    public void setNhaXuatBan(String nhaXuatBan) {
        this.nhaXuatBan = nhaXuatBan;
    }
    
    public BookStatus getTinhTrang() {
        return tinhTrang;
    }
    
    public void setTinhTrang(BookStatus tinhTrang) {
        this.tinhTrang = tinhTrang;
    }
    
    public Double getThue() {
        return thue;
    }
    
    public void setThue(Double thue) {
        this.thue = thue;
    }
}

