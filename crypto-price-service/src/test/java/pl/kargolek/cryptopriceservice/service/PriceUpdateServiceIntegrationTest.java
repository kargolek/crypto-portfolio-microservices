package pl.kargolek.cryptopriceservice.service;

import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.kargolek.cryptopriceservice.exception.MarketApiClientException;
import pl.kargolek.cryptopriceservice.extension.MockWebServerExtension;
import pl.kargolek.cryptopriceservice.extension.MySqlTestContainerExtension;
import pl.kargolek.cryptopriceservice.model.Cryptocurrency;
import pl.kargolek.cryptopriceservice.model.Price;
import pl.kargolek.cryptopriceservice.repository.CryptocurrencyRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static pl.kargolek.cryptopriceservice.extension.MockWebServerExtension.mockWebServer;

/**
 * @author Karol Kuta-Orlowicz
 */

@ExtendWith(MySqlTestContainerExtension.class)
@ExtendWith(MockWebServerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("IntegrationTest")
@ActiveProfiles("test")
class PriceUpdateServiceIntegrationTest {

    private static final BigDecimal BTC_RES_PRICE = new BigDecimal("25000.12345").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_RES_PERCENT_1H = new BigDecimal("1.54321").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_RES_PERCENT_24H = new BigDecimal("-2.54321").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_RES_PERCENT_7D = new BigDecimal("3.54321").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_RES_PERCENT_30D = new BigDecimal("-4.54321").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_RES_PERCENT_60D = new BigDecimal("5.54321").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_RES_PERCENT_90D = new BigDecimal("-6.54321").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_RES_PRICE = new BigDecimal("2543.304022840702").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_1H = new BigDecimal("7.54321").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_24H = new BigDecimal("-8.54321").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_7D = new BigDecimal("9.54321").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_30D = new BigDecimal("-10.54321").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_60D = new BigDecimal("11.54321").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_90D = new BigDecimal("-12.54321").setScale(12, RoundingMode.HALF_UP);
    @Autowired
    private PriceUpdateService underTest;

    @Autowired
    CryptocurrencyService cryptocurrencyService;

    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;
    private Price priceBTC;
    private Price priceETH;

    @DynamicPropertySource
    static void registerProperty(DynamicPropertyRegistry registry) {
        registry.add("api.coin.market.cap.baseUrl", () -> mockWebServer.url("/").toString());
    }

    @BeforeEach
    void setUp() {

        priceBTC = Price.builder()
                .priceCurrent(new BigDecimal("20000.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange1h(new BigDecimal("0.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange24h(new BigDecimal("1.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange7d(new BigDecimal("2.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange30d(new BigDecimal("3.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange60d(new BigDecimal("4.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange90d(new BigDecimal("5.5").setScale(12, RoundingMode.HALF_UP))
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        Cryptocurrency cryptocurrencyBTC = Cryptocurrency.builder()
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(1L)
                .price(priceBTC)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        priceBTC.setCryptocurrency(cryptocurrencyBTC);

        priceETH = Price.builder()
                .priceCurrent(new BigDecimal("1800.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange1h(new BigDecimal("0.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange24h(new BigDecimal("1.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange7d(new BigDecimal("2.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange30d(new BigDecimal("3.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange60d(new BigDecimal("4.5").setScale(12, RoundingMode.HALF_UP))
                .percentChange90d(new BigDecimal("5.5").setScale(12, RoundingMode.HALF_UP))
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        Cryptocurrency cryptocurrencyETH = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1027L)
                .price(priceETH)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        priceETH.setCryptocurrency(cryptocurrencyETH);

        cryptocurrencyRepository.save(cryptocurrencyBTC);
        cryptocurrencyRepository.save(cryptocurrencyETH);
    }

    @AfterEach
    void tearDown() {
        cryptocurrencyRepository.deleteAll();
    }

    @Test
    void whenReceiveOneCryptoPriceUpdate_thenPricesShouldBeUpdatedForOneEntity() {
        var bodyRes = """
                {
                "data": {
                "1": {
                "id": 1,
                "name": "Bitcoin",
                "symbol": "BTC",
                "slug": "bitcoin",
                "is_active": 1,
                "is_fiat": 0,
                "circulating_supply": 17199862,
                "total_supply": 17199862,
                "max_supply": 21000000,
                "date_added": "2013-04-28T00:00:00.000Z",
                "num_market_pairs": 331,
                "cmc_rank": 1,
                "last_updated": "2018-08-09T21:56:28.000Z",
                "tags": [
                "mineable"
                ],
                "platform": null,
                "self_reported_circulating_supply": null,
                "self_reported_market_cap": null,
                "quote": {
                "USD": {
                "price": 25000.12345,
                "volume_24h": 4314444687.5194,
                "volume_change_24h": -0.152774,
                "percent_change_1h": 1.54321,
                "percent_change_24h": -2.54321,
                "percent_change_7d": 3.54321,
                "percent_change_30d": -4.54321,
                "percent_change_60d": 5.54321,
                "percent_change_90d": -6.54321,
                "market_cap": 852164659250.2758,
                "market_cap_dominance": 51,
                "fully_diluted_market_cap": 952835089431.14,
                "last_updated": "2018-08-09T21:56:28.000Z"
                }
                }
                }
                },
                "status": {
                "timestamp": "2023-01-03T19:55:35.426Z",
                "error_code": 0,
                "error_message": "",
                "elapsed": 10,
                "credit_count": 1
                }
                }""";
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        underTest.updateCryptocurrencyPrices();

        assertThat(cryptocurrencyRepository.findAll()
                .stream().map(Cryptocurrency::getPrice).toList()).extracting(
                Price::getId,
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d,
                Price::getPercentChange30d,
                Price::getPercentChange60d,
                Price::getPercentChange90d
        ).containsExactly(
                tuple(
                        priceBTC.getId(),
                        BTC_RES_PRICE,
                        BTC_RES_PERCENT_1H,
                        BTC_RES_PERCENT_24H,
                        BTC_RES_PERCENT_7D,
                        BTC_RES_PERCENT_30D,
                        BTC_RES_PERCENT_60D,
                        BTC_RES_PERCENT_90D

                ),
                tuple(
                        priceETH.getId(),
                        priceETH.getPriceCurrent(),
                        priceETH.getPercentChange1h(),
                        priceETH.getPercentChange24h(),
                        priceETH.getPercentChange7d(),
                        priceETH.getPercentChange30d(),
                        priceETH.getPercentChange60d(),
                        priceETH.getPercentChange90d()
                )
        );
    }

    @Test
    void whenReceiveTwoCryptoPriceUpdate_thenPricesShouldBeUpdatedForTwoEntities() {
        var bodyRes = """
                {
                    "status": {
                        "timestamp": "2023-01-16T09:23:08.500Z",
                        "error_code": 0,
                        "error_message": null,
                        "elapsed": 40,
                        "credit_count": 1,
                        "notice": null
                    },
                    "data": {
                        "1": {
                            "id": 1,
                            "name": "Bitcoin",
                            "symbol": "BTC",
                            "slug": "bitcoin",
                            "num_market_pairs": 9931,
                            "date_added": "2013-04-28T00:00:00.000Z",
                            "max_supply": 21000000,
                            "circulating_supply": 19263793,
                            "total_supply": 19263793,
                            "is_active": 1,
                            "platform": null,
                            "cmc_rank": 1,
                            "is_fiat": 0,
                            "self_reported_circulating_supply": null,
                            "self_reported_market_cap": null,
                            "tvl_ratio": null,
                            "last_updated": "2023-01-16T09:21:00.000Z",
                            "quote": {
                                "USD": {
                                    "price": 25000.12345,
                                    "volume_24h": 4314444687.5194,
                                    "volume_change_24h": -0.152774,
                                    "percent_change_1h": 1.54321,
                                    "percent_change_24h": -2.54321,
                                    "percent_change_7d": 3.54321,
                                    "percent_change_30d": -4.54321,
                                    "percent_change_60d": 5.54321,
                                    "percent_change_90d": -6.54321,
                                    "market_cap": 400915038365.6269,
                                    "market_cap_dominance": 40.9945,
                                    "fully_diluted_market_cap": 437048706123.36,
                                    "tvl": null,
                                    "last_updated": "2023-01-16T09:21:00.000Z"
                                }
                            }
                        },
                        "1027": {
                                    "id": 1027,
                                    "name": "Ethereum",
                                    "symbol": "ETH",
                                    "slug": "ethereum",
                                    "num_market_pairs": 6360,
                                    "date_added": "2015-08-07T00:00:00.000Z",
                                    "max_supply": null,
                                    "circulating_supply": 122373866.2178,
                                    "total_supply": 122373866.2178,
                                    "is_active": 1,
                                    "platform": null,
                                    "cmc_rank": 2,
                                    "is_fiat": 0,
                                    "self_reported_circulating_supply": null,
                                    "self_reported_market_cap": null,
                                    "tvl_ratio": null,
                                    "last_updated": "2023-01-16T09:35:00.000Z",
                                    "quote": {
                                        "USD": {
                                            "price": 2543.3040228407024,
                                            "volume_24h": 7685876434.055611,
                                            "volume_change_24h": -6.5169,
                                            "percent_change_1h": 7.54321,
                                            "percent_change_24h": -8.54321,
                                            "percent_change_7d": 9.54321,
                                            "percent_change_30d": -10.54321,
                                            "percent_change_60d": 11.54321,
                                            "percent_change_90d": -12.54321,
                                            "market_cap": 188860080024.50067,
                                            "market_cap_dominance": 19.2937,
                                            "fully_diluted_market_cap": 188860080024.5,
                                            "tvl": null,
                                            "last_updated": "2023-01-16T09:35:00.000Z"
                                        }
                                    }
                                }
                    }
                }
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        underTest.updateCryptocurrencyPrices();

        var prices = cryptocurrencyRepository.findAll()
                .stream()
                .map(Cryptocurrency::getPrice)
                .toList();

        assertThat(prices).extracting(
                Price::getId,
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d,
                Price::getPercentChange30d,
                Price::getPercentChange60d,
                Price::getPercentChange90d
        ).containsExactly(
                tuple(
                        priceBTC.getId(),
                        BTC_RES_PRICE,
                        BTC_RES_PERCENT_1H,
                        BTC_RES_PERCENT_24H,
                        BTC_RES_PERCENT_7D,
                        BTC_RES_PERCENT_30D,
                        BTC_RES_PERCENT_60D,
                        BTC_RES_PERCENT_90D
                ),
                tuple(
                        priceETH.getId(),
                        ETH_RES_PRICE,
                        ETH_PERCENT_1H,
                        ETH_PERCENT_24H,
                        ETH_PERCENT_7D,
                        ETH_PERCENT_30D,
                        ETH_PERCENT_60D,
                        ETH_PERCENT_90D
                )
        );
    }

    @Test
    void whenReceiveNoCryptoPriceUpdate_thenPriceShouldNotBeUpdate() {
        var bodyRes = """
                {
                    "status": {
                        "timestamp": "2023-01-16T09:23:08.500Z",
                        "error_code": 0,
                        "error_message": null,
                        "elapsed": 40,
                        "credit_count": 1,
                        "notice": null
                    }
                }
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        underTest.updateCryptocurrencyPrices();

        assertThat(cryptocurrencyRepository.findAll()
                .stream().map(Cryptocurrency::getPrice).toList()).extracting(
                Price::getId,
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d,
                Price::getPercentChange30d,
                Price::getPercentChange60d,
                Price::getPercentChange90d
        ).containsExactly(
                tuple(
                        priceBTC.getId(),
                        priceBTC.getPriceCurrent(),
                        priceBTC.getPercentChange1h(),
                        priceBTC.getPercentChange24h(),
                        priceBTC.getPercentChange7d(),
                        priceBTC.getPercentChange30d(),
                        priceBTC.getPercentChange60d(),
                        priceBTC.getPercentChange90d()
                ),
                tuple(
                        priceETH.getId(),
                        priceETH.getPriceCurrent(),
                        priceETH.getPercentChange1h(),
                        priceETH.getPercentChange24h(),
                        priceETH.getPercentChange7d(),
                        priceETH.getPercentChange30d(),
                        priceETH.getPercentChange60d(),
                        priceETH.getPercentChange90d()
                )
        );
    }

    @Test
    void whenReceiveResponse500_thenPriceShouldNotBeUpdate() {
        var bodyRes = """
                {
                "status": {
                "timestamp": "2018-06-02T22:51:28.209Z",
                "error_code": 500,
                "error_message": "An internal server error occurred",
                "elapsed": 10,
                "credit_count": 0
                }
                }""";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        assertThatThrownBy(() -> underTest.updateCryptocurrencyPrices())
                .isInstanceOf(MarketApiClientException.class);

        assertThat(cryptocurrencyRepository.findAll()
                .stream().map(Cryptocurrency::getPrice).toList()).extracting(
                Price::getId,
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d,
                Price::getPercentChange30d,
                Price::getPercentChange60d,
                Price::getPercentChange90d
        ).containsExactly(
                tuple(
                        priceBTC.getId(),
                        priceBTC.getPriceCurrent(),
                        priceBTC.getPercentChange1h(),
                        priceBTC.getPercentChange24h(),
                        priceBTC.getPercentChange7d(),
                        priceBTC.getPercentChange30d(),
                        priceBTC.getPercentChange60d(),
                        priceBTC.getPercentChange90d()
                ),
                tuple(
                        priceETH.getId(),
                        priceETH.getPriceCurrent(),
                        priceETH.getPercentChange1h(),
                        priceETH.getPercentChange24h(),
                        priceETH.getPercentChange7d(),
                        priceETH.getPercentChange30d(),
                        priceETH.getPercentChange60d(),
                        priceETH.getPercentChange90d()
                )
        );
    }

    @Test
    void whenCryptocurrencyRepoEmpty_thenReturnEmptyList() {
        cryptocurrencyRepository.deleteAll();
        var expected = underTest.updateCryptocurrencyPrices();
        assertThat(expected).hasSize(0);
    }

    @Test
    void whenResponse200PriceDataNull_thenNotUpdatePricesEntities() {
        var bodyRes = """
                {
                    "status": {
                        "timestamp": "2023-01-22T19:04:55.091Z",
                        "error_code": 0,
                        "error_message": null,
                        "elapsed": 97,
                        "credit_count": 1,
                        "notice": null
                    },
                    "data": {
                        "1": {
                        }
                    }
                }
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        var expected = underTest.updateCryptocurrencyPrices();

        assertThat(expected).extracting(
                Price::getId,
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d,
                Price::getPercentChange30d,
                Price::getPercentChange60d,
                Price::getPercentChange90d
        ).containsExactly(
                tuple(
                        priceBTC.getId(),
                        priceBTC.getPriceCurrent(),
                        priceBTC.getPercentChange1h(),
                        priceBTC.getPercentChange24h(),
                        priceBTC.getPercentChange7d(),
                        priceBTC.getPercentChange30d(),
                        priceBTC.getPercentChange60d(),
                        priceBTC.getPercentChange90d()
                ),
                tuple(
                        priceETH.getId(),
                        priceETH.getPriceCurrent(),
                        priceETH.getPercentChange1h(),
                        priceETH.getPercentChange24h(),
                        priceETH.getPercentChange7d(),
                        priceETH.getPercentChange30d(),
                        priceETH.getPercentChange60d(),
                        priceETH.getPercentChange90d()
                )
        );
    }

    @Test
    void whenResponse200QuotaDataNull_thenNotUpdatePricesEntities() {
        var bodyRes = """
                {
                    "status": {
                        "timestamp": "2023-01-16T09:23:08.500Z",
                        "error_code": 0,
                        "error_message": null,
                        "elapsed": 40,
                        "credit_count": 1,
                        "notice": null
                    },
                    "data": {
                        "1": {
                            "id": 1,
                            "name": "Bitcoin",
                            "symbol": "BTC",
                            "slug": "bitcoin",
                            "num_market_pairs": 9931,
                            "date_added": "2013-04-28T00:00:00.000Z",
                            "max_supply": 21000000,
                            "circulating_supply": 19263793,
                            "total_supply": 19263793,
                            "is_active": 1,
                            "platform": null,
                            "cmc_rank": 1,
                            "is_fiat": 0,
                            "self_reported_circulating_supply": null,
                            "self_reported_market_cap": null,
                            "tvl_ratio": null,
                            "last_updated": "2023-01-16T09:21:00.000Z"
                        },
                        "1027": {
                                    "id": 1027,
                                    "name": "Ethereum",
                                    "symbol": "ETH",
                                    "slug": "ethereum",
                                    "num_market_pairs": 6360,
                                    "date_added": "2015-08-07T00:00:00.000Z",
                                    "max_supply": null,
                                    "circulating_supply": 122373866.2178,
                                    "total_supply": 122373866.2178,
                                    "is_active": 1,
                                    "platform": null,
                                    "cmc_rank": 2,
                                    "is_fiat": 0,
                                    "self_reported_circulating_supply": null,
                                    "self_reported_market_cap": null,
                                    "tvl_ratio": null,
                                    "last_updated": "2023-01-16T09:35:00.000Z"
                                }
                    }
                }
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        var expected = underTest.updateCryptocurrencyPrices();

        assertThat(expected).hasSize(2);

        assertThat(cryptocurrencyRepository.findAll()
                .stream().map(Cryptocurrency::getPrice).toList()).extracting(
                Price::getId,
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d,
                Price::getPercentChange30d,
                Price::getPercentChange60d,
                Price::getPercentChange90d
        ).containsExactly(
                tuple(
                        priceBTC.getId(),
                        priceBTC.getPriceCurrent(),
                        priceBTC.getPercentChange1h(),
                        priceBTC.getPercentChange24h(),
                        priceBTC.getPercentChange7d(),
                        priceBTC.getPercentChange30d(),
                        priceBTC.getPercentChange60d(),
                        priceBTC.getPercentChange90d()
                ),
                tuple(
                        priceETH.getId(),
                        priceETH.getPriceCurrent(),
                        priceETH.getPercentChange1h(),
                        priceETH.getPercentChange24h(),
                        priceETH.getPercentChange7d(),
                        priceETH.getPercentChange30d(),
                        priceETH.getPercentChange60d(),
                        priceETH.getPercentChange90d()
                )
        );
    }
}