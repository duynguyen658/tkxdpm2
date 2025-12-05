# Hướng dẫn Test - Quản lý Sách

## Tổng quan

Dự án có đầy đủ các file test theo pattern tương tự dự án THXDPM_VANPHONGPHAM, bao gồm:

1. **Domain Entity Tests** - Test logic nghiệp vụ ở domain layer
2. **Application Service Tests** - Test các use cases
3. **Application Tests** - Test Spring Boot context

## Cấu trúc Test

```
src/test/java/com/thuvien/quanlysach/
├── domain/entity/book/
│   ├── SachGiaoKhoaTest.java
│   └── SachThamKhaoTest.java
├── application/service/
│   ├── AddBookServiceTest.java
│   ├── UpdateBookServiceTest.java
│   ├── DeleteBookServiceTest.java
│   ├── GetAllBooksServiceTest.java
│   ├── SearchBooksServiceTest.java (Mới)
│   ├── CalculateTotalByTypeServiceTest.java (Mới)
│   ├── CalculateAveragePriceServiceTest.java (Mới)
│   └── GetBooksByPublisherServiceTest.java (Mới)
├── QuanLySachApplicationTests.java
├── TestcontainersConfiguration.java
└── TestQuanLySachApplication.java
```

## Domain Entity Tests

### SachGiaoKhoaTest

Test các kịch bản:
1. **kichBan1**: Tính thành tiền khi tình trạng là mới (số lượng × đơn giá)
2. **kichBan2**: Tính thành tiền khi tình trạng là cũ (số lượng × đơn giá × 50%)
3. **kichBan3**: Cập nhật thông tin sách giáo khoa
4. **kichBan4**: Tạo sách với tất cả thông tin hợp lệ

### SachThamKhaoTest

Test các kịch bản:
1. **kichBan1**: Tính thành tiền với thuế (số lượng × đơn giá + thuế)
2. **kichBan2**: Tính thành tiền khi thuế = 0
3. **kichBan3**: Cập nhật thông tin sách tham khảo
4. **kichBan4**: Tạo sách với tất cả thông tin hợp lệ

## Application Service Tests

### AddBookServiceTest

Test các kịch bản:
1. **kichBan1**: Thêm sách giáo khoa tình trạng mới
2. **kichBan2**: Thêm sách giáo khoa tình trạng cũ
3. **kichBan3**: Thêm sách tham khảo
4. **kichBan4**: Thêm sách giáo khoa thiếu tình trạng (lỗi)
5. **kichBan5**: Thêm sách tham khảo thiếu thuế (lỗi)
6. **kichBan6**: Thêm sách với mã sách trùng (lỗi)
7. **kichBan7**: Tự động tạo mã sách nếu không cung cấp

### UpdateBookServiceTest

Test các kịch bản:
1. **kichBan1**: Cập nhật sách giáo khoa
2. **kichBan2**: Cập nhật sách tham khảo
3. **kichBan3**: Cập nhật sách không tồn tại (lỗi)
4. **kichBan4**: Cập nhật sách giáo khoa thiếu tình trạng (lỗi)
5. **kichBan5**: Cập nhật sách tham khảo thiếu thuế (lỗi)

### DeleteBookServiceTest

Test các kịch bản:
1. **kichBan1**: Xóa sách tồn tại
2. **kichBan2**: Xóa sách không tồn tại (lỗi)
3. **kichBan3**: Xóa chỉ sách được chỉ định, các sách khác vẫn còn

### GetAllBooksServiceTest

Test các kịch bản:
1. **kichBan1**: Lấy danh sách khi chưa có sách nào (trả về danh sách rỗng)
2. **kichBan2**: Lấy danh sách tất cả sách (cả sách giáo khoa và sách tham khảo)
3. **kichBan3**: Kiểm tra tính toán thành tiền đúng cho từng loại sách

### SearchBooksServiceTest (Mới)

Test các kịch bản:
1. **kichBan1**: Tìm kiếm với từ khóa rỗng (lỗi)
2. **kichBan2**: Tìm kiếm với từ khóa null (lỗi)
3. **kichBan3**: Tìm kiếm theo mã sách
4. **kichBan4**: Tìm kiếm theo nhà xuất bản
5. **kichBan5**: Tìm kiếm theo loại sách
6. **kichBan6**: Tìm kiếm không phân biệt hoa thường
7. **kichBan7**: Không tìm thấy kết quả (trả về danh sách rỗng)

