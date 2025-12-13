# 🔧 Sửa Lỗi Permission PostgreSQL - Hướng Dẫn Nhanh

## ❌ Lỗi hiện tại:
```
ERROR: permission denied for schema public
ERROR: relation "books" does not exist
```

## ✅ Cách sửa nhanh (Chọn 1 trong 3 cách):

### Cách 1: Sử dụng Script PowerShell (Dễ nhất) ⭐

```powershell
cd D:\TKXDPM\THXDPM_QUANLYSACH
.\scripts\fix-postgresql-permissions.ps1
```

Script sẽ tự động:
- Cấp quyền cho schema public
- Tạo bảng books nếu chưa có
- Kiểm tra kết quả

---

### Cách 2: Chạy SQL thủ công

1. **Mở psql:**
   ```powershell
   psql -U postgres
   ```

2. **Chạy các lệnh sau:**
   ```sql
   -- Kết nối với database
   \c quanlysach
   
   -- Cấp quyền cho schema public
   GRANT ALL ON SCHEMA public TO postgres;
   GRANT ALL ON SCHEMA public TO iot_user;
   GRANT ALL ON SCHEMA public TO public;
   
   -- Cấp quyền tạo bảng
   GRANT CREATE ON SCHEMA public TO iot_user;
   ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO iot_user;
   
   -- Tạo bảng books (nếu chưa có)
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
   
   -- Cấp quyền cho bảng
   GRANT ALL PRIVILEGES ON TABLE books TO iot_user;
   
   -- Kiểm tra
   \d books
   ```

3. **Thoát:**
   ```sql
   \q
   ```

---

### Cách 3: Sử dụng file SQL

1. **Chạy file SQL:**
   ```powershell
   psql -U postgres -d quanlysach -f scripts\fix-postgresql-permissions.sql
   psql -U postgres -d quanlysach -f scripts\create-books-table.sql
   ```

---

## 🔍 Kiểm tra sau khi fix

### 1. Kiểm tra quyền:
```sql
\c quanlysach
\dn+ public
```

### 2. Kiểm tra bảng:
```sql
\dt books
-- Hoặc
SELECT * FROM information_schema.tables WHERE table_name = 'books';
```

### 3. Test tạo bảng:
```sql
CREATE TABLE test_table (id INT);
DROP TABLE test_table;
```

Nếu không lỗi → Quyền đã OK!

---

## 🚀 Chạy lại ứng dụng

Sau khi fix permission, chạy lại:

```powershell
mvn spring-boot:run
```

Ứng dụng sẽ tự động tạo bảng (nếu chưa có) hoặc sử dụng bảng đã tạo.

---

## ⚠️ Lưu ý

- **User hiện tại**: `iot_user` (theo application.properties)
- **Database**: `quanlysach`
- Nếu dùng user khác, thay `iot_user` trong các lệnh SQL

---

## 🔄 Nếu vẫn không được

### Option 1: Đổi sang user postgres

Cập nhật `application.properties`:
```properties
spring.datasource.username=postgres
spring.datasource.password=your_postgres_password
```

### Option 2: Tạo user mới với đầy đủ quyền

```sql
-- Kết nối với postgres user
psql -U postgres

-- Tạo user mới
CREATE USER quanlysach_user WITH PASSWORD 'your_password';
CREATE DATABASE quanlysach OWNER quanlysach_user;

-- Cấp quyền
GRANT ALL PRIVILEGES ON DATABASE quanlysach TO quanlysach_user;
\c quanlysach
GRANT ALL ON SCHEMA public TO quanlysach_user;
```

Cập nhật `application.properties` với user mới.

