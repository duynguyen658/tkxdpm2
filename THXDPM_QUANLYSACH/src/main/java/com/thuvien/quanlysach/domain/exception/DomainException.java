package com.thuvien.quanlysach.domain.exception;

public class DomainException extends Exception {
    public DomainException(final String message) {
        super(message);
    }

    public DomainException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

