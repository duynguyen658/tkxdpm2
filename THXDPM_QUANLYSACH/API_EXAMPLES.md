# API Examples - Quản lý Sách

## 1. Thêm Sách Giáo Khoa

### Request
```bash
POST /api/books
Content-Type: application/json
```

```json
{
  "bookType": "SACH_GIAO_KHOA",
  "maSach": "SGK-001",
  "ngayNhap": "15/01/2024",
  "donGia": 50000,
  "soLuong": 10,
  "nhaXuatBan": "NXB Giáo Dục",
  "tinhTrang": "mới"
}
```

### Response (Success - HTTP 201)
```json
{
  "success": true,
  "message": "Đã thêm sách thành công",
  "book": {
    "bookId": "SGK-001",
    "bookType": "Sách giáo khoa"
  }
}
```

### Response (Error - HTTP 400)
```json
{
  "success": false,
  "message": "Sách giáo khoa cần có tình trạng (mới/cũ)"
}
```

---

## 2. Thêm Sách Tham Khảo

### Request
```bash
POST /api/books
Content-Type: application/json
```

```json
{
  "bookType": "SACH_THAM_KHAO",
  "maSach": "STK-001",
  "ngayNhap": "15/01/2024",
  "donGia": 80000,
  "soLuong": 5,
  "nhaXuatBan": "NXB Khoa Học",
  "thue": 5000
}
```

### Response (Success - HTTP 201)
```json
{
  "success": true,
  "message": "Đã thêm sách thành công",
  "book": {
    "bookId": "STK-001",
    "bookType": "Sách tham khảo"
  }
}
```

---

## 3. Cập nhật Sách

### Request
```bash
PUT /api/books/SGK-001
Content-Type: application/json
```

```json
{
  "ngayNhap": "20/01/2024",
  "donGia": 55000,
  "soLuong": 15,
  "nhaXuatBan": "NXB Giáo Dục",
  "tinhTrang": "cũ"
}
```

### Response (Success - HTTP 200)
```json
{
  "success": true,
  "message": "Đã cập nhật sách thành công",
  "book": {
    "bookId": "SGK-001",
    "bookType": "Sách giáo khoa"
  }
}
```

### Response (Error - HTTP 400)
```json
{
  "success": false,
  "message": "Không tìm thấy sách với mã: SGK-999"
}
```

---

## 4. Xóa Sách

### Request
```bash
DELETE /api/books/SGK-001
```

### Response (Success - HTTP 200)
```json
{
  "success": true,
  "message": "Đã xóa sách thành công: SGK-001"
}
```

### Response (Error - HTTP 400)
```json
{
  "success": false,
  "message": "Không tìm thấy sách với mã: SGK-999"
}
```

---

## 5. Lấy Danh Sách Tất Cả Sách

### Request
```bash
GET /api/books
```

### Response (Success - HTTP 200)
```json
{
  "success": true,
  "total": 2,
  "books": [
    {
      "maSach": "SGK-001",
      "loaiSach": "Sách giáo khoa",
      "ngayNhap": "15/01/2024",
      "donGia": 50000.0,
      "soLuong": 10,
      "nhaXuatBan": "NXB Giáo Dục",
      "tinhTrang": "mới",
      "thue": null,
      "thanhTien": 500000.0
    },
    {
      "maSach": "STK-001",
      "loaiSach": "Sách tham khảo",
      "ngayNhap": "15/01/2024",
      "donGia": 80000.0,
      "soLuong": 5,
      "nhaXuatBan": "NXB Khoa Học",
      "tinhTrang": null,
      "thue": 5000.0,
      "thanhTien": 405000.0
    }
  ]
}
```

---

## Quy Tắc Tính Thành Tiền

### Sách Giáo Khoa
- **Tình trạng mới**: `thanhTien = soLuong × donGia`
  - Ví dụ: 10 × 50000 = 500000
  
- **Tình trạng cũ**: `thanhTien = soLuong × donGia × 50%`
  - Ví dụ: 10 × 50000 × 0.5 = 250000

### Sách Tham Khảo
- `thanhTien = soLuong × donGia + thue`
  - Ví dụ: 5 × 80000 + 5000 = 405000

---

## Ví dụ với cURL

### Thêm sách giáo khoa (mới)
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "bookType": "SACH_GIAO_KHOA",
    "maSach": "SGK-001",
    "ngayNhap": "15/01/2024",
    "donGia": 50000,
    "soLuong": 10,
    "nhaXuatBan": "NXB Giáo Dục",
    "tinhTrang": "mới"
  }'
