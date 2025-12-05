package com.thuvien.quanlysach.application.config;

import com.thuvien.quanlysach.application.port.output.BookRepository;
import com.thuvien.quanlysach.application.service.AddBookService;
import com.thuvien.quanlysach.application.service.CalculateAveragePriceService;
import com.thuvien.quanlysach.application.service.CalculateTotalByTypeService;
import com.thuvien.quanlysach.application.service.DeleteBookService;
import com.thuvien.quanlysach.application.service.GetAllBooksService;
import com.thuvien.quanlysach.application.service.GetBooksByPublisherService;
import com.thuvien.quanlysach.application.service.SearchBooksService;
import com.thuvien.quanlysach.application.service.UpdateBookService;
import com.thuvien.quanlysach.application.usecase.AddBookUseCase;
import com.thuvien.quanlysach.application.usecase.CalculateAveragePriceUseCase;
import com.thuvien.quanlysach.application.usecase.CalculateTotalByTypeUseCase;
import com.thuvien.quanlysach.application.usecase.DeleteBookUseCase;
import com.thuvien.quanlysach.application.usecase.GetAllBooksUseCase;
import com.thuvien.quanlysach.application.usecase.GetBooksByPublisherUseCase;
import com.thuvien.quanlysach.application.usecase.SearchBooksUseCase;
import com.thuvien.quanlysach.application.usecase.UpdateBookUseCase;
import com.thuvien.quanlysach.infrastructure.repository.InMemoryBookRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public BookRepository bookRepository() {
        return new InMemoryBookRepository();
    }

    @Bean
    public AddBookUseCase addBookUseCase(final BookRepository bookRepository) {
        return new AddBookService(bookRepository);
    }

    @Bean
    public UpdateBookUseCase updateBookUseCase(final BookRepository bookRepository) {
        return new UpdateBookService(bookRepository);
    }

    @Bean
    public DeleteBookUseCase deleteBookUseCase(final BookRepository bookRepository) {
        return new DeleteBookService(bookRepository);
    }

    @Bean
    public GetAllBooksUseCase getAllBooksUseCase(final BookRepository bookRepository) {
        return new GetAllBooksService(bookRepository);
    }

    @Bean
    public SearchBooksUseCase searchBooksUseCase(final BookRepository bookRepository) {
        return new SearchBooksService(bookRepository);
    }

    @Bean
    public CalculateTotalByTypeUseCase calculateTotalByTypeUseCase(final BookRepository bookRepository) {
        return new CalculateTotalByTypeService(bookRepository);
    }

    @Bean
    public CalculateAveragePriceUseCase calculateAveragePriceUseCase(final BookRepository bookRepository) {
        return new CalculateAveragePriceService(bookRepository);
    }

    @Bean
    public GetBooksByPublisherUseCase getBooksByPublisherUseCase(final BookRepository bookRepository) {
        return new GetBooksByPublisherService(bookRepository);
    }
}

