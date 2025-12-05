package com.thuvien.quanlysach.application.usecase;

import com.thuvien.quanlysach.domain.Result;

public interface DeleteBookUseCase {
    Result<String> execute(String bookId);
}

