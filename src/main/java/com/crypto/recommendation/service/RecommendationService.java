package com.crypto.recommendation.service;

import com.crypto.recommendation.exceptions.CryptoNotSupportedException;
import com.crypto.recommendation.exceptions.NoCryptoDataForSpecificDateException;
import com.crypto.recommendation.model.Crypto;
import com.crypto.recommendation.model.CryptoStats;
import com.fasterxml.jackson.databind.MappingIterator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {

    private static final Map<String, List<Crypto>> allCryptos = new HashMap<>();

    private static final Map<String, CryptoStats> cryptoStatsMap = new HashMap<>();

    @Value("#{'${crypto.supported_list}'.split(',')}")
    private List<String> supportedCryptos;

    private final ResourceLoader resourceLoader;

    private final CsvMapper csvMapper = new CsvMapper();

    /**
     * Load all supported Cryptos from CSV files
     * @throws IOException
     */
    @PostConstruct
    @SneakyThrows
    public void init() {
        for(String key : supportedCryptos) {
            MappingIterator<Crypto> cryptoMappingIterator = csvMapper.readerWithTypedSchemaFor(Crypto.class)
                    .readValues(
                            resourceLoader.getResource("classpath:assets/crypto-prices/%s_values.csv"
                                    .formatted(key.toUpperCase())).getInputStream()
                    );
            List<Crypto> cryptoList = cryptoMappingIterator.readAll();
            cryptoList.remove(0); // removing headers part of csv files
            allCryptos.put(key.toUpperCase(), cryptoList);
            cryptoStatsMap.put(key.toUpperCase(), calculateCryptoStats(cryptoList));
        }
    }

    private CryptoStats calculateCryptoStats(List<Crypto> cryptoList) {
        if(cryptoList.isEmpty()){
            return null;
        }
        CryptoStats cryptoStats = new CryptoStats();
        cryptoStats.setMax(calculateMaxPrice(cryptoList));
        cryptoStats.setMin(calculateMinPrice(cryptoList));
        cryptoStats.setOldest(Double.parseDouble(cryptoList.get(0).getPrice()));
        cryptoStats.setNewest(Double.parseDouble(cryptoList.get(cryptoList.size() -1).getPrice()));
        return cryptoStats;
    }

    public CryptoStats specificCryptoStats(String crypto){
        checkIfCryptoIsSupported(crypto);
        return cryptoStatsMap.get(crypto.toUpperCase());
    }

    public List<String> normalizedCryptosDescending(){
        return calculateNormalizedCryptosRangeDescending(cryptoStatsMap);
    }

    public String cryptoWithHighestNormalizedRangeByDay(LocalDate day){
        HashMap<String, List<Crypto>> cryptoMapByDay = getCryptoMapByDay(day);
        Map<String, CryptoStats> cryptoStatsMapByDay = getCryptoStatsMapByDay(cryptoMapByDay);
        List<String> cryptoListDsc = calculateNormalizedCryptosRangeDescending(cryptoStatsMapByDay);
        if(cryptoListDsc.isEmpty()) {
            log.error("No crypto data for specified date: {}", day);
            throw new NoCryptoDataForSpecificDateException(day);
        }
        return cryptoListDsc.get(0);
    }

    private Map<String, CryptoStats> getCryptoStatsMapByDay(HashMap<String, List<Crypto>> cryptoMapByDay) {
        Map<String, CryptoStats> cryptoStatsMapByDay = new HashMap<>();
        cryptoMapByDay.forEach((key, value) -> cryptoStatsMapByDay.put(key, calculateCryptoStats(value)));
        return cryptoStatsMapByDay;
    }

    private List<String> calculateNormalizedCryptosRangeDescending(Map<String, CryptoStats> cryptoMap) {
        return cryptoMap.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .sorted(Map.Entry.comparingByValue(compareByNormalizedRange().reversed()))
                .map(Map.Entry::getKey).toList();
    }

    private HashMap<String,List<Crypto>> getCryptoMapByDay(LocalDate day) {
        long timestampStart = day.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long timestampEnd = day.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

        HashMap<String,List<Crypto>> mapByDay = new HashMap<>();

        allCryptos.forEach((key, value) -> {
            List<Crypto> filteredList = value.stream().filter(e -> {
                long timestamp = Long.parseLong(e.getTimestamp());
                return timestamp >= timestampStart && timestamp <= timestampEnd;
            }).toList();
            mapByDay.put(key, filteredList);
        });

        return mapByDay;
    }

    private static void checkIfCryptoIsSupported(String crypto) {
        List<Crypto> cryptoList = allCryptos.get(crypto.toUpperCase());
        if(cryptoList == null) {
            log.error("Crypto {} not supported", crypto);
            throw new CryptoNotSupportedException(crypto);
        }
    }

    private Comparator<CryptoStats> compareByNormalizedRange() {
        return Comparator
                .comparingDouble((CryptoStats obj) -> ((obj.getMax() - obj.getMin()) / obj.getMin()));
    }

    private double calculateMaxPrice(List<Crypto> cryptoList) {
        return cryptoList.stream().mapToDouble(v -> Double.parseDouble(v.getPrice())).max().orElse(-1);
    }

    private double calculateMinPrice(List<Crypto> cryptoList) {
        return cryptoList.stream().mapToDouble(v -> Double.parseDouble(v.getPrice())).min().orElse(-1);
    }

    public Map<String, List<Crypto>> getAllCryptosMap() {
        return allCryptos;
    }

    public Map<String, CryptoStats> getCryptoStatsMap() {
        return cryptoStatsMap;
    }
}
