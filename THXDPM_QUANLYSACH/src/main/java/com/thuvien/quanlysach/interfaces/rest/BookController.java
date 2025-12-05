package com.thuvien.quanlysach.interfaces.rest;

import com.thuvien.quanlysach.application.usecase.AddBookRequest;
import com.thuvien.quanlysach.application.usecase.AddBookResponse;
import com.thuvien.quanlysach.application.usecase.AddBookUseCase;
import com.thuvien.quanlysach.application.usecase.AveragePriceResponse;
import com.thuvien.quanlysach.application.usecase.BookResponse;
import com.thuvien.quanlysach.application.usecase.CalculateAveragePriceUseCase;
import com.thuvien.quanlysach.application.usecase.CalculateTotalByTypeUseCase;
import com.thuvien.quanlysach.application.usecase.DeleteBookUseCase;
import com.thuvien.quanlysach.application.usecase.GetAllBooksUseCase;
import com.thuvien.quanlysach.application.usecase.GetBooksByPublisherUseCase;
import com.thuvien.quanlysach.application.usecase.SearchBooksUseCase;
import com.thuvien.quanlysach.application.usecase.TotalByTypeResponse;
import com.thuvien.quanlysach.application.usecase.UpdateBookRequest;
import com.thuvien.quanlysach.application.usecase.UpdateBookResponse;
import com.thuvien.quanlysach.application.usecase.UpdateBookUseCase;
import com.thuvien.quanlysach.domain.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final AddBookUseCase addBookUseCase;
    private final UpdateBookUseCase updateBookUseCase;
    private final DeleteBookUseCase deleteBookUseCase;
    private final GetAllBooksUseCase getAllBooksUseCase;
    private final SearchBooksUseCase searchBooksUseCase;
    private final CalculateTotalByTypeUseCase calculateTotalByTypeUseCase;
    private final CalculateAveragePriceUseCase calculateAveragePriceUseCase;
    private final GetBooksByPublisherUseCase getBooksByPublisherUseCase;

    public BookController(
            final AddBookUseCase addBookUseCase,
            final UpdateBookUseCase updateBookUseCase,
            final DeleteBookUseCase deleteBookUseCase,
            final GetAllBooksUseCase getAllBooksUseCase,
            final SearchBooksUseCase searchBooksUseCase,
            final CalculateTotalByTypeUseCase calculateTotalByTypeUseCase,
            final CalculateAveragePriceUseCase calculateAveragePriceUseCase,
            final GetBooksByPublisherUseCase getBooksByPublisherUseCase) {
        this.addBookUseCase = addBookUseCase;
        this.updateBookUseCase = updateBookUseCase;
        this.deleteBookUseCase = deleteBookUseCase;
        this.getAllBooksUseCase = getAllBooksUseCase;
        this.searchBooksUseCase = searchBooksUseCase;
        this.calculateTotalByTypeUseCase = calculateTotalByTypeUseCase;
        this.calculateAveragePriceUseCase = calculateAveragePriceUseCase;
        this.getBooksByPublisherUseCase = getBooksByPublisherUseCase;
    }

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody final AddBookRequest request) {
        final Result<AddBookResponse> result = addBookUseCase.execute(request);

        if (result.isSuccess()) {
            final AddBookResponse response = result.payload().orElseThrow();
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", response.message(),
                    "book", Map.of(
                            "bookId", response.bookId(),
                            "bookType", response.bookType()
                    )
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", result.errorMessage().orElse("Không thể thêm sách")
        ));
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<?> updateBook(
            @PathVariable final String bookId,
            @RequestBody final UpdateBookRequest request) {
        final Result<UpdateBookResponse> result = updateBookUseCase.execute(bookId, request);

        if (result.isSuccess()) {
            final UpdateBookResponse response = result.payload().orElseThrow();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", response.message(),
                    "book", Map.of(
                            "bookId", response.bookId(),
                            "bookType", response.bookType()
                    )
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", result.errorMessage().orElse("Không thể cập nhật sách")
        ));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable final String bookId) {
        final Result<String> result = deleteBookUseCase.execute(bookId);

        if (result.isSuccess()) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", result.payload().orElse("Đã xóa sách thành công")
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", result.errorMessage().orElse("Không thể xóa sách")
        ));
    }

    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        final Result<List<BookResponse>> result = getAllBooksUseCase.execute();

        if (result.isSuccess()) {
            final List<BookResponse> books = result.payload().orElseThrow();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "total", books.size(),
                    "books", books
            ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", result.errorMessage().orElse("Không thể lấy danh sách sách")
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam final String keyword) {
        final Result<List<BookResponse>> result = searchBooksUseCase.execute(keyword);

        if (result.isSuccess()) {
            final List<BookResponse> books = result.payload().orElseThrow();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "total", books.size(),
                    "keyword", keyword,
                    "books", books
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", result.errorMessage().orElse("Không thể tìm kiếm sách")
        ));
    }

    @GetMapping("/statistics/total-by-type")
    public ResponseEntity<?> calculateTotalByType() {
        final Result<TotalByTypeResponse> result = calculateTotalByTypeUseCase.execute();

        if (result.isSuccess()) {
            final TotalByTypeResponse response = result.payload().orElseThrow();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "tongThanhTienSachGiaoKhoa", response.tongThanhTienSachGiaoKhoa(),
                    "tongThanhTienSachThamKhao", response.tongThanhTienSachThamKhao(),
                    "tongThanhTienTatCa", response.tongThanhTienTatCa()
            ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", result.errorMessage().orElse("Không thể tính tổng thành tiền")
        ));
    }

    @GetMapping("/statistics/average-price")
    public ResponseEntity<?> calculateAveragePrice() {
        final Result<AveragePriceResponse> result = calculateAveragePriceUseCase.execute();

        if (result.isSuccess()) {
            final AveragePriceResponse response = result.payload().orElseThrow();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "trungBinhCongDonGia", response.trungBinhCongDonGia(),
                    "soLuongSachThamKhao", response.soLuongSachThamKhao()
            ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", result.errorMessage().orElse("Không thể tính trung bình cộng đơn giá")
        ));
    }

    @GetMapping("/publisher/{publisher}")
    public ResponseEntity<?> getBooksByPublisher(@PathVariable final String publisher) {
        final Result<List<BookResponse>> result = getBooksByPublisherUseCase.execute(publisher);

        if (result.isSuccess()) {
            final List<BookResponse> books = result.payload().orElseThrow();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "publisher", publisher,
                    "total", books.size(),
                    "books", books
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", result.errorMessage().orElse("Không thể lấy sách theo nhà xuất bản")
        ));
    }
}

