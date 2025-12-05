package com.thuvien.quanlysach.application.usecase;

import com.thuvien.quanlysach.domain.Result;
import java.util.List;

public interface GetBooksByPublisherUseCase {
    Result<List<BookResponse>> execute(String publisher);
}

