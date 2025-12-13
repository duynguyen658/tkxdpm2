# Troubleshooting Database Connection

## 🔴 Lỗi: Password Authentication Failed

### Lỗi:
```
FATAL: password authentication failed for user "postgres"
```

### Nguyên nhân:
Password trong `application.properties` không khớp với password của PostgreSQL trên máy bạn.

### Giải pháp:

#### Cách 1: Cập nhật password trong application.properties

1. Mở file `src/main/resources/application.properties`
2. Tìm dòng:
   ```properties
   spring.datasource.password=postgres
   ```
3. Thay đổi thành password thực tế của PostgreSQL:
   ```properties
   spring.datasource.password=your_actual_password
   ```

#### Cách 2: Sử dụng Environment Variables (Khuyến nghị)

1. Tạo file `.env` hoặc set environment variables:
   ```bash
   # Windows PowerShell
   $env:SPRING_DATASOURCE_PASSWORD="your_password"
   
   # Windows CMD
   set SPRING_DATASOURCE_PASSWORD=your_password
   
   # Linux/Mac
   export SPRING_DATASOURCE_PASSWORD=your_password
   ```

2. Cập nhật `application.properties`:
   ```properties
   spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:postgres}
   ```

#### Cách 3: Tạo file application-local.properties

1. Tạo file `src/main/resources/application-local.properties`:
   ```properties
   spring.datasource.password=your_actual_password
   ```

2. Chạy ứng dụng với profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

### Kiểm tra password PostgreSQL:

#### Windows:
```bash
# Kết nối với psql
psql -U postgres

# Hoặc nếu có password prompt
psql -U postgres -W
```

#### Linux/Mac:
```bash
sudo -u postgres psql
```

#### Nếu quên password:
1. **Windows**: 
   - Mở pgAdmin
   - Right-click vào server → Properties → Change password

2. **Linux**:
   ```bash
   sudo -u postgres psql
   ALTER USER postgres PASSWORD 'new_password';
   ```

## 🔴 Lỗi: Database does not exist

### Lỗi:
```
FATAL: database "quanlysach" does not exist
```

### Giải pháp:

1. Kết nối với PostgreSQL:
   ```bash
   psql -U postgres
   ```

2. Tạo database:
   ```sql
   CREATE DATABASE quanlysach;
   ```

3. Kiểm tra:
   ```sql
   \l
   ```

## 🔴 Lỗi: Connection refused

### Lỗi:
```
Connection refused. Check that the hostname and port are correct
```

### Giải pháp:

1. **Kiểm tra PostgreSQL service đang chạy:**
   ```bash
   # Windows
   # Mở Services → Tìm PostgreSQL → Start
   
   # Linux
   sudo systemctl status postgresql
   sudo systemctl start postgresql
   
   # Mac
   brew services list
   brew services start postgresql
   ```

2. **Kiểm tra port:**
   - Mặc định PostgreSQL chạy trên port 5432
   - Nếu khác, cập nhật trong `application.properties`:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:YOUR_PORT/quanlysach
     ```

## 🔴 Lỗi: Permission denied for schema public

### Lỗi:
```
ERROR: permission denied for schema public
Position: 20
```

### Nguyên nhân:
User không có quyền tạo bảng trong schema `public` của PostgreSQL.

### Giải pháp:

#### Cách 1: Sử dụng script PowerShell (Khuyến nghị)

```powershell
cd THXDPM_QUANLYSACH
.\scripts\fix-postgresql-permissions.ps1
```

#### Cách 2: Chạy SQL thủ công

1. Kết nối với PostgreSQL:
   ```bash
   psql -U postgres
   ```

2. Chạy các lệnh sau:
   ```sql
   \c quanlysach
   
   -- Cấp quyền cho schema public
   GRANT ALL ON SCHEMA public TO postgres;
   GRANT ALL ON SCHEMA public TO public;
   ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO postgres;
   ALTER DATABASE quanlysach OWNER TO postgres;
   ```

3. Tạo bảng thủ công (nếu cần):
   ```sql
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
   ```

#### Cách 3: Sửa file pg_hba.conf (Nếu vẫn không được)

1. Tìm file `pg_hba.conf` (thường ở: `C:\Program Files\PostgreSQL\13\data\pg_hba.conf`)
2. Tìm dòng:
   ```
   host    all             all             127.0.0.1/32            scram-sha-256
   ```
3. Đổi thành:
   ```
   host    all             all             127.0.0.1/32            trust
   ```
4. **Lưu ý**: Chỉ dùng cho development! Sau đó restart PostgreSQL service.

## 🔴 Lỗi: Relation "books" does not exist

### Lỗi:
```
ERROR: relation "books" does not exist
```

### Nguyên nhân:
Bảng `books` chưa được tạo do lỗi permission ở trên.

### Giải pháp:

1. **Fix permission trước** (xem phần trên)
2. **Tạo bảng thủ công** (nếu cần):
   ```sql
   \c quanlysach
   \i scripts/create-books-table.sql
   ```
3. **Hoặc restart ứng dụng** sau khi fix permission - Hibernate sẽ tự tạo bảng.

## 🔴 Lỗi: Permission denied for database

### Lỗi:
```
permission denied for database quanlysach
```

### Giải pháp:

1. Cấp quyền cho user:
   ```sql
   GRANT ALL PRIVILEGES ON DATABASE quanlysach TO postgres;
   ```

2. Hoặc tạo user mới:
   ```sql
   CREATE USER quanlysach_user WITH PASSWORD 'password';
   GRANT ALL PRIVILEGES ON DATABASE quanlysach TO quanlysach_user;
   ```

## ✅ Kiểm tra kết nối

### Test kết nối từ command line:

```bash
psql -U postgres -d quanlysach -h localhost
```

Nếu kết nối thành công, bạn sẽ thấy prompt:
```
quanlysach=#
```

### Test từ ứng dụng:

1. Chạy ứng dụng:
   ```bash
   mvn spring-boot:run
   ```

2. Kiểm tra logs - nếu thấy:
   ```
   HikariPool-1 - Start completed
   ```
   → Kết nối thành công!

## 📝 Checklist trước khi chạy

- [ ] PostgreSQL đã được cài đặt
- [ ] PostgreSQL service đang chạy
- [ ] Database `quanlysach` đã được tạo
- [ ] Username và password trong `application.properties` đúng
- [ ] Port trong `application.properties` đúng (mặc định 5432)
- [ ] User có quyền truy cập database

## 🔧 Cấu hình nhanh

### Tạo database và user mới:

```sql
-- Kết nối với PostgreSQL
psql -U postgres

-- Tạo database
CREATE DATABASE quanlysach;

-- Tạo user (tùy chọn)
CREATE USER quanlysach_user WITH PASSWORD 'your_password';

-- Cấp quyền
GRANT ALL PRIVILEGES ON DATABASE quanlysach TO quanlysach_user;

-- Kết nối với database mới
\c quanlysach

-- Cấp quyền trên schema
GRANT ALL ON SCHEMA public TO quanlysach_user;
```

### Cập nhật application.properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/quanlysach
spring.datasource.username=quanlysach_user
spring.datasource.password=your_password
```

