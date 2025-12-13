# Tài liệu: GetAllBooksService - Lấy Danh Sách Tất Cả Sách

## 📋 Mục lục

- [Tổng quan](#tổng-quan)
- [Kiến trúc và các thành phần](#kiến-trúc-và-các-thành-phần)
- [Luồng hoạt động](#luồng-hoạt-động)
- [Mối quan hệ với Use Cases](#mối-quan-hệ-với-use-cases)
- [Kịch bản hoạt động](#kịch-bản-hoạt-động)
- [Xử lý lỗi](#xử-lý-lỗi)
- [Ví dụ sử dụng](#ví-dụ-sử-dụng)

---

## 🎯 Tổng quan

**GetAllBooksService** là một service trong Application Layer, có nhiệm vụ lấy danh sách tất cả sách trong hệ thống và chuyển đổi chúng thành định dạng response phù hợp cho client.

### Chức năng chính:
- ✅ Lấy tất cả sách từ repository (bao gồm cả sách giáo khoa và sách tham khảo)
- ✅ Chuyển đổi từ Domain Entity (`Book`) sang DTO (`BookResponse`)
- ✅ Tính toán thành tiền cho từng sách
- ✅ Xử lý lỗi và trả về kết quả dưới dạng `Result<T>`

### Đặc điểm:
- **Không có tham số đầu vào**: Service này không yêu cầu bất kỳ tham số nào
- **Trả về tất cả sách**: Không có filter hay pagination
- **Tự động tính thành tiền**: Mỗi sách sẽ được tính thành tiền theo quy tắc của loại sách

---

## 🏗️ Kiến trúc và các thành phần

### 1. Interface: GetAllBooksUseCase

```java
public interface GetAllBooksUseCase {
    Result<List<BookResponse>> execute();
}
```

**Vai trò**: Định nghĩa contract cho use case lấy danh sách sách
- **Input**: Không có
- **Output**: `Result<List<BookResponse>>` - Kết quả chứa danh sách sách hoặc thông báo lỗi

### 2. Implementation: GetAllBooksService

```java
public class GetAllBooksService implements GetAllBooksUseCase {
    private final BookRepository bookRepository;
    
    @Override
    public Result<List<BookResponse>> execute() {
        // Logic xử lý
    }
}
```

**Vai trò**: Triển khai logic nghiệp vụ để lấy danh sách sách

**Dependencies**:
- `BookRepository`: Port để truy cập dữ liệu sách

### 3. BookRepository (Port)

```java
public interface BookRepository {
    List<Book> findAll();
    // ... các methods khác
}
```

**Vai trò**: Interface định nghĩa cách truy cập dữ liệu sách
- `findAll()`: Lấy tất cả sách từ storage

### 4. BookResponse (DTO)

```java
public record BookResponse(
    String maSach,
    String loaiSach,
    String ngayNhap,
    double donGia,
    int soLuong,
    String nhaXuatBan,
    String tinhTrang,  // null nếu là sách tham khảo
    Double thue,       // null nếu là sách giáo khoa
    double thanhTien
) {
    public static BookResponse from(Book book) {
        // Chuyển đổi từ Book entity sang BookResponse
    }
}
```

**Vai trò**: Data Transfer Object để truyền dữ liệu từ service đến controller

### 5. BookController (REST Endpoint)

```java
@GetMapping
public ResponseEntity<?> getAllBooks() {
    final Result<List<BookResponse>> result = getAllBooksUseCase.execute();
    // Xử lý response
}
```

**Vai trò**: Expose REST API endpoint `GET /api/books`

---

## 🔄 Luồng hoạt động

### Sequence Diagram

```
Client → BookController → GetAllBooksService → BookRepository
         ↓                    ↓                    ↓
    GET /api/books    execute()           findAll()
         ↓                    ↓                    ↓
    Response ← Result.ok ← List<BookResponse> ← List<Book>
```

### Chi tiết từng bước:

#### **Bước 1: Client gửi request**
```
GET http://localhost:8080/api/books
```

#### **Bước 2: BookController nhận request**
- Controller nhận HTTP GET request tại endpoint `/api/books`
- Gọi `getAllBooksUseCase.execute()`

#### **Bước 3: GetAllBooksService xử lý**
```java
public Result<List<BookResponse>> execute() {
    try {
        // 3.1: Lấy tất cả sách từ repository
        final List<Book> books = bookRepository.findAll();
        
        // 3.2: Chuyển đổi từ Book entity sang BookResponse DTO
        final List<BookResponse> responses = books.stream()
            .map(BookResponse::from)
            .collect(Collectors.toList());
        
        // 3.3: Trả về kết quả thành công
        return Result.ok(responses);
    } catch (Exception ex) {
        // 3.4: Xử lý lỗi nếu có
        return Result.fail("Lỗi khi lấy danh sách sách: " + ex.getMessage());
    }
}
```

**Chi tiết bước 3.2 - Chuyển đổi BookResponse**:
- Với mỗi `Book` entity:
  - Nếu là `SachGiaoKhoa`:
    - Lấy `tinhTrang` (mới/cũ)
    - `thue = null`
    - Tính `thanhTien` = `soLuong × donGia` (nếu mới) hoặc `soLuong × donGia × 0.5` (nếu cũ)
  - Nếu là `SachThamKhao`:
    - `tinhTrang = null`
    - Lấy `thue`
    - Tính `thanhTien` = `soLuong × donGia + thue`

#### **Bước 4: BookController xử lý response**
```java
if (result.isSuccess()) {
    final List<BookResponse> books = result.payload().orElseThrow();
    return ResponseEntity.ok(Map.of(
        "success", true,
        "total", books.size(),
        "books", books
    ));
} else {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
        "success", false,
        "message", result.errorMessage().orElse("Không thể lấy danh sách sách")
    ));
}
```

#### **Bước 5: Client nhận response**
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

## 🔗 Mối quan hệ với Use Cases

### 1. Quan hệ với các Use Case khác

GetAllBooksService là một use case độc lập nhưng có liên quan đến các use case khác:

#### **AddBookUseCase** (Thêm sách)
- **Mối quan hệ**: Sau khi thêm sách thành công, client có thể gọi GetAllBooksUseCase để xem sách mới được thêm vào danh sách
- **Ví dụ**: Frontend sau khi thêm sách → tự động refresh danh sách bằng cách gọi `GET /api/books`

#### **DeleteBookUseCase** (Xóa sách)
- **Mối quan hệ**: Sau khi xóa sách, danh sách trả về từ GetAllBooksUseCase sẽ không còn sách đã xóa
- **Ví dụ**: Frontend sau khi xóa sách → refresh danh sách để cập nhật UI

#### **UpdateBookUseCase** (Cập nhật sách)
- **Mối quan hệ**: Sau khi cập nhật sách, danh sách trả về từ GetAllBooksUseCase sẽ hiển thị thông tin đã được cập nhật
- **Ví dụ**: Frontend sau khi sửa sách → refresh danh sách để hiển thị thông tin mới

#### **SearchBooksUseCase** (Tìm kiếm sách)
- **Mối quan hệ**: GetAllBooksUseCase lấy TẤT CẢ sách, SearchBooksUseCase lấy sách theo điều kiện
- **Khác biệt**: 
  - `GetAllBooksUseCase`: Không có filter, trả về tất cả
  - `SearchBooksUseCase`: Có filter theo keyword, trả về sách phù hợp

#### **GetBooksByPublisherUseCase** (Lấy sách theo nhà xuất bản)
- **Mối quan hệ**: GetAllBooksUseCase lấy TẤT CẢ sách, GetBooksByPublisherUseCase lấy sách giáo khoa theo nhà xuất bản
- **Khác biệt**:
  - `GetAllBooksUseCase`: Trả về tất cả sách (SGK + STK)
  - `GetBooksByPublisherUseCase`: Chỉ trả về sách giáo khoa của một nhà xuất bản

#### **CalculateTotalByTypeUseCase** (Tính tổng thành tiền theo loại)
- **Mối quan hệ**: Cả hai đều sử dụng `BookRepository.findAll()`
- **Khác biệt**:
  - `GetAllBooksUseCase`: Trả về danh sách chi tiết từng sách
  - `CalculateTotalByTypeUseCase`: Trả về tổng hợp thống kê (tổng thành tiền)

#### **CalculateAveragePriceUseCase** (Tính trung bình đơn giá)
- **Mối quan hệ**: Cả hai đều sử dụng `BookRepository.findAll()`
- **Khác biệt**:
  - `GetAllBooksUseCase`: Trả về danh sách chi tiết
  - `CalculateAveragePriceUseCase`: Trả về thống kê (trung bình đơn giá sách tham khảo)

### 2. Dependency Injection

GetAllBooksService được inject vào BookController thông qua Spring:

```java
@Configuration
public class ApplicationConfig {
    @Bean
    public GetAllBooksUseCase getAllBooksUseCase(final BookRepository bookRepository) {
        return new GetAllBooksService(bookRepository);
    }
}
```

```java
@RestController
public class BookController {
    private final GetAllBooksUseCase getAllBooksUseCase;
    
    public BookController(final GetAllBooksUseCase getAllBooksUseCase) {
        this.getAllBooksUseCase = getAllBooksUseCase;
    }
}
```

---

## 📝 Kịch bản hoạt động

### Kịch bản 1: Lấy danh sách khi không có sách

**Input**: Không có
**Process**:
1. `bookRepository.findAll()` trả về `List<Book>` rỗng
2. Stream mapping trả về `List<BookResponse>` rỗng
3. `Result.ok(emptyList)` được trả về

**Output**:
```json
{
  "success": true,
  "total": 0,
  "books": []
}
```

**Test Case**: `kichBan1_shouldReturnEmptyList_whenNoBooks()`

---

### Kịch bản 2: Lấy danh sách khi có sách

**Input**: Không có
**Process**:
1. `bookRepository.findAll()` trả về danh sách sách
2. Mỗi sách được chuyển đổi sang `BookResponse`:
   - Sách giáo khoa: có `tinhTrang`, `thue = null`
   - Sách tham khảo: có `thue`, `tinhTrang = null`
3. Tính thành tiền cho từng sách
4. `Result.ok(bookResponses)` được trả về

**Output**:
```json
{
  "success": true,
  "total": 2,
  "books": [
    {
      "maSach": "SGK-001",
      "loaiSach": "Sách giáo khoa",
      "tinhTrang": "mới",
      "thue": null,
      "thanhTien": 500000.0
    },
    {
      "maSach": "STK-001",
      "loaiSach": "Sách tham khảo",
      "tinhTrang": null,
      "thue": 5000.0,
      "thanhTien": 405000.0
    }
  ]
}
```

**Test Case**: `kichBan2_shouldReturnAllBooks_whenBooksExist()`

---

### Kịch bản 3: Kiểm tra tính toán thành tiền đúng

**Input**: Không có
**Process**:
1. Lấy danh sách sách
2. Với mỗi sách:
   - **Sách giáo khoa mới**: `thanhTien = soLuong × donGia`
   - **Sách giáo khoa cũ**: `thanhTien = soLuong × donGia × 0.5`
   - **Sách tham khảo**: `thanhTien = soLuong × donGia + thue`

**Ví dụ**:
- SGK cũ: 10 × 50000 × 0.5 = 250000
- STK: 5 × 80000 + 5000 = 405000

**Test Case**: `kichBan3_shouldReturnBooksWithCorrectCalculations()`

---

### Kịch bản 4: Xử lý lỗi

**Input**: Không có
**Process**:
1. `bookRepository.findAll()` ném exception
2. Catch exception và trả về `Result.fail()`

**Output**:
```json
{
  "success": false,
  "message": "Lỗi khi lấy danh sách sách: [chi tiết lỗi]"
}
```

**HTTP Status**: 500 Internal Server Error

---

## ⚠️ Xử lý lỗi

### Các trường hợp lỗi:

1. **Lỗi từ Repository**
   - Repository không thể truy cập dữ liệu
   - Database connection error
   - **Xử lý**: Catch exception → `Result.fail("Lỗi khi lấy danh sách sách: " + ex.getMessage())`

2. **Lỗi khi chuyển đổi BookResponse**
   - Book entity không phải SachGiaoKhoa hoặc SachThamKhao
   - **Xử lý**: `BookResponse.from()` sẽ throw `IllegalArgumentException` → được catch và trả về lỗi

### Error Response Format:

```json
{
  "success": false,
  "message": "Lỗi khi lấy danh sách sách: [chi tiết]"
}
```

---

## 💡 Ví dụ sử dụng

### 1. Sử dụng trong Controller

```java
@GetMapping
public ResponseEntity<?> getAllBooks() {
    final Result<List<BookResponse>> result = getAllBooksUseCase.execute();
    
    if (result.isSuccess()) {
        final List<BookResponse> books = result.payload().orElseThrow();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "total", books.size(),
            "books", books
        ));
    }
    
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of(
            "success", false,
            "message", result.errorMessage().orElse("Không thể lấy danh sách sách")
        ));
}
```

### 2. Sử dụng trong Frontend (JavaScript)

```javascript
async function loadBooks() {
    try {
        const response = await fetch('/api/books');
        const data = await response.json();
        
        if (data.success) {
            displayBooks(data.books);
            updateTotalCount(data.total);
        } else {
            showError(data.message);
        }
    } catch (error) {
        showError('Lỗi kết nối đến server');
    }
}
```

### 3. Sử dụng với cURL

```bash
curl -X GET http://localhost:8080/api/books
```

### 4. Sử dụng trong Test

```java
@Test
void kichBan2_shouldReturnAllBooks_whenBooksExist() {
    // Setup: Thêm sách vào repository
    bookRepository.save(sachGiaoKhoa);
    bookRepository.save(sachThamKhao);
    
    // Execute
    final Result<List<BookResponse>> result = service.execute();
    
    // Verify
    assertTrue(result.isSuccess());
    final List<BookResponse> books = result.payload().orElseThrow();
    assertEquals(2, books.size());
}
```

---

## 📊 Tóm tắt

| Thuộc tính | Giá trị |
|-----------|---------|
| **Use Case** | GetAllBooksUseCase |
| **Implementation** | GetAllBooksService |
| **Input** | Không có |
| **Output** | `Result<List<BookResponse>>` |
| **Endpoint** | `GET /api/books` |
| **HTTP Method** | GET |
| **HTTP Status Success** | 200 OK |
| **HTTP Status Error** | 500 Internal Server Error |
| **Dependencies** | BookRepository |
| **Related Use Cases** | AddBook, DeleteBook, UpdateBook, SearchBooks, GetBooksByPublisher, CalculateTotalByType, CalculateAveragePrice |

---

## 🔍 So sánh với các Use Case tương tự

| Use Case | Input | Output | Mô tả |
|----------|-------|--------|-------|
| **GetAllBooksUseCase** | Không có | Tất cả sách | Lấy tất cả sách không filter |
| **SearchBooksUseCase** | `keyword` | Sách phù hợp | Tìm kiếm sách theo keyword |
| **GetBooksByPublisherUseCase** | `publisher` | Sách giáo khoa của NXB | Lọc sách giáo khoa theo nhà xuất bản |
| **CalculateTotalByTypeUseCase** | Không có | Thống kê tổng thành tiền | Tính tổng thành tiền theo loại |
| **CalculateAveragePriceUseCase** | Không có | Thống kê trung bình đơn giá | Tính trung bình đơn giá sách tham khảo |

---

## 📚 Tài liệu liên quan

- [Sequence Diagram](SEQUENCE_GET_ALL_BOOKS.puml)
- [Class Diagram](CLASS_DIAGRAM_GET_ALL_BOOKS.puml)
- [Test Cases](../../src/test/java/com/thuvien/quanlysach/application/service/GetAllBooksServiceTest.java)
- [API Examples](../API_EXAMPLES.md)

