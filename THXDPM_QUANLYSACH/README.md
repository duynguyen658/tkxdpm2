# Module Quản lý Sách - Thư viện X

**Sub-module** của dự án THXDPM - Quản lý sách cho Thư viện X, hỗ trợ 2 loại sách:
- **Sách giáo khoa**: Có tình trạng (mới/cũ), tính thành tiền theo tình trạng
- **Sách tham khảo**: Có thuế, tính thành tiền = số lượng × đơn giá + thuế

> **Lưu ý**: Đây là một sub-module, không phải project độc lập. Để chạy, cần build từ parent project.

## 📋 Mục lục

- [Quick Start](#-quick-start)
- [Kiến trúc](#-kiến-trúc)
- [Công nghệ](#-công-nghệ)
- [Cấu trúc dự án](#-cấu-trúc-dự-án)
- [API Endpoints](#-api-endpoints)
- [Quy tắc tính thành tiền](#-quy-tắc-tính-thành-tiền)
- [Chạy ứng dụng](#-chạy-ứng-dụng)
- [Giao diện Web](#-giao-diện-web)
- [Chạy Tests](#-chạy-tests)
- [Test Cases](#-test-cases)
- [Test API](#-test-api)
- [Troubleshooting](#-troubleshooting)

## ⚡ Quick Start

```bash
# 1. Build và chạy ứng dụng
cd d:\TKXDPM\THXDPM_QUANLYSACH
mvn clean install
mvn spring-boot:run

# 2. Test API (từ terminal khác)
curl -X GET http://localhost:8080/api/books

# 3. Chạy tests
mvn test
```

Ứng dụng sẽ chạy tại: **http://localhost:8080**

## 🏗️ Kiến trúc

Dự án sử dụng **Clean Architecture / Hexagonal Architecture** với các layer:

- **Domain Layer**: Entities, Value Objects, Exceptions (Business logic)
- **Application Layer**: Use Cases, Services, Ports (Application logic)
- **Infrastructure Layer**: Repository implementations (Technical details)
- **Interfaces Layer**: REST Controllers (API endpoints)

## 🛠️ Công nghệ

- **Java 17**
- **Spring Boot 4.0.0**
- **Maven** (Multi-module)
- **JUnit 5** (Testing)

## 📁 Cấu trúc dự án

```
src/main/java/com/thuvien/quanlysach/
├── domain/
│   ├── entity/book/
│   │   ├── Book.java (interface)
│   │   ├── SachGiaoKhoa.java
│   │   ├── SachThamKhao.java
│   │   └── valueobject/
│   ├── exception/
│   └── shared/
├── application/
│   ├── config/
│   ├── port/output/
│   ├── service/
│   └── usecase/
├── infrastructure/
│   └── repository/
├── interfaces/
│   └── rest/
└── QuanLySachApplication.java
```

## 🔌 API Endpoints

### CRUD Operations

#### 1. Thêm sách (POST /api/books)

**Request Body - Sách giáo khoa:**
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

**Request Body - Sách tham khảo:**
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

**Response:**
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

#### 2. Cập nhật sách (PUT /api/books/{bookId})

**Request Body:**
```json
{
  "ngayNhap": "20/01/2024",
  "donGia": 55000,
  "soLuong": 15,
  "nhaXuatBan": "NXB Giáo Dục",
  "tinhTrang": "cũ"
}
```

**Response:**
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

#### 3. Xóa sách (DELETE /api/books/{bookId})

**Response:**
```json
{
  "success": true,
  "message": "Đã xóa sách thành công: SGK-001"
}
```

#### 4. Lấy danh sách tất cả sách (GET /api/books)

**Response:**
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

### Tìm kiếm & Lọc

#### 5. Tìm kiếm sách (GET /api/books/search?keyword={keyword})

Tìm kiếm theo: mã sách, nhà xuất bản, loại sách (không phân biệt hoa thường)

**Ví dụ:**
```bash
GET /api/books/search?keyword=Giáo
GET /api/books/search?keyword=SGK-001
GET /api/books/search?keyword=Khoa
```

**Response:**
```json
{
  "success": true,
  "total": 1,
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

#### 6. Lấy sách giáo khoa theo nhà xuất bản (GET /api/books/publisher/{publisher})

Chỉ trả về sách giáo khoa của nhà xuất bản được chỉ định.

**Ví dụ:**
```bash
GET /api/books/publisher/NXB Giáo Dục
```

**Response:**
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
    }
  ]
}
```

### Thống kê

#### 7. Tính tổng thành tiền theo từng loại (GET /api/books/statistics/total-by-type)

**Response:**
```json
{
  "success": true,
  "tongThanhTienSachGiaoKhoa": 750000.0,
  "tongThanhTienSachThamKhao": 405000.0,
  "tongThanhTienTatCa": 1155000.0
}
```

#### 8. Tính trung bình cộng đơn giá sách tham khảo (GET /api/books/statistics/average-price)

**Response (Khi có sách tham khảo):**
```json
{
  "success": true,
  "trungBinhCongDonGia": 80000.0,
  "soLuongSachThamKhao": 2
}
```

**Response (Khi không có sách tham khảo):**
```json
{
  "success": false,
  "message": "Không có sách tham khảo trong hệ thống"
}
```

**Lưu ý:** Nếu không có sách tham khảo, API sẽ trả về lỗi (HTTP 500) với thông báo tương ứng.

## 📊 Quy tắc tính thành tiền

### Sách giáo khoa

- **Tình trạng mới**: `Thành tiền = Số lượng × Đơn giá`
  - Ví dụ: 10 cuốn × 50,000đ = **500,000đ**

- **Tình trạng cũ**: `Thành tiền = Số lượng × Đơn giá × 50%`
  - Ví dụ: 10 cuốn × 50,000đ × 0.5 = **250,000đ**

### Sách tham khảo

- `Thành tiền = Số lượng × Đơn giá + Thuế`
  - Ví dụ: 5 cuốn × 80,000đ + 5,000đ = **405,000đ**

## 📝 Validation Rules

### Thêm sách (AddBookRequest)

| Field | Bắt buộc | Mô tả |
|-------|----------|-------|
| `bookType` | ✅ | "SACH_GIAO_KHOA" hoặc "SACH_THAM_KHAO" |
| `maSach` | ❌ | Tự động tạo UUID nếu không cung cấp |
| `ngayNhap` | ✅ | Format: "dd/MM/yyyy" hoặc "yyyy-MM-dd" |
| `donGia` | ✅ | Phải > 0 |
| `soLuong` | ✅ | Phải > 0 |
| `nhaXuatBan` | ✅ | Không được để trống |
| `tinhTrang` | ✅* | Bắt buộc cho sách giáo khoa ("mới" hoặc "cũ") |
| `thue` | ✅* | Bắt buộc cho sách tham khảo, phải ≥ 0 |

*Chỉ bắt buộc tùy theo loại sách

### Kịch bản thêm sách

#### Kịch bản 1: Thêm sách thành công (đủ thông tin, trạng thái)
- ✅ Sách giáo khoa với tình trạng "mới" hoặc "cũ"
- ✅ Sách tham khảo với thuế đầy đủ
- ✅ Tất cả các trường bắt buộc đều được cung cấp

#### Kịch bản 2: Thêm sách thất bại (validation fails)
- ❌ Sách giáo khoa thiếu tình trạng → Lỗi: "Sách giáo khoa cần có tình trạng (mới/cũ)"
- ❌ Sách tham khảo thiếu thuế → Lỗi: "Sách tham khảo cần có thuế"
- ❌ Mã sách đã tồn tại → Lỗi: "Mã sách đã tồn tại: {maSach}"

#### Kịch bản 3: Thêm sách thành công (thiếu mã sách, hệ thống tự tạo)
- ✅ Không cung cấp `maSach` → Hệ thống tự động tạo UUID
- ✅ Mã sách tự tạo là unique và không trùng lặp

### Cập nhật sách (UpdateBookRequest)

- Tất cả các field giống như thêm sách (trừ `bookType` và `maSach`)
- `maSach` trong body phải khớp với `{bookId}` trong URL

## 🚀 Chạy ứng dụng

### Yêu cầu

- Java 17+
- Maven 3.6+

### Cách 1: Từ parent project (khuyến nghị)

```bash
# Từ thư mục gốc (d:\TKXDPM)
cd d:\TKXDPM

# Build tất cả modules
mvn clean install

# Chạy module quản lý sách
mvn spring-boot:run -pl THXDPM_QUANLYSACH
```

### Cách 2: Từ module này

```bash
# Vào thư mục module
cd d:\TKXDPM\THXDPM_QUANLYSACH

# Build module
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

### Cách 3: Chạy JAR file

```bash
# Build và package
mvn clean package

# Chạy JAR
java -jar target/THXDPM_QUANLYSACH-0.0.1-SNAPSHOT.jar
```

Ứng dụng sẽ chạy tại: **http://localhost:8080**

### Giao diện Web

Sau khi chạy ứng dụng, bạn có thể truy cập giao diện web tại: **http://localhost:8080**

Giao diện web cung cấp:
- 📊 Dashboard thống kê (tổng số sách, tổng thành tiền theo loại, trung bình đơn giá)
- 📚 Danh sách sách với đầy đủ thông tin
- ➕ Thêm sách mới (form động theo loại sách)
- ✏️ Sửa thông tin sách
- 🗑️ Xóa sách (có xác nhận)
- 🔍 Tìm kiếm sách theo mã sách, nhà xuất bản, loại sách
- 📑 Lọc sách giáo khoa theo nhà xuất bản
- 📱 Responsive design (tương thích mobile và desktop)

## 🧪 Chạy Tests

### Chạy tất cả tests

```bash
# Từ thư mục module
mvn test

# Hoặc từ parent project
mvn test -pl THXDPM_QUANLYSACH
```

### Chạy test cho một class cụ thể

```bash
mvn test -Dtest=UpdateBookServiceTest
mvn test -Dtest=SachGiaoKhoaTest
```

### Chạy test cho một method cụ thể

```bash
mvn test -Dtest=UpdateBookServiceTest#kichBan1_shouldUpdateSachGiaoKhoa_whenAllFieldsValid
```

### Xem test coverage

```bash
mvn test jacoco:report
# Kết quả tại: target/site/jacoco/index.html
```

Xem thêm hướng dẫn test tại: [TEST_GUIDE.md](TEST_GUIDE.md)

## 📡 Test API

### Sử dụng cURL

#### Thêm sách giáo khoa
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

#### Thêm sách tham khảo
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

#### Lấy danh sách sách
```bash
curl -X GET http://localhost:8080/api/books
```

#### Tìm kiếm sách
```bash
curl -X GET "http://localhost:8080/api/books/search?keyword=Giáo"
```

#### Tính tổng thành tiền theo loại
```bash
curl -X GET http://localhost:8080/api/books/statistics/total-by-type
```

#### Tính trung bình đơn giá sách tham khảo
```bash
curl -X GET http://localhost:8080/api/books/statistics/average-price
```

#### Lấy sách giáo khoa theo nhà xuất bản
```bash
curl -X GET "http://localhost:8080/api/books/publisher/NXB%20Giáo%20Dục"
```

#### Cập nhật sách
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

#### Xóa sách
```bash
curl -X DELETE http://localhost:8080/api/books/SGK-001
```

### Sử dụng Postman hoặc REST Client

Xem file [API_EXAMPLES.md](API_EXAMPLES.md) để có các ví dụ request/response chi tiết.

## 🐛 Troubleshooting

### Lỗi: Port 8080 đã được sử dụng

```bash
# Tìm process đang dùng port 8080
netstat -ano | findstr :8080

# Kill process (thay <PID> bằng Process ID)
taskkill /PID <PID> /F

# Hoặc đổi port trong src/main/resources/application.properties
server.port=8081
```

### Lỗi: Compilation failed

```bash
# Clean và rebuild
mvn clean install

# Kiểm tra Java version
java -version
# Phải là Java 17 trở lên
```

### Lỗi: Tests failed

```bash
# Chạy test với verbose output
mvn test -X

# Chạy test cụ thể để xem lỗi chi tiết
mvn test -Dtest=UpdateBookServiceTest
```

### Lỗi: Module không tìm thấy

Đảm bảo bạn đang chạy từ thư mục gốc hoặc đã build parent project:
```bash
# Từ thư mục gốc
cd d:\TKXDPM
mvn clean install
```

## 🧪 Test Cases

### Thêm sách (AddBookService)

- **Kịch bản 1**: Thêm sách thành công (đủ thông tin, trạng thái)
  - Thêm sách giáo khoa tình trạng "mới"
  - Thêm sách giáo khoa tình trạng "cũ"
  - Thêm sách tham khảo với thuế đầy đủ

- **Kịch bản 2**: Thêm sách thất bại (validation fails)
  - Thiếu tình trạng của sách giáo khoa
  - Thiếu thuế của sách tham khảo
  - Trùng mã sách

- **Kịch bản 3**: Thêm sách thành công (thiếu mã sách, hệ thống tự tạo)
  - Tự động tạo mã sách cho sách giáo khoa
  - Tự động tạo mã sách cho sách tham khảo
  - Đảm bảo mã sách tự tạo là unique

### Tính trung bình đơn giá sách tham khảo (CalculateAveragePriceService)

- **Kịch bản 1**: Tính trung bình thành công (có sách tham khảo)
  - Trả về `Result.ok` với giá trị trung bình và số lượng sách

- **Kịch bản 2**: Tính trung bình thất bại (không có sách tham khảo)
  - Trả về `Result.fail` với thông báo "Không có sách tham khảo trong hệ thống"

## 📚 Tài liệu tham khảo

- [API_EXAMPLES.md](API_EXAMPLES.md) - Ví dụ chi tiết về API
- [TEST_GUIDE.md](TEST_GUIDE.md) - Hướng dẫn về tests
- [../MODULE_STRUCTURE.md](../MODULE_STRUCTURE.md) - Cấu trúc multi-module
- [../README.md](../README.md) - Tài liệu tổng quan về project

## 📄 License

Dự án này được tạo cho mục đích học tập.
