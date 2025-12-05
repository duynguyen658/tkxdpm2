package com.thuvien.quanlysach.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.BookResponse;
import com.thuvien.quanlysach.application.usecase.GetAllBooksUseCase;
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

class GetAllBooksServiceTest {

    private InMemoryBookRepository bookRepository;
    private GetAllBooksService service;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        service = new GetAllBooksService(bookRepository);
    }

    @Test
    void kichBan1_shouldReturnEmptyList_whenNoBooks() {
        // Kịch bản 1: Lấy danh sách khi chưa có sách nào
        final Result<List<BookResponse>> result = service.execute();

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();
        assertEquals(0, books.size());
    }

    @Test
    void kichBan2_shouldReturnAllBooks_whenBooksExist() {
        // Kịch bản 2: Lấy danh sách tất cả sách
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

        final Result<List<BookResponse>> result = service.execute();

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();
        assertEquals(2, books.size());

        // Kiểm tra sách giáo khoa
        final BookResponse sachGiaoKhoa = books.stream()
                .filter(b -> "SGK-001".equals(b.maSach()))
                .findFirst()
                .orElseThrow();
        assertEquals("Sách giáo khoa", sachGiaoKhoa.loaiSach());
        assertEquals("mới", sachGiaoKhoa.tinhTrang());
        assertEquals(null, sachGiaoKhoa.thue());
        assertEquals(500000, sachGiaoKhoa.thanhTien(), 0.01); // 10 * 50000

        // Kiểm tra sách tham khảo
        final BookResponse sachThamKhao = books.stream()
                .filter(b -> "STK-001".equals(b.maSach()))
                .findFirst()
                .orElseThrow();
        assertEquals("Sách tham khảo", sachThamKhao.loaiSach());
        assertEquals(null, sachThamKhao.tinhTrang());
        assertEquals(5000, sachThamKhao.thue(), 0.01);
        assertEquals(405000, sachThamKhao.thanhTien(), 0.01); // 5 * 80000 + 5000
    }

    @Test
    void kichBan3_shouldReturnBooksWithCorrectCalculations() {
        // Kịch bản 3: Kiểm tra tính toán thành tiền đúng
        // Sách giáo khoa cũ
        final SachGiaoKhoa bookCu = SachGiaoKhoa.create(
                BookId.of("SGK-CU"),
                ImportDate.of(10, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.CU
        );

        // Sách tham khảo
        final SachThamKhao bookThamKhao = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );

        bookRepository.save(bookCu);
        bookRepository.save(bookThamKhao);

        final Result<List<BookResponse>> result = service.execute();

        assertTrue(result.isSuccess());
        final List<BookResponse> books = result.payload().orElseThrow();

        // Kiểm tra sách giáo khoa cũ: 10 * 50000 * 0.5 = 250000
        final BookResponse sachCu = books.stream()
                .filter(b -> "SGK-CU".equals(b.maSach()))
                .findFirst()
                .orElseThrow();
        assertEquals(250000, sachCu.thanhTien(), 0.01);

        // Kiểm tra sách tham khảo: 5 * 80000 + 5000 = 405000
        final BookResponse sachThamKhao = books.stream()
                .filter(b -> "STK-001".equals(b.maSach()))
                .findFirst()
                .orElseThrow();
        assertEquals(405000, sachThamKhao.thanhTien(), 0.01);
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

