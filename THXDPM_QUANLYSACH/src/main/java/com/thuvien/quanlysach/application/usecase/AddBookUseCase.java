package com.thuvien.quanlysach.application.usecase;

import com.thuvien.quanlysach.domain.Result;

public interface AddBookUseCase {
    Result<AddBookResponse> execute(AddBookRequest request);
}

