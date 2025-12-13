# Hướng dẫn Cài đặt PostgreSQL

## 📋 Yêu cầu

- PostgreSQL 12 trở lên
- Java 17+
- Maven 3.6+

## 🔧 Cài đặt PostgreSQL

### Windows

1. Tải PostgreSQL từ: https://www.postgresql.org/download/windows/
2. Cài đặt và ghi nhớ password cho user `postgres`
3. Đảm bảo PostgreSQL service đang chạy

### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### macOS

```bash
brew install postgresql
brew services start postgresql
```

## 🗄️ Tạo Database

### Cách 1: Sử dụng psql

```bash
# Kết nối với PostgreSQL
psql -U postgres

# Tạo database
CREATE DATABASE quanlysach;

# Kiểm tra
\l

# Thoát
\q
```

### Cách 2: Sử dụng pgAdmin

1. Mở pgAdmin
2. Right-click vào "Databases" → "Create" → "Database"
3. Đặt tên: `quanlysach`
4. Click "Save"

## ⚙️ Cấu hình Ứng dụng

### 1. Cập nhật application.properties

Đảm bảo thông tin kết nối đúng:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/quanlysach
spring.datasource.username=postgres
spring.datasource.password=your_password_here
```

### 2. Kiểm tra kết nối

```bash
# Test kết nối từ command line
psql -U postgres -d quanlysach -h localhost
```

## 🚀 Chạy Ứng dụng

```bash
# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

Hibernate sẽ tự động tạo bảng `books` khi ứng dụng khởi động.

## ✅ Kiểm tra Database

### Xem dữ liệu

```sql
-- Kết nối
psql -U postgres -d quanlysach

-- Xem tất cả sách
SELECT * FROM books;

-- Xem cấu trúc bảng
\d books;

-- Đếm số lượng sách
SELECT COUNT(*) FROM books;
```

## 🔍 Troubleshooting

### Lỗi: Connection refused

**Nguyên nhân**: PostgreSQL service chưa chạy

**Giải pháp**:
```bash
# Windows
# Mở Services → Tìm PostgreSQL → Start

# Linux
sudo systemctl start postgresql

# macOS
brew services start postgresql
```

### Lỗi: Authentication failed

**Nguyên nhân**: Username/password sai

**Giải pháp**: 
- Kiểm tra lại username/password trong `application.properties`
- Đảm bảo user có quyền truy cập database

### Lỗi: Database does not exist

**Nguyên nhân**: Database chưa được tạo

**Giải pháp**:
```sql
CREATE DATABASE quanlysach;
```

### Lỗi: Permission denied

**Nguyên nhân**: User không có quyền

**Giải pháp**:
```sql
-- Cấp quyền cho user
GRANT ALL PRIVILEGES ON DATABASE quanlysach TO postgres;
```

## 📝 Lưu ý

1. **Password**: Thay đổi password mặc định sau khi cài đặt
2. **Port**: Mặc định PostgreSQL chạy trên port 5432
3. **Backup**: Nên backup database định kỳ
4. **Production**: Sử dụng connection pooling và cấu hình bảo mật phù hợp