### CalculateTotalByTypeServiceTest (Mới)

Test các kịch bản:
1. **kichBan1**: Tính tổng khi chưa có sách nào (trả về 0)
2. **kichBan2**: Tính tổng thành tiền cho sách giáo khoa (mới và cũ)
3. **kichBan3**: Tính tổng thành tiền cho sách tham khảo
4. **kichBan4**: Tính tổng thành tiền cho cả 2 loại sách
5. **kichBan5**: Tính tổng với nhiều sách (kiểm tra tính toán chính xác)

### CalculateAveragePriceServiceTest (Mới)

Test các kịch bản:
1. **kichBan1**: Không có sách tham khảo (trả về 0)
2. **kichBan2**: Tính trung bình với 1 sách tham khảo
3. **kichBan3**: Tính trung bình với nhiều sách tham khảo
4. **kichBan4**: Chỉ tính sách tham khảo, bỏ qua sách giáo khoa
5. **kichBan5**: Tính trung bình có kết quả thập phân

### GetBooksByPublisherServiceTest (Mới)

Test các kịch bản:
1. **kichBan1**: Tên nhà xuất bản rỗng (lỗi)
2. **kichBan2**: Tên nhà xuất bản null (lỗi)
3. **kichBan3**: Chỉ trả về sách giáo khoa, không trả về sách tham khảo
4. **kichBan4**: Lấy sách giáo khoa theo nhà xuất bản chính xác
5. **kichBan5**: Tìm kiếm không phân biệt hoa thường
6. **kichBan6**: Không tìm thấy sách giáo khoa của nhà xuất bản (trả về danh sách rỗng)
7. **kichBan7**: Chỉ có sách tham khảo, không có sách giáo khoa (trả về danh sách rỗng)
8. **kichBan8**: Tìm kiếm theo một phần tên nhà xuất bản

## Chạy Tests

### Chạy tất cả tests
```bash
mvn test
```

### Chạy test cho một class cụ thể
```bash
mvn test -Dtest=SachGiaoKhoaTest
```

### Chạy test cho một method cụ thể
```bash
mvn test -Dtest=SachGiaoKhoaTest#kichBan1_shouldCalculateTotal_whenStatusIsMoi
```

### Chạy test với coverage
```bash
mvn test jacoco:report
```

## Pattern Test

Các test tuân theo pattern:
- **Tên test**: `kichBan{N}_should{ExpectedBehavior}_when{Condition}`
- **Cấu trúc**: Arrange → Act → Assert
- **Sử dụng**: JUnit 5 (Jupiter)
- **Mock/Stub**: In-memory repository implementations trong test classes

## Ví dụ Test Case

```java
@Test
void kichBan1_shouldCalculateTotal_whenStatusIsMoi() {
    // Arrange: Chuẩn bị dữ liệu
    final SachGiaoKhoa book = SachGiaoKhoa.create(
            bookId, importDate, unitPrice, quantity, publisher, BookStatus.MOI);

    // Act: Thực hiện hành động
    final double total = book.calculateTotal().value();

    // Assert: Kiểm tra kết quả
    final double expectedTotal = 10 * 50000; // 500000
    assertEquals(expectedTotal, total, 0.01);
}
```

## Coverage

Các test cover:
- ✅ Tất cả các use cases (Thêm, Sửa, Xóa, Lấy danh sách)
- ✅ Tìm kiếm sách (theo mã sách, nhà xuất bản, loại sách)
- ✅ Tính tổng thành tiền theo từng loại
- ✅ Tính trung bình cộng đơn giá sách tham khảo
- ✅ Lấy sách giáo khoa theo nhà xuất bản
- ✅ Tính toán thành tiền cho cả 2 loại sách
- ✅ Validation và error handling
- ✅ Business rules (tình trạng mới/cũ, thuế)
- ✅ Edge cases (sách không tồn tại, mã trùng, thiếu thông tin)
- ✅ Case-insensitive search
- ✅ Empty/null input handling

