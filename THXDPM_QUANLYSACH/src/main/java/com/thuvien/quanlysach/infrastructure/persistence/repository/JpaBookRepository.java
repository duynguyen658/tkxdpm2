package com.thuvien.quanlysach.infrastructure.persistence.repository;

import com.thuvien.quanlysach.infrastructure.persistence.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository interface cho BookEntity
 */
@Repository
public interface JpaBookRepository extends JpaRepository<BookEntity, String> {
    
    /**
     * Tìm kiếm sách theo nhà xuất bản (chỉ sách giáo khoa)
     */
    List<BookEntity> findByNhaXuatBanAndBookType(
            String nhaXuatBan, 
            BookEntity.BookType bookType
    );
    
    /**
     * Tìm kiếm sách theo keyword (mã sách, nhà xuất bản)
     */
    List<BookEntity> findByIdContainingIgnoreCaseOrNhaXuatBanContainingIgnoreCase(
            String id, 
            String nhaXuatBan
    );
}

