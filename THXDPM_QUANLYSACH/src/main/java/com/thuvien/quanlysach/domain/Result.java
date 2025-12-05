package com.thuvien.quanlysach.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable result wrapper that keeps domain responses explicit.
 *
 * @param <T> successful payload type
 */
public final class Result<T> {
    private final boolean success;
    private final T payload;
    private final String errorMessage;

    private Result(final boolean success, final T payload, final String errorMessage) {
        this.success = success;
        this.payload = payload;
        this.errorMessage = errorMessage;
    }

    public static <T> Result<T> ok(final T payload) {
        return new Result<>(true, Objects.requireNonNull(payload, "payload"), null);
    }

    public static <T> Result<T> fail(final String errorMessage) {
        return new Result<>(false, null, Objects.requireNonNull(errorMessage, "errorMessage"));
    }

    public boolean isSuccess() {
        return success;
    }

    public Optional<T> payload() {
        return Optional.ofNullable(payload);
    }

    public Optional<String> errorMessage() {
        return Optional.ofNullable(errorMessage);
    }
}

