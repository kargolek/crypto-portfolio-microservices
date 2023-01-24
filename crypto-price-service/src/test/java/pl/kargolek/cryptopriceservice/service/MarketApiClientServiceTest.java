package pl.kargolek.cryptopriceservice.service;

import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import pl.kargolek.cryptopriceservice.dto.client.CryptocurrencyMapDTO;
import pl.kargolek.cryptopriceservice.dto.client.CryptocurrencyQuoteDTO;
import pl.kargolek.cryptopriceservice.dto.client.PriceQuoteDTO;
import pl.kargolek.cryptopriceservice.exception.MarketApiClientException;
import pl.kargolek.cryptopriceservice.extension.MockWebServerExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static pl.kargolek.cryptopriceservice.extension.MockWebServerExtension.mockWebServer;

/**
 * @author Karol Kuta-Orlowicz
 */
@ExtendWith(MockWebServerExtension.class)
@Tag("UnitTest")
class MarketApiClientServiceTest {
    private MarketApiClientService underTest;

    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("").toString())
                .build();
        underTest = new MarketApiClientService(webClient);
    }


    @Test
    void whenServerRespond200_thenClientReturnRespondDTOCollection() {
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
                "price": 6602.60701122,
                "volume_24h": 4314444687.5194,
                "volume_change_24h": -0.152774,
                "percent_change_1h": 0.988615,
                "percent_change_24h": 4.37185,
                "percent_change_7d": -12.1352,
                "percent_change_30d": -12.1352,
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

        var response = underTest.getLatestPriceByIds("1");
        var cryptoResponse = response.orElseThrow().getData().get("1");
        var priceResponse = response.orElseThrow().getData().get("1").getQuote().get("USD");

        assertThat(cryptoResponse)
                .extracting(
                        CryptocurrencyQuoteDTO::getName,
                        CryptocurrencyQuoteDTO::getSymbol,
                        CryptocurrencyQuoteDTO::getCoinMarketId
                ).containsExactly(
                        "Bitcoin",
                        "BTC",
                        1L
                );

        assertThat(priceResponse).extracting(
                PriceQuoteDTO::getPriceCurrent,
                PriceQuoteDTO::getPercentChange1h,
                PriceQuoteDTO::getPercentChange24h,
                PriceQuoteDTO::getPercentChange7d,
                PriceQuoteDTO::getPercentChange30d,
                PriceQuoteDTO::getPercentChange60d,
                PriceQuoteDTO::getPercentChange90d
        ).containsExactly(
                new BigDecimal("6602.60701122"),
                new BigDecimal("0.988615"),
                new BigDecimal("4.37185"),
                new BigDecimal("-12.1352"),
                new BigDecimal("-12.1352"),
                null,
                null
        );
    }

    @Test
    void whenServerRespond400_thenClientThrowCustomExc() {
        var bodyRes = """
                {
                "status": {
                "timestamp": "2018-06-02T22:51:28.209Z",
                "error_code": 400,
                "error_message": "Invalid value for \\"id\\"",
                "elapsed": 10,
                "credit_count": 0
                }
                }""";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(400)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        assertThatThrownBy(() -> underTest.getLatestPriceByIds("1234567890"))
                .isInstanceOf(MarketApiClientException.class)
                .hasMessageContaining("Invalid value for \"id\"");
    }

    @Test
    void whenServerRespond500_thenClientThrowCustomExc() {
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

        assertThatThrownBy(() -> underTest.getLatestPriceByIds("1234567890"))
                .isInstanceOf(MarketApiClientException.class)
                .hasMessageContaining("serverMessage: An internal server error occurred");
    }

    @Test
    void whenServerRes200_thenClientReturnMapDataDTO() {
        var bodyRes = """
                {
                    "status": {
                        "timestamp": "2023-01-19T21:30:07.690Z",
                        "error_code": 0,
                        "error_message": null,
                        "elapsed": 15,
                        "credit_count": 1,
                        "notice": null
                    },
                    "data": [
                        {
                            "id": 1027,
                            "name": "Ethereum",
                            "symbol": "ETH",
                            "slug": "ethereum",
                            "rank": 2,
                            "displayTV": 1,
                            "manualSetTV": 0,
                            "tvCoinSymbol": "",
                            "is_active": 1,
                            "first_historical_data": "2015-08-07T14:49:30.000Z",
                            "last_historical_data": "2023-01-19T21:19:00.000Z",
                            "platform": null
                        }
                    ]
                }
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        var expected = underTest.getCryptoMarketIdBySymbol("eth");

        assertThat(expected.orElseThrow().getData())
                .extracting(
                        CryptocurrencyMapDTO::getName,
                        CryptocurrencyMapDTO::getSymbol,
                        CryptocurrencyMapDTO::getCoinMarketId
                ).containsExactly(
                        tuple(
                                "Ethereum",
                                "ETH",
                                1027L
                        )
                );
    }

    @Test
    void whenServerRes400_thenThrowMarketClientExc() {
        var bodyRes = """
                {
                    "status": {
                        "timestamp": "2023-01-19T22:08:01.209Z",
                        "error_code": 400,
                        "error_message": "\\"symbol\\" is not allowed to be empty",
                        "elapsed": 0,
                        "credit_count": 0
                    }
                }
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(400)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        assertThatThrownBy(() -> underTest.getCryptoMarketIdBySymbol(""))
                .isInstanceOf(MarketApiClientException.class)
                .hasMessageContaining("serverMessage: \"symbol\" is not allowed to be empty");

    }

}