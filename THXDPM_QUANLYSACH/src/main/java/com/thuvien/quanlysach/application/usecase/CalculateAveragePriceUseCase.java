package com.thuvien.quanlysach.application.usecase;

import com.thuvien.quanlysach.domain.Result;

public interface CalculateAveragePriceUseCase {
    Result<AveragePriceResponse> execute();
}

