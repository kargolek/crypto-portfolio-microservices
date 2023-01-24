package pl.kargolek.cryptopriceservice.service;

import okhttp3.mockwebserver.MockResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import pl.kargolek.cryptopriceservice.exception.CryptocurrencyNotFoundException;
import pl.kargolek.cryptopriceservice.exception.MarketApiClientException;
import pl.kargolek.cryptopriceservice.extension.MockWebServerExtension;
import pl.kargolek.cryptopriceservice.extension.MySqlTestContainerExtension;
import pl.kargolek.cryptopriceservice.model.Cryptocurrency;
import pl.kargolek.cryptopriceservice.model.Price;
import pl.kargolek.cryptopriceservice.repository.CryptocurrencyRepository;
import pl.kargolek.cryptopriceservice.repository.PriceRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static pl.kargolek.cryptopriceservice.extension.MockWebServerExtension.mockWebServer;

@Transactional
@ExtendWith(MySqlTestContainerExtension.class)
@ExtendWith(MockWebServerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("IntegrationTest")
@ActiveProfiles("test")
public class CryptocurrencyServiceIntegrationTest {

    @Autowired
    private CryptocurrencyService underTestService;
    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;

    @Autowired
    private PriceRepository priceRepository;

    private Cryptocurrency cryptocurrency;

    @DynamicPropertySource
    static void registerProperty(DynamicPropertyRegistry registry) {
        registry.add("api.coin.market.cap.baseUrl", () -> mockWebServer.url("/").toString());
    }

    @BeforeEach
    public void setup() {
        cryptocurrency = Cryptocurrency.builder()
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(RandomUtils.nextLong())
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
    }

    @Test
    void whenAddCryptocurrency_thenShouldSaveSuccessful() {
        var expected = underTestService.addCryptocurrency(cryptocurrency);
        assertThat(cryptocurrencyRepository.findAll())
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId,
                        Cryptocurrency::getLastUpdate
                ).containsExactly(
                        tuple(
                                expected.getId(),
                                expected.getName(),
                                expected.getSymbol(),
                                expected.getCoinMarketId(),
                                expected.getLastUpdate()
                        )
                );
    }

    @Test
    void whenAddCryptocurrencyWithoutMarketId_thenShouldSaveSuccessful() {
        cryptocurrency.setCoinMarketId(null);

        var bodyRes = """
                {
                    "status": {
                        "timestamp": "2023-01-20T00:06:31.909Z",
                        "error_code": 0,
                        "error_message": null,
                        "elapsed": 16,
                        "credit_count": 1,
                        "notice": null
                    },
                    "data": [
                        {
                            "id": 1,
                            "name": "Bitcoin",
                            "symbol": "BTC",
                            "slug": "bitcoin",
                            "rank": 1,
                            "displayTV": 1,
                            "manualSetTV": 0,
                            "tvCoinSymbol": "",
                            "is_active": 1,
                            "first_historical_data": "2013-04-28T18:47:21.000Z",
                            "last_historical_data": "2023-01-19T23:59:00.000Z",
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

        var expected = underTestService.addCryptocurrency(cryptocurrency);

        System.out.println("COIN MARKET: " + expected.getCoinMarketId());

        assertThat(cryptocurrencyRepository.findAll())
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId,
                        Cryptocurrency::getLastUpdate
                ).containsExactly(
                        tuple(
                                expected.getId(),
                                expected.getName(),
                                expected.getSymbol(),
                                expected.getCoinMarketId(),
                                expected.getLastUpdate()
                        )
                );
    }

    @Test
    void whenAddWithoutMarketIdAndMarketResponse500_thenShouldSave() {
        cryptocurrency.setCoinMarketId(null);

        var bodyRes = """
                {
                    "status": {
                    "timestamp": "2018-06-02T22:51:28.209Z",
                    "error_code": 500,
                    "error_message": "An internal server error occurred",
                    "elapsed": 10,
                    "credit_count": 0
                    }
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        assertThatThrownBy(() -> underTestService.addCryptocurrency(cryptocurrency))
                .isInstanceOf(MarketApiClientException.class)
                .hasMessageContaining("serverMessage: An internal server error occurred");
    }

    @Test
    void whenAddCryptoWithTheSameCoinMarketId_thenShouldThrowConstraintViolation() {
        var coinMarketIdRandom = RandomUtils.nextLong();
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(coinMarketIdRandom)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        var crypto2 = Cryptocurrency.builder()
                .name("Chia")
                .symbol("XCH")
                .coinMarketId(coinMarketIdRandom)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        underTestService.addCryptocurrency(crypto);

        assertThatThrownBy(() -> underTestService.addCryptocurrency(crypto2))
                .hasCauseInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void whenUpdateByCorrectId_thenShouldUpdateSuccessful() {
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        var cryptoToUpdate = cryptocurrencyRepository.save(crypto);

        var cryptoUpdate = Cryptocurrency.builder()
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(2L)
                .build();

        var expected =
                underTestService.updateCryptocurrency(cryptoToUpdate.getId(), cryptoUpdate);

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId)
                .containsExactly(cryptoToUpdate.getId(), "Bitcoin", "BTC", 2L);
    }

    @Test
    void whenUpdateByIncorrectId_thenShouldThrowAnException() {
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        var cryptoToUpdate = cryptocurrencyRepository.save(crypto);

        var cryptoUpdate = Cryptocurrency.builder()
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(2L)
                .build();

        var searchedId = cryptoToUpdate.getId() + 10L;

        assertThatThrownBy(() ->
                underTestService.updateCryptocurrency(
                        searchedId,
                        cryptoUpdate))
                .isInstanceOf(CryptocurrencyNotFoundException.class)
                .hasMessageContaining("id: " + searchedId);
    }

    @Test
    void whenDeleteById_thenShouldDeleteCryptocurrency() {
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        var id = cryptocurrencyRepository.save(crypto).getId();

        underTestService.deleteCryptocurrency(id);

        assertThat(cryptocurrencyRepository.findAll())
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName
                ).doesNotContain(
                        tuple(id, "Ethereum")
                );
    }

    @Test
    void whenDeleteByIdNotExisted_thenThrowEmptyResultDataAccessExc() {
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        var id = cryptocurrencyRepository.save(crypto);

        assertThatThrownBy(() -> underTestService.deleteCryptocurrency(100L))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessageContaining("No class")
                .hasMessageContaining("entity with id 100 exists");
    }

    @Test
    void whenFindByName_thenFindShouldBeSuccessful() {
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        var cryptoSaved = cryptocurrencyRepository.save(crypto);

        var expected = underTestService.getByName(List.of("Ethereum", "Bitcoin"));

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId
                ).containsExactly(
                        tuple(
                                cryptoSaved.getId(),
                                "Ethereum",
                                "ETH",
                                1L
                        )
                );
    }

    @Test
    void whenSaveCryptocurrencyEntity_thenPriceEntityShouldSaveAlso() {
        underTestService.addCryptocurrency(cryptocurrency);

        assertThat(priceRepository.findAll())
                .hasSize(1)
                .extracting(
                        Price::getId,
                        Price::getPriceCurrent,
                        Price::getPercentChange1h,
                        Price::getCryptocurrency
                ).containsExactly(
                        tuple(
                                cryptocurrency.getPrice().getId(), null, null, cryptocurrency
                        )
                );
    }
}
