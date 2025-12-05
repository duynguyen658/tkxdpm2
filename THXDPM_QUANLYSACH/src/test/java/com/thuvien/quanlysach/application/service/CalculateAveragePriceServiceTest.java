package com.thuvien.quanlysach.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.AveragePriceResponse;
import com.thuvien.quanlysach.application.usecase.CalculateAveragePriceUseCase;
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

class CalculateAveragePriceServiceTest {

    private InMemoryBookRepository bookRepository;
    private CalculateAveragePriceService service;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        service = new CalculateAveragePriceService(bookRepository);
    }

    @Test
    void kichBan1_shouldReturnZero_whenNoSachThamKhao() {
        // Kịch bản 1: Không có sách tham khảo
        final SachGiaoKhoa bookGiaoKhoa = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        bookRepository.save(bookGiaoKhoa);

        final Result<AveragePriceResponse> result = service.execute();

        assertTrue(result.isSuccess());
        final AveragePriceResponse response = result.payload().orElseThrow();
        assertEquals(0.0, response.trungBinhCongDonGia(), 0.01);
        assertEquals(0, response.soLuongSachThamKhao());
    }

    @Test
    void kichBan2_shouldCalculateAverage_whenOneSachThamKhao() {
        // Kịch bản 2: Tính trung bình với 1 sách tham khảo
        final SachThamKhao book = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );

        bookRepository.save(book);

        final Result<AveragePriceResponse> result = service.execute();

        assertTrue(result.isSuccess());
        final AveragePriceResponse response = result.payload().orElseThrow();
        assertEquals(80000, response.trungBinhCongDonGia(), 0.01);
        assertEquals(1, response.soLuongSachThamKhao());
    }

    @Test
    void kichBan3_shouldCalculateAverage_whenMultipleSachThamKhao() {
        // Kịch bản 3: Tính trung bình với nhiều sách tham khảo
        // Sách 1: đơn giá 80000
        final SachThamKhao book1 = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );

        // Sách 2: đơn giá 90000
        final SachThamKhao book2 = SachThamKhao.create(
                BookId.of("STK-002"),
                ImportDate.of(16, 1, 2024),
                Price.of(90000),
                Quantity.of(3),
                Publisher.of("NXB Khoa Học"),
                Tax.of(3000)
        );

        // Sách 3: đơn giá 100000
        final SachThamKhao book3 = SachThamKhao.create(
                BookId.of("STK-003"),
                ImportDate.of(17, 1, 2024),
                Price.of(100000),
                Quantity.of(2),
                Publisher.of("NXB Khoa Học"),
                Tax.of(7000)
        );

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        final Result<AveragePriceResponse> result = service.execute();

        assertTrue(result.isSuccess());
        final AveragePriceResponse response = result.payload().orElseThrow();
        // Trung bình: (80000 + 90000 + 100000) / 3 = 90000
        assertEquals(90000, response.trungBinhCongDonGia(), 0.01);
        assertEquals(3, response.soLuongSachThamKhao());
    }

    @Test
    void kichBan4_shouldIgnoreSachGiaoKhoa() {
        // Kịch bản 4: Chỉ tính sách tham khảo, bỏ qua sách giáo khoa
        final SachGiaoKhoa bookGiaoKhoa = SachGiaoKhoa.create(
                BookId.of("SGK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        final SachThamKhao bookThamKhao1 = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );

        final SachThamKhao bookThamKhao2 = SachThamKhao.create(
                BookId.of("STK-002"),
                ImportDate.of(16, 1, 2024),
                Price.of(90000),
                Quantity.of(3),
                Publisher.of("NXB Khoa Học"),
                Tax.of(3000)
        );

        bookRepository.save(bookGiaoKhoa);
        bookRepository.save(bookThamKhao1);
        bookRepository.save(bookThamKhao2);

        final Result<AveragePriceResponse> result = service.execute();

        assertTrue(result.isSuccess());
        final AveragePriceResponse response = result.payload().orElseThrow();
        // Chỉ tính 2 sách tham khảo: (80000 + 90000) / 2 = 85000
        assertEquals(85000, response.trungBinhCongDonGia(), 0.01);
        assertEquals(2, response.soLuongSachThamKhao());
    }

    @Test
    void kichBan5_shouldCalculateWithDecimalResult() {
        // Kịch bản 5: Tính trung bình có kết quả thập phân
        final SachThamKhao book1 = SachThamKhao.create(
                BookId.of("STK-001"),
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );

        final SachThamKhao book2 = SachThamKhao.create(
                BookId.of("STK-002"),
                ImportDate.of(16, 1, 2024),
                Price.of(90000),
                Quantity.of(3),
                Publisher.of("NXB Khoa Học"),
                Tax.of(3000)
        );

        final SachThamKhao book3 = SachThamKhao.create(
                BookId.of("STK-003"),
                ImportDate.of(17, 1, 2024),
                Price.of(85000),
                Quantity.of(2),
                Publisher.of("NXB Khoa Học"),
                Tax.of(7000)
        );

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        final Result<AveragePriceResponse> result = service.execute();

        assertTrue(result.isSuccess());
        final AveragePriceResponse response = result.payload().orElseThrow();
        // Trung bình: (80000 + 90000 + 85000) / 3 = 85000
        assertEquals(85000, response.trungBinhCongDonGia(), 0.01);
        assertEquals(3, response.soLuongSachThamKhao());
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

