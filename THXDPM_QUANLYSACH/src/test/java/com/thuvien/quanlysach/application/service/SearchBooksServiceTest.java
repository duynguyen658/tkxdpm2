package com.thuvien.quanlysach.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.BookResponse;
import com.thuvien.quanlysach.application.usecase.SearchBooksUseCase;
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

class SearchBooksServiceTest {

    private InMemoryBookRepository bookRepository;
    private SearchBooksService service;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        service = new SearchBooksService(bookRepository);
    }

    @Test
    void kichBan1_shouldReturnFailure_whenKeywordIsEmpty() {
        // Kịch bản 1: Tìm kiếm với từ khóa rỗng
        final Result<List<BookResponse>> result = service.execute("");

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().orElse("").contains("Từ khóa tìm kiếm không được để trống"));
    }

    @Test
    void kichBan2_shouldReturnFailure_whenKeywordIsNull() {
        // Kịch bản 2: Tìm kiếm với từ khóa null
        final Result<List<BookResponse>> result = service.execute(null);

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().orElse("").contains("Từ khóa tìm kiếm không được để trống"));
    }

    @Test
    void kichBan3_shouldSearchByBookId() {
        // Kịch bản 3: Tìm kiếm theo mã sách
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

        bookRepository.save(book1);
        bookRepository.save(book2);

        final Result<List<BookResponse>> result = service.execute("SGK-001");

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();
        assertEquals(1, books.size());
        assertEquals("SGK-001", books.get(0).maSach());
    }

    @Test
    void kichBan4_shouldSearchByPublisher() {
        // Kịch bản 4: Tìm kiếm theo nhà xuất bản
        final SachGiaoKhoa book1 = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        final SachThamKhao book2 = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );

        bookRepository.save(book1);
        bookRepository.save(book2);

        // Tìm theo "Giáo" - sẽ tìm thấy sách có nhà xuất bản "NXB Giáo Dục"
        final Result<List<BookResponse>> result = service.execute("Giáo");

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();
        assertEquals(1, books.size());
        assertEquals("NXB Giáo Dục", books.get(0).nhaXuatBan());
    }

    @Test
    void kichBan5_shouldSearchByBookType() {
        // Kịch bản 5: Tìm kiếm theo loại sách
        final SachGiaoKhoa book1 = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        final SachThamKhao book2 = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );

        bookRepository.save(book1);
        bookRepository.save(book2);

        // Tìm theo "tham khảo"
        final Result<List<BookResponse>> result = service.execute("tham khảo");

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();
        assertEquals(1, books.size());
        assertEquals("Sách tham khảo", books.get(0).loaiSach());
    }

    @Test
    void kichBan6_shouldSearchCaseInsensitive() {
        // Kịch bản 6: Tìm kiếm không phân biệt hoa thường
        final SachGiaoKhoa book1 = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        bookRepository.save(book1);

        // Tìm với chữ hoa
        final Result<List<BookResponse>> result1 = service.execute("GIÁO");
        assertTrue(result1.isSuccess());
        assertEquals(1, result1.payload().orElseThrow().size());

        // Tìm với chữ thường
        final Result<List<BookResponse>> result2 = service.execute("giáo");
        assertTrue(result2.isSuccess());
        assertEquals(1, result2.payload().orElseThrow().size());

        // Tìm với chữ hoa thường lẫn lộn
        final Result<List<BookResponse>> result3 = service.execute("GiÁo");
        assertTrue(result3.isSuccess());
        assertEquals(1, result3.payload().orElseThrow().size());
    }

    @Test
    void kichBan7_shouldReturnEmptyList_whenNoMatch() {
        // Kịch bản 7: Không tìm thấy kết quả
        final SachGiaoKhoa book1 = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        bookRepository.save(book1);

        final Result<List<BookResponse>> result = service.execute("Không tồn tại");

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();
        assertEquals(0, books.size());
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