```

### Thêm sách giáo khoa (cũ)
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "bookType": "SACH_GIAO_KHOA",
    "maSach": "SGK-002",
    "ngayNhap": "10/01/2024",
    "donGia": 50000,
    "soLuong": 10,
    "nhaXuatBan": "NXB Giáo Dục",
    "tinhTrang": "cũ"
  }'
```

### Thêm sách tham khảo
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "bookType": "SACH_THAM_KHAO",
    "maSach": "STK-001",
    "ngayNhap": "15/01/2024",
    "donGia": 80000,
    "soLuong": 5,
    "nhaXuatBan": "NXB Khoa Học",
    "thue": 5000
  }'
```

### Lấy danh sách sách
```bash
curl -X GET http://localhost:8080/api/books
```

### Cập nhật sách
```bash
curl -X PUT http://localhost:8080/api/books/SGK-001 \
  -H "Content-Type: application/json" \
  -d '{
    "ngayNhap": "20/01/2024",
    "donGia": 55000,
    "soLuong": 15,
    "nhaXuatBan": "NXB Giáo Dục",
    "tinhTrang": "cũ"
  }'
```

### Xóa sách
```bash
curl -X DELETE http://localhost:8080/api/books/SGK-001
```

---

---

## 6. Tìm kiếm Sách

### Request
```bash
GET /api/books/search?keyword=Giáo
```

### Response (Success - HTTP 200)
```json
{
  "success": true,
  "total": 2,
  "keyword": "Giáo",
  "books": [
    {
      "maSach": "SGK-001",
      "loaiSach": "Sách giáo khoa",
      "ngayNhap": "15/01/2024",
      "donGia": 50000.0,
      "soLuong": 10,
      "nhaXuatBan": "NXB Giáo Dục",
      "tinhTrang": "mới",
      "thue": null,
      "thanhTien": 500000.0
    }
  ]
}
```

**Lưu ý**: Tìm kiếm theo mã sách, nhà xuất bản, hoặc loại sách (không phân biệt hoa thường).

---

## 7. Tính Tổng Thành Tiền Theo Từng Loại

### Request
```bash
GET /api/books/statistics/total-by-type
```

### Response (Success - HTTP 200)
```json
{
  "success": true,
  "tongThanhTienSachGiaoKhoa": 750000.0,
  "tongThanhTienSachThamKhao": 405000.0,
  "tongThanhTienTatCa": 1155000.0
}
```

---

## 8. Tính Trung Bình Cộng Đơn Giá Sách Tham Khảo

### Request
```bash
GET /api/books/statistics/average-price
```

### Response (Success - HTTP 200)
```json
{
  "success": true,
  "trungBinhCongDonGia": 80000.0,
  "soLuongSachThamKhao": 2
}
```

### Response (Khi không có sách tham khảo)
```json
{
  "success": true,
  "trungBinhCongDonGia": 0.0,
  "soLuongSachThamKhao": 0
}
```

---

## 9. Lấy Sách Giáo Khoa Theo Nhà Xuất Bản

### Request
```bash
GET /api/books/publisher/NXB Giáo Dục
```

### Response (Success - HTTP 200)
```json
{
  "success": true,
  "publisher": "NXB Giáo Dục",
  "total": 2,
  "books": [
    {
      "maSach": "SGK-001",
      "loaiSach": "Sách giáo khoa",
      "ngayNhap": "15/01/2024",
      "donGia": 50000.0,
      "soLuong": 10,
      "nhaXuatBan": "NXB Giáo Dục",
      "tinhTrang": "mới",
      "thue": null,
      "thanhTien": 500000.0
    },
    {
      "maSach": "SGK-002",
      "loaiSach": "Sách giáo khoa",
      "ngayNhap": "10/01/2024",
      "donGia": 50000.0,
      "soLuong": 10,
      "nhaXuatBan": "NXB Giáo Dục",
      "tinhTrang": "cũ",
      "thue": null,
      "thanhTien": 250000.0
    }
  ]
}
```

**Lưu ý**: Chỉ trả về sách giáo khoa, không bao gồm sách tham khảo.

---

## Validation Rules

1. **Mã sách**: Nếu không cung cấp, hệ thống sẽ tự động tạo UUID
2. **Ngày nhập**: Format `dd/MM/yyyy` hoặc `yyyy-MM-dd`
3. **Đơn giá**: Phải >= 0
4. **Số lượng**: Phải > 0
5. **Nhà xuất bản**: Không được để trống
6. **Tình trạng** (sách giáo khoa): Chỉ chấp nhận "mới" hoặc "cũ"
7. **Thuế** (sách tham khảo): Phải >= 0

