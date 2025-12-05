# Tài liệu Sơ đồ - Quản lý Sách

Thư mục này chứa các sơ đồ UML và kịch bản use case cho module Quản lý Sách.

## 📋 Danh sách Files

### Use Case Diagram
- **USE_CASE_DIAGRAM.puml** - Sơ đồ use case tổng quan của hệ thống

### Sequence Diagrams
- **SEQUENCE_ADD_BOOK.puml** - Sơ đồ tuần tự: Thêm sách
- **SEQUENCE_UPDATE_BOOK.puml** - Sơ đồ tuần tự: Cập nhật sách
- **SEQUENCE_DELETE_BOOK.puml** - Sơ đồ tuần tự: Xóa sách
- **SEQUENCE_GET_ALL_BOOKS.puml** - Sơ đồ tuần tự: Lấy danh sách sách
- **SEQUENCE_SEARCH_BOOKS.puml** - Sơ đồ tuần tự: Tìm kiếm sách
- **SEQUENCE_CALCULATE_TOTAL_BY_TYPE.puml** - Sơ đồ tuần tự: Tính tổng thành tiền theo loại
- **SEQUENCE_CALCULATE_AVERAGE_PRICE.puml** - Sơ đồ tuần tự: Tính trung bình đơn giá
- **SEQUENCE_GET_BOOKS_BY_PUBLISHER.puml** - Sơ đồ tuần tự: Lấy sách theo nhà xuất bản

### Kịch Bản Use Case
- **USE_CASE_SCENARIOS.md** - Tài liệu mô tả chi tiết kịch bản cho từng use case

## 🛠️ Cách sử dụng

### Với PlantUML

#### 1. Online (Khuyến nghị)
1. Truy cập [PlantUML Online Server](http://www.plantuml.com/plantuml/uml/)
2. Copy nội dung file `.puml`
3. Paste vào editor
4. Xem kết quả ngay lập tức

#### 2. VS Code
1. Cài extension "PlantUML" (by jebbs)
2. Mở file `.puml`
3. Nhấn `Alt+D` để preview
4. Hoặc nhấn `Ctrl+Shift+P` → "PlantUML: Export Current Diagram"

#### 3. IntelliJ IDEA
1. Cài plugin "PlantUML integration"
2. Mở file `.puml`
3. Preview tự động hoặc nhấn `Ctrl+Alt+U`

#### 4. Command Line
```bash
# Cài đặt PlantUML (cần Java)
# Windows: choco install plantuml
# Mac: brew install plantuml
# Linux: sudo apt-get install plantuml

# Generate PNG
plantuml docs/USE_CASE_DIAGRAM.puml

# Generate SVG
plantuml -tsvg docs/USE_CASE_DIAGRAM.puml

# Generate tất cả
plantuml docs/*.puml
```

### Với Draw.io

1. Mở [draw.io](https://app.diagrams.net/)
2. File → Import → chọn file `.puml` (draw.io hỗ trợ import PlantUML)
3. Hoặc vẽ thủ công dựa trên cấu trúc trong file PlantUML

## 📊 Mô tả các Sơ đồ

### Use Case Diagram
Mô tả tổng quan các use case của hệ thống, được nhóm thành:
- **CRUD Operations**: Thêm, Sửa, Xóa, Lấy danh sách
- **Tìm kiếm & Lọc**: Tìm kiếm, Lọc theo nhà xuất bản
- **Thống kê**: Tính tổng thành tiền, Tính trung bình đơn giá

### Sequence Diagrams
Mô tả luồng tương tác giữa các thành phần khi thực hiện từng use case:
- **Actors**: Client (người dùng)
- **Controllers**: BookController
- **Services**: Các service tương ứng
- **Repository**: BookRepository
- **Domain Entities**: SachGiaoKhoa, SachThamKhao

### Use Case Scenarios
Tài liệu chi tiết mô tả:
- Mô tả use case
- Actors
- Preconditions
- Main Flow
- Alternative Flows
- Postconditions
- Business Rules

## 🎯 Use Cases

1. **UC1: Thêm sách** - Thêm sách giáo khoa hoặc sách tham khảo
2. **UC2: Cập nhật sách** - Cập nhật thông tin sách đã tồn tại
3. **UC3: Xóa sách** - Xóa sách khỏi hệ thống
4. **UC4: Lấy danh sách sách** - Lấy tất cả sách trong hệ thống
5. **UC5: Tìm kiếm sách** - Tìm kiếm theo từ khóa
6. **UC6: Lấy sách theo nhà xuất bản** - Lọc sách giáo khoa theo nhà xuất bản
7. **UC7: Tính tổng thành tiền theo loại** - Thống kê tổng thành tiền
8. **UC8: Tính trung bình đơn giá** - Thống kê trung bình đơn giá sách tham khảo

## 📝 Lưu ý

- Tất cả các sơ đồ sử dụng PlantUML syntax
- Có thể export sang PNG, SVG, PDF
- Các sơ đồ được tạo dựa trên implementation thực tế của code
- Kịch bản use case mô tả chi tiết các luồng xử lý và business rules

## 🔄 Cập nhật

Khi có thay đổi trong code, cần cập nhật các sơ đồ tương ứng để đảm bảo tính nhất quán.

