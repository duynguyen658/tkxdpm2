package com.thuvien.quanlysach.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.UpdateBookRequest;
import com.thuvien.quanlysach.application.usecase.UpdateBookResponse;
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

class UpdateBookServiceTest {

    private InMemoryBookRepository bookRepository;
    private UpdateBookService service;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        service = new UpdateBookService(bookRepository);
    }

    @Test
    void kichBan1_shouldUpdateSachGiaoKhoa_whenAllFieldsValid() {
        // Kịch bản 1: Cập nhật sách giáo khoa
        // Tạo sách ban đầu
        final BookId bookId = BookId.of("SGK-001");
        final SachGiaoKhoa existingBook = SachGiaoKhoa.create(
                bookId,
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );
        bookRepository.save(existingBook);

        // Cập nhật sách
        final UpdateBookRequest request = new UpdateBookRequest(
                "SGK-001",
                "20/01/2024",
                55000,
                15,
                "NXB Giáo Dục Việt Nam",
                "cũ",
                null
        );

        final Result<UpdateBookResponse> result = service.execute("SGK-001", request);

        assertTrue(result.isSuccess());
        final Book updatedBook = bookRepository.findById(bookId).orElseThrow();
        assertTrue(updatedBook instanceof SachGiaoKhoa);
        final SachGiaoKhoa sachGiaoKhoa = (SachGiaoKhoa) updatedBook;
        assertEquals(BookStatus.CU, sachGiaoKhoa.status());
        assertEquals(55000, updatedBook.unitPrice().value(), 0.01);
        assertEquals(15, updatedBook.quantity().value());
        // Thành tiền = 15 * 55000 * 0.5 = 412500
        assertEquals(412500, updatedBook.calculateTotal().value(), 0.01);
    }

    @Test
    void kichBan2_shouldUpdateSachThamKhao_whenAllFieldsValid() {
        // Kịch bản 2: Cập nhật sách tham khảo
        // Tạo sách ban đầu
        final BookId bookId = BookId.of("STK-001");
        final SachThamKhao existingBook = SachThamKhao.create(
                bookId,
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );
        bookRepository.save(existingBook);

        // Cập nhật sách
        final UpdateBookRequest request = new UpdateBookRequest(
                "STK-001",
                "20/01/2024",
                90000,
                8,
                "NXB Khoa Học và Kỹ Thuật",
                null,
                8000.0
        );

        final Result<UpdateBookResponse> result = service.execute("STK-001", request);

        assertTrue(result.isSuccess());
        final Book updatedBook = bookRepository.findById(bookId).orElseThrow();
        assertTrue(updatedBook instanceof SachThamKhao);
        final SachThamKhao sachThamKhao = (SachThamKhao) updatedBook;
        assertEquals(90000, updatedBook.unitPrice().value(), 0.01);
        assertEquals(8, updatedBook.quantity().value());
        assertEquals(8000, sachThamKhao.tax().value(), 0.01);
        // Thành tiền = 8 * 90000 + 8000 = 728000
        assertEquals(728000, updatedBook.calculateTotal().value(), 0.01);
    }

    @Test
    void kichBan3_shouldReturnFailure_whenBookNotFound() {
        // Kịch bản 3: Cập nhật sách không tồn tại
        final UpdateBookRequest request = new UpdateBookRequest(
                "SGK-NOT-FOUND",
                "20/01/2024",
                55000,
                15,
                "NXB Giáo Dục",
                "mới",
                null
        );

        final Result<UpdateBookResponse> result = service.execute("SGK-NOT-FOUND", request);

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().orElse("").contains("Không tìm thấy"));
    }

    @Test
    void kichBan4_shouldReturnFailure_whenSachGiaoKhoaMissingStatus() {
        // Kịch bản 4: Cập nhật sách giáo khoa thiếu tình trạng
        final BookId bookId = BookId.of("SGK-002");
        final SachGiaoKhoa existingBook = SachGiaoKhoa.create(
                bookId,
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );
        bookRepository.save(existingBook);

        final UpdateBookRequest request = new UpdateBookRequest(
                "SGK-002",
                "20/01/2024",
                55000,
                15,
                "NXB Giáo Dục",
                null, // Thiếu tình trạng
                null
        );

        final Result<UpdateBookResponse> result = service.execute("SGK-002", request);

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().orElse("").contains("tình trạng"));
    }

    @Test
    void kichBan5_shouldReturnFailure_whenSachThamKhaoMissingTax() {
        // Kịch bản 5: Cập nhật sách tham khảo thiếu thuế
        final BookId bookId = BookId.of("STK-002");
        final SachThamKhao existingBook = SachThamKhao.create(
                bookId,
                ImportDate.of(15, 1, 2024),
                Price.of(80000),
                Quantity.of(5),
                Publisher.of("NXB Khoa Học"),
                Tax.of(5000)
        );
        bookRepository.save(existingBook);

        final UpdateBookRequest request = new UpdateBookRequest(
                "STK-002",
                "20/01/2024",
                90000,
                8,
                "NXB Khoa Học",
                null,
                null // Thiếu thuế
        );

        final Result<UpdateBookResponse> result = service.execute("STK-002", request);

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().orElse("").contains("thuế"));
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

