package com.thuvien.quanlysach.infrastructure.repository;

import com.thuvien.quanlysach.application.port.output.BookRepository;
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
import com.thuvien.quanlysach.infrastructure.persistence.entity.BookEntity;
import com.thuvien.quanlysach.infrastructure.persistence.repository.JpaBookRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter để chuyển đổi giữa JPA Repository và Domain Repository
 * Map giữa BookEntity (JPA) và Book (Domain Entity)
 */
@Component
public class JpaBookRepositoryAdapter implements BookRepository {
    
    private final JpaBookRepository jpaBookRepository;
    
    public JpaBookRepositoryAdapter(final JpaBookRepository jpaBookRepository) {
        this.jpaBookRepository = jpaBookRepository;
    }
    
    @Override
    public Optional<Book> findById(final BookId bookId) {
        return jpaBookRepository.findById(bookId.value())
                .map(this::toDomainEntity);
    }
    
    @Override
    public List<Book> findAll() {
        return jpaBookRepository.findAll().stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public Book save(final Book book) {
        final BookEntity entity = toJpaEntity(book);
        final BookEntity savedEntity = jpaBookRepository.save(entity);
        return toDomainEntity(savedEntity);
    }
    
    @Override
    public void delete(final BookId bookId) {
        jpaBookRepository.deleteById(bookId.value());
    }
    
    @Override
    public boolean existsById(final BookId bookId) {
        return jpaBookRepository.existsById(bookId.value());
    }
    
    /**
     * Chuyển đổi từ JPA Entity sang Domain Entity
     */
    private Book toDomainEntity(final BookEntity entity) {
        final BookId bookId = BookId.of(entity.getId());
        final ImportDate importDate = ImportDate.of(
                entity.getNgayNhap().getDayOfMonth(),
                entity.getNgayNhap().getMonthValue(),
                entity.getNgayNhap().getYear()
        );
        final Price unitPrice = Price.of(entity.getDonGia());
        final Quantity quantity = Quantity.of(entity.getSoLuong());
        final Publisher publisher = Publisher.of(entity.getNhaXuatBan());
        
        if (entity.getBookType() == BookEntity.BookType.SACH_GIAO_KHOA) {
            final BookStatus status = entity.getTinhTrang() == BookEntity.BookStatus.MOI
                    ? BookStatus.MOI
                    : BookStatus.CU;
            return SachGiaoKhoa.create(bookId, importDate, unitPrice, quantity, publisher, status);
        } else {
            final Tax tax = Tax.of(entity.getThue());
            return SachThamKhao.create(bookId, importDate, unitPrice, quantity, publisher, tax);
        }
    }
    
    /**
     * Chuyển đổi từ Domain Entity sang JPA Entity
     */
    private BookEntity toJpaEntity(final Book book) {
        final BookEntity entity = new BookEntity();
        entity.setId(book.id().value());
        entity.setNgayNhap(book.importDate().date());
        entity.setDonGia(book.unitPrice().value());
        entity.setSoLuong(book.quantity().value());
        entity.setNhaXuatBan(book.publisher().value());
        
        if (book instanceof SachGiaoKhoa sachGiaoKhoa) {
            entity.setBookType(BookEntity.BookType.SACH_GIAO_KHOA);
            entity.setTinhTrang(sachGiaoKhoa.status() == BookStatus.MOI
                    ? BookEntity.BookStatus.MOI
                    : BookEntity.BookStatus.CU);
            entity.setThue(null);
        } else if (book instanceof SachThamKhao sachThamKhao) {
            entity.setBookType(BookEntity.BookType.SACH_THAM_KHAO);
            entity.setTinhTrang(null);
            entity.setThue(sachThamKhao.tax().value());
        }
        
        return entity;
    }
}

