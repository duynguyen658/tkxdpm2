package com.thuvien.quanlysach.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.usecase.DeleteBookUseCase;
import com.thuvien.quanlysach.domain.Result;
import com.thuvien.quanlysach.domain.entity.book.Book;
import com.thuvien.quanlysach.domain.entity.book.SachGiaoKhoa;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookId;
import com.thuvien.quanlysach.domain.entity.book.valueobject.BookStatus;
import com.thuvien.quanlysach.domain.entity.book.valueobject.ImportDate;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Price;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Publisher;
import com.thuvien.quanlysach.domain.entity.book.valueobject.Quantity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteBookServiceTest {

    private InMemoryBookRepository bookRepository;
    private DeleteBookService service;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        service = new DeleteBookService(bookRepository);
    }

    @Test
    void kichBan1_shouldDeleteBook_whenBookExists() {
        // Kịch bản 1: Xóa sách tồn tại
        final BookId bookId = BookId.of("SGK-001");
        final SachGiaoKhoa book = SachGiaoKhoa.create(
                bookId,
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );
        bookRepository.save(book);

        // Xác nhận sách tồn tại trước khi xóa
        assertTrue(bookRepository.existsById(bookId));

        final Result<String> result = service.execute("SGK-001");

        assertTrue(result.isSuccess());
        assertTrue(result.payload().orElse("").contains("Đã xóa sách thành công"));

        // Xác nhận sách đã bị xóa
        assertFalse(bookRepository.existsById(bookId));
        assertTrue(bookRepository.findById(bookId).isEmpty());
    }

    @Test
    void kichBan2_shouldReturnFailure_whenBookNotFound() {
        // Kịch bản 2: Xóa sách không tồn tại
        final Result<String> result = service.execute("SGK-NOT-FOUND");

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().orElse("").contains("Không tìm thấy"));
    }

    @Test
    void kichBan3_shouldDeleteOnlySpecifiedBook() {
        // Kịch bản 3: Xóa chỉ sách được chỉ định, các sách khác vẫn còn
        final BookId bookId1 = BookId.of("SGK-001");
        final BookId bookId2 = BookId.of("SGK-002");

        final SachGiaoKhoa book1 = SachGiaoKhoa.create(
                bookId1,
                ImportDate.of(15, 1, 2024),
                Price.of(50000),
                Quantity.of(10),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.MOI
        );

        final SachGiaoKhoa book2 = SachGiaoKhoa.create(
                bookId2,
                ImportDate.of(16, 1, 2024),
                Price.of(60000),
                Quantity.of(12),
                Publisher.of("NXB Giáo Dục"),
                BookStatus.CU
        );

        bookRepository.save(book1);
        bookRepository.save(book2);

        // Xóa book1
        final Result<String> result = service.execute("SGK-001");

        assertTrue(result.isSuccess());
        // book1 đã bị xóa
        assertFalse(bookRepository.existsById(bookId1));
        // book2 vẫn còn
        assertTrue(bookRepository.existsById(bookId2));
        assertEquals(1, bookRepository.findAll().size());
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

