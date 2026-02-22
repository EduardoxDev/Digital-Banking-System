package com.bank.digital.domain.exception;

public abstract class BankingException extends RuntimeException {
    public BankingException(String message) {
        super(message);
    }
}
