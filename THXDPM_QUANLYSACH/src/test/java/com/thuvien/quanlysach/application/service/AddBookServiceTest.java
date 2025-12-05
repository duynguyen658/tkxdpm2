package com.thuvien.quanlysach.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.AddBookRequest;
import com.thuvien.quanlysach.application.usecase.AddBookResponse;
import com.thuvien.quanlysach.domain.Result;
import com.thuvien.quanlysach.domain.entity.book.Book;
import com.thuvien.quanlysach.domain.entity.book.SachGiaoKhoa;
import com.thuvien.quanlysach.domain.entity.book.SachThamKhao;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddBookServiceTest {

    private InMemoryBookRepository bookRepository;
    private AddBookService service;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        service = new AddBookService(bookRepository);
    }

    @Test
    void kichBan1_shouldAddSachGiaoKhoa_whenStatusIsMoi() {
        // Kịch bản 1: Thêm sách giáo khoa tình trạng mới
        final AddBookRequest request = new AddBookRequest(
                "SACH_GIAO_KHOA",
                "SGK-001",
                "15/01/2024",
                50000,
                10,
                "NXB Giáo Dục",
                "mới",
                null
        );

        final Result<AddBookResponse> result = service.execute(request);

        assertTrue(result.isSuccess());
        final AddBookResponse response = result.payload().orElseThrow();
        assertEquals("SGK-001", response.bookId());
        assertEquals("Sách giáo khoa", response.bookType());

        // Kiểm tra sách đã được lưu
        final Book savedBook = bookRepository.findById(BookId.of("SGK-001")).orElseThrow();
        assertTrue(savedBook instanceof SachGiaoKhoa);
        final SachGiaoKhoa sachGiaoKhoa = (SachGiaoKhoa) savedBook;
        assertEquals("mới", sachGiaoKhoa.status().displayName());
        // Thành tiền = 10 * 50000 = 500000
        assertEquals(500000, savedBook.calculateTotal().value(), 0.01);
    }

    @Test
    void kichBan2_shouldAddSachGiaoKhoa_whenStatusIsCu() {
        // Kịch bản 2: Thêm sách giáo khoa tình trạng cũ
        final AddBookRequest request = new AddBookRequest(
                "SACH_GIAO_KHOA",
                "SGK-002",
                "15/01/2024",
                50000,
                10,
                "NXB Giáo Dục",
                "cũ",
                null
        );

        final Result<AddBookResponse> result = service.execute(request);

        assertTrue(result.isSuccess());
        final Book savedBook = bookRepository.findById(BookId.of("SGK-002")).orElseThrow();
        // Thành tiền = 10 * 50000 * 0.5 = 250000
        assertEquals(250000, savedBook.calculateTotal().value(), 0.01);
    }

    @Test
    void kichBan3_shouldAddSachThamKhao_whenAllFieldsValid() {
        // Kịch bản 3: Thêm sách tham khảo
        final AddBookRequest request = new AddBookRequest(
                "SACH_THAM_KHAO",
                "STK-001",
                "15/01/2024",
                80000,
                5,
                "NXB Khoa Học",
                null,
                5000.0
        );

        final Result<AddBookResponse> result = service.execute(request);

        assertTrue(result.isSuccess());
        final AddBookResponse response = result.payload().orElseThrow();
        assertEquals("STK-001", response.bookId());
        assertEquals("Sách tham khảo", response.bookType());

        // Kiểm tra sách đã được lưu
        final Book savedBook = bookRepository.findById(BookId.of("STK-001")).orElseThrow();
        assertTrue(savedBook instanceof SachThamKhao);
        final SachThamKhao sachThamKhao = (SachThamKhao) savedBook;
        assertEquals(5000, sachThamKhao.tax().value(), 0.01);
        // Thành tiền = 5 * 80000 + 5000 = 405000
        assertEquals(405000, savedBook.calculateTotal().value(), 0.01);
    }

    @Test
    void kichBan4_shouldReturnFailure_whenSachGiaoKhoaMissingStatus() {
        // Kịch bản 4: Thêm sách giáo khoa thiếu tình trạng
        final AddBookRequest request = new AddBookRequest(
                "SACH_GIAO_KHOA",
                "SGK-003",
                "15/01/2024",
                50000,
                10,
                "NXB Giáo Dục",
                null, // Thiếu tình trạng
                null
        );

        final Result<AddBookResponse> result = service.execute(request);

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().orElse("").contains("tình trạng"));
    }

    @Test
    void kichBan5_shouldReturnFailure_whenSachThamKhaoMissingTax() {
        // Kịch bản 5: Thêm sách tham khảo thiếu thuế
        final AddBookRequest request = new AddBookRequest(
                "SACH_THAM_KHAO",
                "STK-002",
                "15/01/2024",
                80000,
                5,
                "NXB Khoa Học",
                null,
                null // Thiếu thuế
        );

        final Result<AddBookResponse> result = service.execute(request);

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().orElse("").contains("thuế"));
    }

    @Test
    void kichBan6_shouldReturnFailure_whenBookIdAlreadyExists() {
        // Kịch bản 6: Thêm sách với mã sách đã tồn tại
        final AddBookRequest firstRequest = new AddBookRequest(
                "SACH_GIAO_KHOA",
                "SGK-DUPLICATE",
                "15/01/2024",
                50000,
                10,
                "NXB Giáo Dục",
                "mới",
                null
        );
        service.execute(firstRequest); // Thêm lần đầu

        final AddBookRequest duplicateRequest = new AddBookRequest(
                "SACH_GIAO_KHOA",
                "SGK-DUPLICATE", // Mã trùng
                "20/01/2024",
                60000,
                15,
                "NXB Giáo Dục",
                "cũ",
                null
        );

        final Result<AddBookResponse> result = service.execute(duplicateRequest);

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().orElse("").contains("đã tồn tại"));
    }

    @Test
    void kichBan7_shouldAutoGenerateBookId_whenNotProvided() {
        // Kịch bản 7: Tự động tạo mã sách nếu không cung cấp
        final AddBookRequest request = new AddBookRequest(
                "SACH_GIAO_KHOA",
                null, // Không cung cấp mã sách
                "15/01/2024",
                50000,
                10,
                "NXB Giáo Dục",
                "mới",
                null
        );

        final Result<AddBookResponse> result = service.execute(request);

        assertTrue(result.isSuccess());
        final AddBookResponse response = result.payload().orElseThrow();
        assertNotNull(response.bookId());
        assertFalse(response.bookId().isBlank());
    }

    private static class InMemoryBookRepository implements BookRepository {
        private final Map<String, Book> books = new HashMap<>();

        @Override
        public Optional<Book> findById(final BookId bookId) {
            return Optional.ofNullable(books.get(bookId.value()));
        }

        @Override
        public List<Book> findAll() {
            return List.copyOf(books.values());
        }

        @Override
        public Book save(final Book book) {
            books.put(book.id().value(), book);
            return book;
        }

        @Override
        public void delete(final BookId bookId) {
            books.remove(bookId.value());
        }

        @Override
        public boolean existsById(final BookId bookId) {
            return books.containsKey(bookId.value());
        }
    }
}

