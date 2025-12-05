package com.thuvien.quanlysach.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.CalculateTotalByTypeUseCase;
import com.thuvien.quanlysach.application.usecase.TotalByTypeResponse;
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

class CalculateTotalByTypeServiceTest {

    private InMemoryBookRepository bookRepository;
    private CalculateTotalByTypeService service;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        service = new CalculateTotalByTypeService(bookRepository);
    }

    @Test
    void kichBan1_shouldReturnZero_whenNoBooks() {
        // Kịch bản 1: Tính tổng khi chưa có sách nào
        final Result<TotalByTypeResponse> result = service.execute();

        assertTrue(result.isSuccess());
        final TotalByTypeResponse response = result.payload().orElseThrow();
        assertEquals(0.0, response.tongThanhTienSachGiaoKhoa(), 0.01);
        assertEquals(0.0, response.tongThanhTienSachThamKhao(), 0.01);
        assertEquals(0.0, response.tongThanhTienTatCa(), 0.01);
    }

    @Test
    void kichBan2_shouldCalculateTotalForSachGiaoKhoa() {
        // Kịch bản 2: Tính tổng thành tiền cho sách giáo khoa
        // Sách giáo khoa mới: 10 * 50000 = 500000
        final SachGiaoKhoa bookMoi = SachGiaoKhoa.create(
                BookId.of("SGK-MOI"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        // Sách giáo khoa cũ: 10 * 50000 * 0.5 = 250000
        final SachGiaoKhoa bookCu = SachGiaoKhoa.create(
                BookId.of("SGK-CU"),
                ImportDate.of(10, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.CU
        );

        bookRepository.save(bookMoi);
        bookRepository.save(bookCu);

        final Result<TotalByTypeResponse> result = service.execute();

        assertTrue(result.isSuccess());
        final TotalByTypeResponse response = result.payload().orElseThrow();
        // Tổng sách giáo khoa: 500000 + 250000 = 750000
        assertEquals(750000, response.tongThanhTienSachGiaoKhoa(), 0.01);
        assertEquals(0.0, response.tongThanhTienSachThamKhao(), 0.01);
        assertEquals(750000, response.tongThanhTienTatCa(), 0.01);
    }

    @Test
    void kichBan3_shouldCalculateTotalForSachThamKhao() {
        // Kịch bản 3: Tính tổng thành tiền cho sách tham khảo
        // Sách tham khảo 1: 5 * 80000 + 5000 = 405000
        final SachThamKhao book1 = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );

        // Sách tham khảo 2: 3 * 90000 + 3000 = 273000
        final SachThamKhao book2 = SachThamKhao.create(
                BookId.of("STK-002"),
                ImportDate.of(16, 1, 2024),
                Price.of(90000),
                Quantity.of(3),
                Publisher.of("NXB Khoa Học"),
                Tax.of(3000)
        );

        bookRepository.save(book1);
        bookRepository.save(book2);

        final Result<TotalByTypeResponse> result = service.execute();

        assertTrue(result.isSuccess());
        final TotalByTypeResponse response = result.payload().orElseThrow();
        assertEquals(0.0, response.tongThanhTienSachGiaoKhoa(), 0.01);
        // Tổng sách tham khảo: 405000 + 273000 = 678000
        assertEquals(678000, response.tongThanhTienSachThamKhao(), 0.01);
        assertEquals(678000, response.tongThanhTienTatCa(), 0.01);
    }

    @Test
    void kichBan4_shouldCalculateTotalForBothTypes() {
        // Kịch bản 4: Tính tổng thành tiền cho cả 2 loại sách
        // Sách giáo khoa mới: 10 * 50000 = 500000
        final SachGiaoKhoa bookGiaoKhoa = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        // Sách tham khảo: 5 * 80000 + 5000 = 405000
        final SachThamKhao bookThamKhao = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );

        bookRepository.save(bookGiaoKhoa);
        bookRepository.save(bookThamKhao);

        final Result<TotalByTypeResponse> result = service.execute();

        assertTrue(result.isSuccess());
        final TotalByTypeResponse response = result.payload().orElseThrow();
        assertEquals(500000, response.tongThanhTienSachGiaoKhoa(), 0.01);
        assertEquals(405000, response.tongThanhTienSachThamKhao(), 0.01);
        // Tổng tất cả: 500000 + 405000 = 905000
        assertEquals(905000, response.tongThanhTienTatCa(), 0.01);
    }

    @Test
    void kichBan5_shouldCalculateCorrectlyWithMultipleBooks() {
        // Kịch bản 5: Tính tổng với nhiều sách
        // Sách giáo khoa mới 1: 10 * 50000 = 500000
        final SachGiaoKhoa bookGiaoKhoa1 = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        // Sách giáo khoa cũ: 10 * 50000 * 0.5 = 250000
        final SachGiaoKhoa bookGiaoKhoa2 = SachGiaoKhoa.create(
                BookId.of("SGK-002"),
                ImportDate.of(10, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.CU
        );

        // Sách tham khảo 1: 5 * 80000 + 5000 = 405000
        final SachThamKhao bookThamKhao1 = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );

        // Sách tham khảo 2: 3 * 90000 + 3000 = 273000
        final SachThamKhao bookThamKhao2 = SachThamKhao.create(
                BookId.of("STK-002"),
                ImportDate.of(16, 1, 2024),
                Price.of(90000),
                Quantity.of(3),
                Publisher.of("NXB Khoa Học"),
                Tax.of(3000)
        );

        bookRepository.save(bookGiaoKhoa1);
        bookRepository.save(bookGiaoKhoa2);
        bookRepository.save(bookThamKhao1);
        bookRepository.save(bookThamKhao2);

        final Result<TotalByTypeResponse> result = service.execute();

        assertTrue(result.isSuccess());
        final TotalByTypeResponse response = result.payload().orElseThrow();
        // Tổng sách giáo khoa: 500000 + 250000 = 750000
        assertEquals(750000, response.tongThanhTienSachGiaoKhoa(), 0.01);
        // Tổng sách tham khảo: 405000 + 273000 = 678000
        assertEquals(678000, response.tongThanhTienSachThamKhao(), 0.01);
        // Tổng tất cả: 750000 + 678000 = 1428000
        assertEquals(1428000, response.tongThanhTienTatCa(), 0.01);
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

