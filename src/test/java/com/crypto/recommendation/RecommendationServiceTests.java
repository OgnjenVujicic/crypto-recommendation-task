package com.crypto.recommendation;

import com.crypto.recommendation.exceptions.CryptoNotSupportedException;
import com.crypto.recommendation.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
class RecommendationServiceTests {

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private RecommendationService recommendationService;

//    @BeforeEach
//    public void setUp() {
//        ReflectionTestUtils.setField(recommendationService, "supportedCryptos", Arrays.asList("btc", "eth"));
//    }

    @Test
    void testCsvLoadData() {
        assertThat(recommendationService.getCryptoStatsMap()).isNotEmpty();
        assertThat(recommendationService.getAllCryptosMap()).isNotEmpty();
    }

    @Test
    void testSpecificCryptoStats() {
        CryptoNotSupportedException e = assertThrows(CryptoNotSupportedException.class, () -> recommendationService.specificCryptoStats("abb"));
        assertEquals("Crypto 'abb' is not supported.", e.getMessage());
    }

    //        String a = cryptoWithHighestNormalizedRangeByDay(LocalDate.of(2022,4,3));

}
