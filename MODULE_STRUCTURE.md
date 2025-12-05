# Cấu trúc Multi-Module Project

## Tổng quan

Dự án được tổ chức theo cấu trúc **multi-module Maven** với parent project quản lý các sub-modules.

## Cấu trúc thư mục

```
TKXDPM/
├── pom.xml                          # Parent POM
├── THXDPM_QUANLYSACH/               # Sub-module: Quản lý sách
│   ├── pom.xml                      # Module POM (kế thừa từ parent)
│   └── src/
│       ├── main/
│       └── test/
└── THXDPM_VANPHONGPHAM/             # Project độc lập (không phải module)
    └── ...
```

## Parent Project

**File**: `pom.xml` (ở thư mục gốc)

- **GroupId**: `com.thuvien`
- **ArtifactId**: `THXDPM_PARENT`
- **Packaging**: `pom` (parent project)
- **Chức năng**:
  - Quản lý version chung cho tất cả modules
  - Định nghĩa dependencies chung trong `dependencyManagement`
  - Quản lý plugins chung trong `pluginManagement`
  - Khai báo danh sách modules

## Sub-Module: THXDPM_QUANLYSACH

**File**: `THXDPM_QUANLYSACH/pom.xml`

- **Parent**: `THXDPM_PARENT`
- **Packaging**: `jar` (mặc định, có thể chạy độc lập với Spring Boot)
- **Chức năng**: Module quản lý sách cho thư viện

## Lợi ích của cấu trúc Multi-Module

1. **Quản lý version tập trung**: Tất cả modules dùng chung version dependencies
2. **Tái sử dụng**: Dễ dàng thêm modules mới với cấu hình tương tự
3. **Build tập trung**: Build tất cả modules từ một lệnh
4. **Dependency management**: Quản lý dependencies chung ở parent
5. **Mở rộng dễ dàng**: Có thể thêm các modules khác (quản lý độc giả, quản lý mượn trả, v.v.)

## Các lệnh Maven

### Build tất cả modules

```bash
# Từ thư mục gốc (có parent pom.xml)
mvn clean install
```

### Build chỉ module quản lý sách

```bash
# Từ thư mục gốc
mvn clean install -pl THXDPM_QUANLYSACH

# Hoặc từ trong thư mục module
cd THXDPM_QUANLYSACH
mvn clean install
```

### Chạy module quản lý sách

```bash
# Từ thư mục gốc
mvn spring-boot:run -pl THXDPM_QUANLYSACH

# Hoặc từ trong thư mục module
cd THXDPM_QUANLYSACH
mvn spring-boot:run
```

### Test tất cả modules

```bash
# Từ thư mục gốc
mvn test
```

### Test chỉ module quản lý sách

```bash
# Từ thư mục gốc
mvn test -pl THXDPM_QUANLYSACH

# Hoặc từ trong thư mục module
cd THXDPM_QUANLYSACH
mvn test
```

## Thêm Module mới

Để thêm một module mới vào project:

1. **Tạo thư mục module**:
   ```bash
   mkdir THXDPM_MODULE_NAME
   ```

2. **Tạo pom.xml cho module** với parent là `THXDPM_PARENT`:
   ```xml
   <parent>
       <groupId>com.thuvien</groupId>
       <artifactId>THXDPM_PARENT</artifactId>
       <version>0.0.1-SNAPSHOT</version>
       <relativePath>../pom.xml</relativePath>
   </parent>
   <artifactId>THXDPM_MODULE_NAME</artifactId>
   ```

3. **Thêm module vào parent pom.xml**:
   ```xml
   <modules>
       <module>THXDPM_QUANLYSACH</module>
       <module>THXDPM_MODULE_NAME</module>
   </modules>
   ```

## So sánh với Project độc lập

| Đặc điểm | Project độc lập | Sub-module |
|----------|----------------|------------|
| Parent POM | `spring-boot-starter-parent` | `THXDPM_PARENT` |
| Quản lý version | Tự quản lý | Tập trung ở parent |
| Build | Độc lập | Có thể build từ parent |
| Mở rộng | Khó thêm modules | Dễ thêm modules |
| Sử dụng | Phù hợp project đơn lẻ | Phù hợp hệ thống lớn |

## Lưu ý

- **THXDPM_VANPHONGPHAM** vẫn là project độc lập, không phải module
- **THXDPM_QUANLYSACH** là sub-module, có thể chạy độc lập hoặc từ parent
- Mỗi module có thể có Spring Boot application riêng
- Các modules có thể share dependencies thông qua parent

