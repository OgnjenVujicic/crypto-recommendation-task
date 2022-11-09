package com.crypto.recommendation.controller;

import com.crypto.recommendation.exceptions.CryptoNotSupportedException;
import com.crypto.recommendation.exceptions.NoCryptoDataForSpecificDateException;
import com.crypto.recommendation.model.CryptoStats;
import com.crypto.recommendation.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "Get list of all Cryptos sorted by Normalized Range Descending.")
    @GetMapping("normalizedCryptosDescending")
    public ResponseEntity<List<String>> normalizedCryptosDescending(){
        return ResponseEntity.ok(recommendationService.normalizedCryptosDescending());
    }

    @Operation(summary = "Get Stats (oldest/newest/min/max values) for specific Crypto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CryptoStats.class)) }),
            @ApiResponse(responseCode = "404", description = "Crypto Not Found/Supported",
                    content = @Content) })
    @GetMapping("cryptoStats/{crypto}")
    public ResponseEntity<CryptoStats> specificCryptoStats(@PathVariable String crypto){
        return ResponseEntity.ok(recommendationService.specificCryptoStats(crypto));
    }

    @Operation(summary = "Get Crypto with highest Normalized Range for specific day.")
    @GetMapping("highestCryptoNormalizedRange/byDay/{date}")
    public ResponseEntity<String> highestCryptoNormalizedRangeByDay(@Parameter(description = "ISO date format: yyyy-MM-dd")
                                                                    @PathVariable
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                    LocalDate date){
        return ResponseEntity.ok(recommendationService.cryptoWithHighestNormalizedRangeByDay(date));
    }

    @ExceptionHandler(CryptoNotSupportedException.class)
    public ResponseEntity<String> handleCryptoNotSupportedException(
            CryptoNotSupportedException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(NoCryptoDataForSpecificDateException.class)
    public ResponseEntity<String> handleNoCryptoDataForSpecificDateException(
            NoCryptoDataForSpecificDateException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

}
