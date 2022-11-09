package com.crypto.recommendation.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({ "timestamp", "symbol", "price" })
public class Crypto {
    private String symbol;
    private String price;
    private String timestamp;
}
