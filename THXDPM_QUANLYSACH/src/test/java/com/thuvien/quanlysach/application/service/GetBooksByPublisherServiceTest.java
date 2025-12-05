package com.thuvien.quanlysach.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.BookResponse;
import com.thuvien.quanlysach.application.usecase.GetBooksByPublisherUseCase;
import com.thuvien.quanlysach.domain.Result;
import com.thuvien.quanlysach.domain.entity.book.Book;
import com.thuvien.quanlysach.domain.entity.book.SachGiaoKhoa;
import com.thuvien.quanlysach.domain.entity.book.SachThamKhao;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookStatus;
import com.thuvien.quanlysach.domain.entity.book.valueobject.ImportDate;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Price;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Publisher;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Quantity;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Tax;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetBooksByPublisherServiceTest {

    private InMemoryBookRepository bookRepository;
    private GetBooksByPublisherService service;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        service = new GetBooksByPublisherService(bookRepository);
    }

    @Test
    void kichBan1_shouldReturnFailure_whenPublisherIsEmpty() {
        // Kịch bản 1: Tên nhà xuất bản rỗng
        final Result<List<BookResponse>> result = service.execute("");

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().orElse("").contains("Tên nhà xuất bản không được để trống"));
    }

    @Test
    void kichBan2_shouldReturnFailure_whenPublisherIsNull() {
        // Kịch bản 2: Tên nhà xuất bản null
        final Result<List<BookResponse>> result = service.execute(null);

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().orElse("").contains("Tên nhà xuất bản không được để trống"));
    }

    @Test
    void kichBan3_shouldReturnOnlySachGiaoKhoa() {
        // Kịch bản 3: Chỉ trả về sách giáo khoa, không trả về sách tham khảo
        final SachGiaoKhoa bookGiaoKhoa = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        final SachThamKhao bookThamKhao = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Giáo Dục"), // Cùng nhà xuất bản
                Tax.of(5000)
        );

        bookRepository.save(bookGiaoKhoa);
        bookRepository.save(bookThamKhao);

        final Result<List<BookResponse>> result = service.execute("NXB Giáo Dục");

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();
        // Chỉ trả về sách giáo khoa
        assertEquals(1, books.size());
        assertEquals("Sách giáo khoa", books.get(0).loaiSach());
        assertEquals("SGK-001", books.get(0).maSach());
    }

    @Test
    void kichBan4_shouldReturnBooksByExactPublisher() {
        // Kịch bản 4: Lấy sách giáo khoa theo nhà xuất bản chính xác
        final SachGiaoKhoa book1 = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        final SachGiaoKhoa book2 = SachGiaoKhoa.create(
                BookId.of("SGK-002"),
                ImportDate.of(16, 1, 2024),
                Price.of(60000),
                Quantity.of(12),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.CU
        );

        final SachGiaoKhoa book3 = SachGiaoKhoa.create(
                BookId.of("SGK-003"),
                ImportDate.of(17, 1, 2024),
                Price.of(70000),
                Quantity.of(8),
                Publisher.of("NXB Khoa Học"), // Nhà xuất bản khác
                BookStatus.MOI
        );

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        final Result<List<BookResponse>> result = service.execute("NXB Giáo Dục");

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();
        assertEquals(2, books.size());
        // Kiểm tra tất cả đều là sách giáo khoa của NXB Giáo Dục
        assertTrue(books.stream().allMatch(b -> "NXB Giáo Dục".equals(b.nhaXuatBan())));
        assertTrue(books.stream().allMatch(b -> "Sách giáo khoa".equals(b.loaiSach())));
    }

    @Test
    void kichBan5_shouldSearchCaseInsensitive() {
        // Kịch bản 5: Tìm kiếm không phân biệt hoa thường
        final SachGiaoKhoa book = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        bookRepository.save(book);

        // Tìm với chữ hoa
        final Result<List<BookResponse>> result1 = service.execute("NXB GIÁO DỤC");
        assertTrue(result1.isSuccess());
        assertEquals(1, result1.payload().orElseThrow().size());

        // Tìm với chữ thường
        final Result<List<BookResponse>> result2 = service.execute("nxb giáo dục");
        assertTrue(result2.isSuccess());
        assertEquals(1, result2.payload().orElseThrow().size());

        // Tìm với chữ hoa thường lẫn lộn
        final Result<List<BookResponse>> result3 = service.execute("Nxb Giáo Dục");
        assertTrue(result3.isSuccess());
        assertEquals(1, result3.payload().orElseThrow().size());
    }

    @Test
    void kichBan6_shouldReturnEmptyList_whenNoMatch() {
        // Kịch bản 6: Không tìm thấy sách giáo khoa của nhà xuất bản
        final SachGiaoKhoa book = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        bookRepository.save(book);

        final Result<List<BookResponse>> result = service.execute("NXB Không Tồn Tại");

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();
        assertEquals(0, books.size());
    }

    @Test
    void kichBan7_shouldReturnEmptyList_whenOnlySachThamKhaoExists() {
        // Kịch bản 7: Chỉ có sách tham khảo, không có sách giáo khoa
        final SachThamKhao book = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Giáo Dục"),
                Tax.of(5000)
        );

        bookRepository.save(book);

        final Result<List<BookResponse>> result = service.execute("NXB Giáo Dục");

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();
        // Không trả về sách tham khảo
        assertEquals(0, books.size());
    }

    @Test
    void kichBan8_shouldSearchByPartialPublisherName() {
        // Kịch bản 8: Tìm kiếm theo một phần tên nhà xuất bản
        final SachGiaoKhoa book1 = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        final SachGiaoKhoa book2 = SachGiaoKhoa.create(
                BookId.of("SGK-002"),
                ImportDate.of(16, 1, 2024),
                Price.of(60000),
                Quantity.of(12),
                Publisher.of("NXB Giáo Dục Việt Nam"),
                BookStatus.CU
        );

        bookRepository.save(book1);
        bookRepository.save(book2);

        // Tìm theo "Giáo" - sẽ tìm thấy cả 2 sách
        final Result<List<BookResponse>> result = service.execute("Giáo");

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();
        assertEquals(2, books.size());
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

