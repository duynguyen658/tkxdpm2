# THXDPM - Hệ thống Quản lý Thư viện

Dự án multi-module quản lý các chức năng của hệ thống thư viện.

## 📋 Yêu cầu hệ thống

- **Java**: JDK 17 trở lên
- **Maven**: 3.6+ (hoặc sử dụng Maven Wrapper)
- **IDE**: IntelliJ IDEA, Eclipse, hoặc VS Code (khuyến nghị)

## 📁 Cấu trúc Project

```
TKXDPM/
├── pom.xml                    # Parent POM - Quản lý tất cả modules
├── THXDPM_QUANLYSACH/         # Sub-module: Quản lý sách
│   ├── src/
│   ├── pom.xml
│   ├── README.md
│   ├── API_EXAMPLES.md
│   └── TEST_GUIDE.md
└── THXDPM_VANPHONGPHAM/       # Project độc lập (tham khảo)
```

## 🧩 Modules

### THXDPM_QUANLYSACH (Sub-module)

Module quản lý sách cho thư viện, hỗ trợ:
- **Sách giáo khoa**: Có tình trạng (mới/cũ), tính thành tiền theo tình trạng
- **Sách tham khảo**: Có thuế, tính thành tiền = số lượng × đơn giá + thuế

**Xem chi tiết**: [THXDPM_QUANLYSACH/README.md](THXDPM_QUANLYSACH/README.md)

## 🚀 Cách sử dụng

### 1. Build tất cả modules

```bash
# Từ thư mục gốc
mvn clean install
```

### 2. Chạy Tests

```bash
# Chạy tất cả tests
mvn test

# Chạy test cho module cụ thể
mvn test -pl THXDPM_QUANLYSACH

# Chạy test cho một class cụ thể
mvn test -pl THXDPM_QUANLYSACH -Dtest=UpdateBookServiceTest
```

### 3. Build và chạy module quản lý sách

**Cách 1: Từ thư mục gốc (khuyến nghị)**

```bash
# Build
mvn clean install -pl THXDPM_QUANLYSACH

# Chạy
mvn spring-boot:run -pl THXDPM_QUANLYSACH
```

**Cách 2: Từ trong module**

```bash
cd THXDPM_QUANLYSACH
mvn clean install
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại: **http://localhost:8080**

### 4. Test API

Sau khi ứng dụng chạy, bạn có thể test API bằng:

```bash
# Lấy danh sách sách
curl -X GET http://localhost:8080/api/books

# Thêm sách giáo khoa
curl -X POST http://localhost:8080/api/books ^
  -H "Content-Type: application/json" ^
  -d "{\"bookType\":\"SACH_GIAO_KHOA\",\"maSach\":\"SGK-001\",\"ngayNhap\":\"15/01/2024\",\"donGia\":50000,\"soLuong\":10,\"nhaXuatBan\":\"NXB Giáo Dục\",\"tinhTrang\":\"mới\"}"
```

Xem thêm ví dụ API tại: [THXDPM_QUANLYSACH/API_EXAMPLES.md](THXDPM_QUANLYSACH/API_EXAMPLES.md)

## 🛠️ Công nghệ

- **Java**: 17
- **Spring Boot**: 4.0.0
- **Maven**: Multi-module project
- **JUnit**: 5 (Jupiter) cho testing

## 📚 Tài liệu

- [MODULE_STRUCTURE.md](MODULE_STRUCTURE.md) - Hướng dẫn về cấu trúc multi-module
- [THXDPM_QUANLYSACH/README.md](THXDPM_QUANLYSACH/README.md) - Tài liệu module quản lý sách
- [THXDPM_QUANLYSACH/API_EXAMPLES.md](THXDPM_QUANLYSACH/API_EXAMPLES.md) - Ví dụ API
- [THXDPM_QUANLYSACH/TEST_GUIDE.md](THXDPM_QUANLYSACH/TEST_GUIDE.md) - Hướng dẫn test

## ➕ Thêm Module mới

Xem hướng dẫn chi tiết trong [MODULE_STRUCTURE.md](MODULE_STRUCTURE.md)

## 🐛 Troubleshooting

### Lỗi: Port 8080 đã được sử dụng

```bash
# Tìm process đang dùng port 8080
netstat -ano | findstr :8080

# Kill process (thay <PID> bằng Process ID)
taskkill /PID <PID> /F

# Hoặc đổi port trong application.properties
server.port=8081
```

### Lỗi: Maven không tìm thấy

Đảm bảo Maven đã được cài đặt và thêm vào PATH, hoặc sử dụng Maven Wrapper:
```bash
# Nếu có mvnw
./mvnw clean install
```

### Lỗi: Java version không đúng

Kiểm tra version Java:
```bash
java -version
# Phải là Java 17 trở lên
```

