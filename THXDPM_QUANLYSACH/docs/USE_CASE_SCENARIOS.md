# Kịch Bản Use Case - Quản lý Sách

## 1. UC1: Thêm Sách

### Mô tả
Người dùng thêm một cuốn sách mới vào hệ thống. Hệ thống hỗ trợ 2 loại sách: Sách giáo khoa và Sách tham khảo.

### Actors
- Người dùng (User)

### Preconditions
- Hệ thống đang hoạt động
- Người dùng có quyền thêm sách

### Main Flow
1. Người dùng gửi request POST `/api/books` với thông tin sách
2. Hệ thống validate dữ liệu đầu vào
3. Hệ thống kiểm tra mã sách đã tồn tại chưa
4. Nếu mã sách chưa tồn tại:
   - Hệ thống tạo đối tượng sách tương ứng (SachGiaoKhoa hoặc SachThamKhao)
   - Hệ thống lưu sách vào repository
   - Hệ thống trả về response thành công với thông tin sách đã tạo
5. Nếu mã sách đã tồn tại:
   - Hệ thống trả về lỗi "Mã sách đã tồn tại"

### Alternative Flows

#### A1: Mã sách không được cung cấp
- Hệ thống tự động tạo UUID làm mã sách

#### A2: Dữ liệu không hợp lệ
- Hệ thống trả về lỗi validation tương ứng:
  - Sách giáo khoa thiếu tình trạng → "Sách giáo khoa cần có tình trạng"
  - Sách tham khảo thiếu thuế → "Sách tham khảo cần có thuế"
  - Đơn giá < 0 → "Đơn giá không được âm"
  - Số lượng ≤ 0 → "Số lượng phải lớn hơn 0"

### Postconditions
- Sách mới được thêm vào hệ thống (nếu thành công)
- Hoặc hệ thống trả về lỗi (nếu thất bại)

---

## 2. UC2: Cập nhật Sách

### Mô tả
Người dùng cập nhật thông tin của một cuốn sách đã tồn tại trong hệ thống.

### Actors
- Người dùng (User)

### Preconditions
- Sách cần cập nhật đã tồn tại trong hệ thống
- Người dùng biết mã sách (bookId)

### Main Flow
1. Người dùng gửi request PUT `/api/books/{bookId}` với thông tin cập nhật
2. Hệ thống tìm sách theo bookId
3. Nếu tìm thấy:
   - Hệ thống validate dữ liệu mới
   - Hệ thống cập nhật thông tin sách
   - Hệ thống lưu sách đã cập nhật
   - Hệ thống trả về response thành công
4. Nếu không tìm thấy:
   - Hệ thống trả về lỗi "Không tìm thấy sách với mã: {bookId}"

### Alternative Flows

#### A1: Dữ liệu không hợp lệ
- Tương tự như UC1 A2

#### A2: Cập nhật sai loại sách
- Hệ thống giữ nguyên loại sách ban đầu
- Chỉ cập nhật các thông tin khác

### Postconditions
- Thông tin sách được cập nhật (nếu thành công)
- Hoặc hệ thống trả về lỗi (nếu thất bại)

---

## 3. UC3: Xóa Sách

### Mô tả
Người dùng xóa một cuốn sách khỏi hệ thống.

### Actors
- Người dùng (User)

### Preconditions
- Sách cần xóa đã tồn tại trong hệ thống
- Người dùng biết mã sách (bookId)

### Main Flow
1. Người dùng gửi request DELETE `/api/books/{bookId}`
2. Hệ thống tìm sách theo bookId
3. Nếu tìm thấy:
   - Hệ thống xóa sách khỏi repository
   - Hệ thống trả về response thành công
4. Nếu không tìm thấy:
   - Hệ thống trả về lỗi "Không tìm thấy sách với mã: {bookId}"

### Postconditions
- Sách được xóa khỏi hệ thống (nếu thành công)
- Hoặc hệ thống trả về lỗi (nếu thất bại)

---

## 4. UC4: Lấy Danh Sách Tất Cả Sách

### Mô tả
Người dùng lấy danh sách tất cả các sách trong hệ thống.

### Actors
- Người dùng (User)

### Preconditions
- Hệ thống đang hoạt động

### Main Flow
1. Người dùng gửi request GET `/api/books`
2. Hệ thống lấy tất cả sách từ repository
3. Hệ thống chuyển đổi sang BookResponse
4. Hệ thống trả về danh sách sách

### Alternative Flows

#### A1: Không có sách nào
- Hệ thống trả về danh sách rỗng `[]`

### Postconditions
- Người dùng nhận được danh sách tất cả sách

---

## 5. UC5: Tìm kiếm Sách

### Mô tả
Người dùng tìm kiếm sách theo từ khóa (mã sách, nhà xuất bản, hoặc loại sách).

### Actors
- Người dùng (User)

### Preconditions
- Hệ thống đang hoạt động

