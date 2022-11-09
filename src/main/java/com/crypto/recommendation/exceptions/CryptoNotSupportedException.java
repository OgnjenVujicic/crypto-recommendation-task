package com.crypto.recommendation.exceptions;

public class CryptoNotSupportedException extends RuntimeException {
    private final String crypto;

    public CryptoNotSupportedException(String crypto)
    {
        this.crypto = crypto;
    }

    @Override
    public String getMessage()
    {
        return String.format("Crypto '%s' is not supported.", this.crypto);
    }
}
