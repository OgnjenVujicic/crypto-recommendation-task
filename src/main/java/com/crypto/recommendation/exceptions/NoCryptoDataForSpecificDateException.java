package com.crypto.recommendation.exceptions;

import java.time.LocalDate;

public class NoCryptoDataForSpecificDateException extends RuntimeException {
    private final LocalDate date;

    public NoCryptoDataForSpecificDateException(LocalDate date)
    {
        this.date = date;
    }

    @Override
    public String getMessage()
    {
        return String.format("There is no crypto data for specified date: '%s'", this.date);
    }
}