### Main Flow
1. Người dùng gửi request GET `/api/books/search?keyword={keyword}`
2. Hệ thống validate từ khóa
3. Nếu từ khóa hợp lệ:
   - Hệ thống lấy tất cả sách từ repository
   - Hệ thống lọc sách theo từ khóa (không phân biệt hoa thường):
     - Mã sách chứa từ khóa
     - Nhà xuất bản chứa từ khóa
     - Loại sách chứa từ khóa
   - Hệ thống trả về danh sách sách tìm được
4. Nếu từ khóa rỗng hoặc null:
   - Hệ thống trả về lỗi "Từ khóa tìm kiếm không được để trống"

### Alternative Flows

#### A1: Không tìm thấy kết quả
- Hệ thống trả về danh sách rỗng `[]`

### Postconditions
- Người dùng nhận được danh sách sách phù hợp với từ khóa

---

## 6. UC6: Lấy Sách Giáo Khoa Theo Nhà Xuất Bản

### Mô tả
Người dùng lấy danh sách các sách giáo khoa của một nhà xuất bản cụ thể.

### Actors
- Người dùng (User)

### Preconditions
- Hệ thống đang hoạt động

### Main Flow
1. Người dùng gửi request GET `/api/books/publisher/{publisher}`
2. Hệ thống validate tên nhà xuất bản
3. Nếu tên nhà xuất bản hợp lệ:
   - Hệ thống lấy tất cả sách từ repository
   - Hệ thống lọc:
     - Chỉ lấy sách giáo khoa (SachGiaoKhoa)
     - Nhà xuất bản chứa từ khóa (không phân biệt hoa thường)
   - Hệ thống trả về danh sách sách giáo khoa
4. Nếu tên nhà xuất bản rỗng hoặc null:
   - Hệ thống trả về lỗi "Tên nhà xuất bản không được để trống"

### Alternative Flows

#### A1: Không tìm thấy sách giáo khoa
- Hệ thống trả về danh sách rỗng `[]`

#### A2: Chỉ có sách tham khảo
- Hệ thống trả về danh sách rỗng `[]` (chỉ trả về sách giáo khoa)

### Postconditions
- Người dùng nhận được danh sách sách giáo khoa của nhà xuất bản

---

## 7. UC7: Tính Tổng Thành Tiền Theo Từng Loại

### Mô tả
Người dùng xem thống kê tổng thành tiền của từng loại sách (sách giáo khoa, sách tham khảo) và tổng tất cả.

### Actors
- Người dùng (User)

### Preconditions
- Hệ thống đang hoạt động

### Main Flow
1. Người dùng gửi request GET `/api/books/statistics/total-by-type`
2. Hệ thống lấy tất cả sách từ repository
3. Hệ thống tính toán:
   - Tổng thành tiền sách giáo khoa (tổng của tất cả SachGiaoKhoa)
   - Tổng thành tiền sách tham khảo (tổng của tất cả SachThamKhao)
   - Tổng thành tiền tất cả = tổng sách giáo khoa + tổng sách tham khảo
4. Hệ thống trả về kết quả thống kê

### Alternative Flows

#### A1: Không có sách nào
- Tất cả các tổng = 0

### Postconditions
- Người dùng nhận được thống kê tổng thành tiền theo từng loại

---

## 8. UC8: Tính Trung Bình Cộng Đơn Giá Sách Tham Khảo

### Mô tả
Người dùng xem thống kê trung bình cộng đơn giá của các sách tham khảo trong hệ thống.

### Actors
- Người dùng (User)

### Preconditions
- Hệ thống đang hoạt động

### Main Flow
1. Người dùng gửi request GET `/api/books/statistics/average-price`
2. Hệ thống lấy tất cả sách từ repository
3. Hệ thống lọc chỉ lấy sách tham khảo (SachThamKhao)
4. Nếu có sách tham khảo:
   - Hệ thống tính tổng đơn giá của tất cả sách tham khảo
   - Hệ thống tính trung bình = tổng đơn giá / số lượng sách tham khảo
   - Hệ thống trả về kết quả (trung bình đơn giá, số lượng sách tham khảo)
5. Nếu không có sách tham khảo:
   - Hệ thống trả về trung bình = 0, số lượng = 0

### Postconditions
- Người dùng nhận được thống kê trung bình đơn giá sách tham khảo

---

## Business Rules

### Tính thành tiền

#### Sách giáo khoa
- **Tình trạng mới**: `Thành tiền = Số lượng × Đơn giá`
- **Tình trạng cũ**: `Thành tiền = Số lượng × Đơn giá × 50%`

#### Sách tham khảo
- `Thành tiền = Số lượng × Đơn giá + Thuế`

### Validation Rules

1. **Mã sách**: Tự động tạo nếu không cung cấp, phải unique
2. **Ngày nhập**: Format `dd/MM/yyyy` hoặc `yyyy-MM-dd`
3. **Đơn giá**: Phải ≥ 0
4. **Số lượng**: Phải > 0
5. **Nhà xuất bản**: Không được để trống
6. **Tình trạng** (sách giáo khoa): Chỉ chấp nhận "mới" hoặc "cũ"
7. **Thuế** (sách tham khảo): Phải ≥ 0

