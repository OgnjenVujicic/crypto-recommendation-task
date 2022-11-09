package com.crypto.recommendation;

import com.crypto.recommendation.exceptions.CryptoNotSupportedException;
import com.crypto.recommendation.exceptions.NoCryptoDataForSpecificDateException;
import com.crypto.recommendation.model.CryptoStats;
import com.crypto.recommendation.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
class RecommendationServiceTests {

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void testCsvLoadInitData() {
        assertThat(recommendationService.getCryptoStatsMap()).isNotEmpty();
        assertThat(recommendationService.getAllCryptosMap()).isNotEmpty();
    }

    @Test
    void testGetSpecificCryptoStats() {
        CryptoStats specificCryptoStats = recommendationService.getSpecificCryptoStats("btc");

        assertThat(specificCryptoStats.getMin()).isEqualTo(33276.59);
        assertThat(specificCryptoStats.getMax()).isEqualTo(47722.66);
    }

    @Test
    void testGetSpecificCryptoStats_exceptionThrown() {
        CryptoNotSupportedException e = assertThrows(CryptoNotSupportedException.class,
                () -> recommendationService.getSpecificCryptoStats("abb"));
        assertEquals("Crypto 'abb' is not supported.", e.getMessage());
    }

    @Test
    void testGetNormalizedCryptosListDescending() {
        List<String> normalizedCryptosListDescending = recommendationService.getNormalizedCryptosListDescending();
        assertThat(normalizedCryptosListDescending).isNotNull().isNotEmpty();
        assertThat(normalizedCryptosListDescending.get(0)).isEqualTo("ETH");
    }

    @Test
    void testCryptoWithHighestNormalizedRangeByDay() {
        LocalDate date1 = LocalDate.of(2022,1,3);
        String crypto1 = recommendationService.cryptoWithHighestNormalizedRangeByDay(date1);
        assertThat(crypto1).isNotNull().isNotEmpty().isEqualTo("ETH");

        LocalDate date2 = LocalDate.of(2022,1,7);
        String crypto2 = recommendationService.cryptoWithHighestNormalizedRangeByDay(date2);
        assertThat(crypto2).isNotNull().isNotEmpty().isEqualTo("DOGE");
    }

    @Test
    void testCryptoWithHighestNormalizedRangeByDay_exceptionThrown() {
        LocalDate dateWithNoData = LocalDate.of(2022,4,3);
        assertThrows(NoCryptoDataForSpecificDateException.class,
                () -> recommendationService.cryptoWithHighestNormalizedRangeByDay(dateWithNoData));
    }

}
