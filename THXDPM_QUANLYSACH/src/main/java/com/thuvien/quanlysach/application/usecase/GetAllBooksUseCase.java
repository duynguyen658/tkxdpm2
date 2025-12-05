package com.thuvien.quanlysach.application.usecase;

import com.thuvien.quanlysach.domain.Result;
import java.util.List;

public interface GetAllBooksUseCase {
    Result<List<BookResponse>> execute();
}

