# Hướng dẫn Cơ sở Dữ liệu

## 📋 Tổng quan

Dự án sử dụng **PostgreSQL Database** với **JPA/Hibernate** để lưu trữ dữ liệu sách.

### Kiến trúc Database

- **JPA Entity**: `BookEntity` - Entity cho database
- **JPA Repository**: `JpaBookRepository` - Spring Data JPA repository
- **Adapter**: `JpaBookRepositoryAdapter` - Chuyển đổi giữa JPA và Domain entities
- **Domain Repository**: `BookRepository` - Interface trong domain layer

## 🗄️ Cấu trúc Database

### Bảng: `books`

| Cột | Kiểu | Mô tả |
|-----|------|-------|
| `id` | VARCHAR(100) | Mã sách (Primary Key) |
| `book_type` | VARCHAR(20) | Loại sách: SACH_GIAO_KHOA, SACH_THAM_KHAO |
| `ngay_nhap` | DATE | Ngày nhập sách |
| `don_gia` | DOUBLE | Đơn giá |
| `so_luong` | INTEGER | Số lượng |
| `nha_xuat_ban` | VARCHAR(255) | Nhà xuất bản |
| `tinh_trang` | VARCHAR(10) | Tình trạng (MOI/CU) - chỉ cho sách giáo khoa |
| `thue` | DOUBLE | Thuế - chỉ cho sách tham khảo |

### Schema SQL (PostgreSQL)

```sql
CREATE TABLE books (
    id VARCHAR(100) PRIMARY KEY,
    book_type VARCHAR(20) NOT NULL,
    ngay_nhap DATE NOT NULL,
    don_gia DOUBLE PRECISION NOT NULL,
    so_luong INTEGER NOT NULL,
    nha_xuat_ban VARCHAR(255) NOT NULL,
    tinh_trang VARCHAR(10),
    thue DOUBLE PRECISION
);
```

**Lưu ý**: Schema sẽ được tự động tạo bởi Hibernate khi `spring.jpa.hibernate.ddl-auto=update`. Bạn chỉ cần tạo database:

```sql
CREATE DATABASE quanlysach;
```

## ⚙️ Cấu hình

### application.properties

```properties
# Database Configuration (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/quanlysach
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
```

### Giải thích các thuộc tính:

- **spring.datasource.url**: URL kết nối PostgreSQL database
- **spring.datasource.username**: Username để kết nối database
- **spring.datasource.password**: Password để kết nối database
- **spring.jpa.database-platform**: Dialect cho PostgreSQL
- **spring.jpa.hibernate.ddl-auto=update**: Tự động tạo/cập nhật schema khi khởi động
- **spring.jpa.show-sql=true**: Hiển thị SQL queries trong console (development)

## 🔧 Cài đặt PostgreSQL

### Yêu cầu

1. Cài đặt PostgreSQL (https://www.postgresql.org/download/)
2. Tạo database:
   ```sql
   CREATE DATABASE quanlysach;
   ```

### Kết nối với psql

```bash
psql -U postgres -d quanlysach
```

### Query mẫu

```sql
-- Xem tất cả sách
SELECT * FROM books;

-- Xem chỉ sách giáo khoa
SELECT * FROM books WHERE book_type = 'SACH_GIAO_KHOA';

-- Xem chỉ sách tham khảo
SELECT * FROM books WHERE book_type = 'SACH_THAM_KHAO';

-- Đếm số lượng sách
SELECT COUNT(*) FROM books;

-- Tìm kiếm theo nhà xuất bản
SELECT * FROM books WHERE nha_xuat_ban LIKE '%Giáo Dục%';
```

## 🔄 Chuyển đổi sang Database khác

### H2 (In-Memory - cho development/testing)

```properties
# Thêm dependency vào pom.xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

# Cập nhật application.properties
spring.datasource.url=jdbc:h2:mem:quanlysachdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

### MySQL

```properties
# Thêm dependency vào pom.xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>

# Cập nhật application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/quanlysach?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

## 📊 Kiến trúc Mapping

### Domain Entity → JPA Entity

```
SachGiaoKhoa/SachThamKhao (Domain)
    ↓
JpaBookRepositoryAdapter.toJpaEntity()
    ↓
BookEntity (JPA)
    ↓
Database
```

### JPA Entity → Domain Entity

```
Database
    ↓
BookEntity (JPA)
    ↓
JpaBookRepositoryAdapter.toDomainEntity()
    ↓
SachGiaoKhoa/SachThamKhao (Domain)
```

## 🧪 Testing với Database

### In-Memory Database cho Tests

Tests vẫn có thể sử dụng `InMemoryBookRepository` hoặc có thể sử dụng H2 in-memory cho tests:

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
class BookServiceTest {
    // ...
}
```

## 📝 Lưu ý

1. **PostgreSQL**: Đảm bảo PostgreSQL đã được cài đặt và chạy trước khi start ứng dụng
2. **Database**: Tạo database `quanlysach` trước khi chạy ứng dụng
3. **DDL Auto**: `update` mode sẽ tự động tạo/cập nhật schema (chỉ dùng cho development)
4. **Production**: Nên set `ddl-auto=validate` và sử dụng Flyway/Liquibase cho migrations
5. **Credentials**: Thay đổi username/password trong `application.properties` theo cấu hình của bạn

## 🔍 Troubleshooting

### Lỗi: Table không tồn tại

- Kiểm tra `spring.jpa.hibernate.ddl-auto=update`
- Kiểm tra logs để xem SQL create table

### Lỗi: Connection refused

- Kiểm tra database đã chạy chưa
- Kiểm tra URL, username, password

### Lỗi: Class not found

- Kiểm tra dependencies trong `pom.xml`
- Chạy `mvn clean install`

