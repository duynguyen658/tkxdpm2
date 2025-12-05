package com.thuvien.quanlysach.application.usecase;

import com.thuvien.quanlysach.domain.Result;

public interface UpdateBookUseCase {
    Result<UpdateBookResponse> execute(String bookId, UpdateBookRequest request);
}

