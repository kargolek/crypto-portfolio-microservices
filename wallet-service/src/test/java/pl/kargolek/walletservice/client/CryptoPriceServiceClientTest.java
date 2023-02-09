package pl.kargolek.walletservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.kargolek.walletservice.dto.TokenDTO;
import pl.kargolek.walletservice.dto.TokenPriceDTO;
import pl.kargolek.walletservice.testutils.config.ConfigCryptoPriceMockServer;
import pl.kargolek.walletservice.testutils.config.InitializerCryptoPriceMockWebServer;
import pl.kargolek.walletservice.testutils.extension.ExtCryptoPriceResponseResolver;
import pl.kargolek.walletservice.testutils.extension.ExtCryptocurrencyResolver;
import pl.kargolek.walletservice.testutils.fixture.ResponseCryptoPriceService;
import pl.kargolek.walletservice.testutils.fixture.DataCryptocurrency;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

/**
 * @author Karol Kuta-Orlowicz
 */
@ExtendWith(ExtCryptoPriceResponseResolver.class)
@ExtendWith(ExtCryptocurrencyResolver.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {InitializerCryptoPriceMockWebServer.class}, classes = {ConfigCryptoPriceMockServer.class})
@Tag("IntegrationTest")
class CryptoPriceServiceClientTest {

    @Autowired
    private CryptoPriceServiceClient underTest;

    @Autowired
    private MockWebServer mockWebServer;

    @Test
    void whenGetAllCryptocurrenciesStatus200_thenReturnListCryptocurrencyDTO(ResponseCryptoPriceService responseCryptoPriceService,
                                                                             DataCryptocurrency dataCryptocurrency) throws JsonProcessingException {
        mockWebServer.enqueue(
                responseCryptoPriceService.getAllCryptocurrenciesHttpStatusOK()
        );

        var expected = underTest.getTokens();

        var cryptocurrencyBTC = dataCryptocurrency.getCryptocurrencyBTC();
        var cryptocurrencyETH = dataCryptocurrency.getCryptocurrencyETH();

        assertThat(expected)
                .hasSize(2)
                .extracting(
                        TokenDTO::getId,
                        TokenDTO::getName,
                        TokenDTO::getSymbol,
                        TokenDTO::getCoinMarketId,
                        TokenDTO::getLastUpdate
                ).containsExactly(
                        tuple(
                                cryptocurrencyBTC.getId(),
                                cryptocurrencyBTC.getName(),
                                cryptocurrencyBTC.getSymbol(),
                                cryptocurrencyBTC.getCoinMarketId(),
                                cryptocurrencyBTC.getLastUpdate()
                        ),
                        tuple(
                                cryptocurrencyETH.getId(),
                                cryptocurrencyETH.getName(),
                                cryptocurrencyETH.getSymbol(),
                                cryptocurrencyETH.getCoinMarketId(),
                                cryptocurrencyETH.getLastUpdate()
                        )
                );

        var priceBTC = cryptocurrencyBTC.getPrice();
        var priceETH = cryptocurrencyETH.getPrice();

        assertThat(expected).extracting(
                TokenDTO::getPrice
        ).extracting(
                TokenPriceDTO::getId,
                TokenPriceDTO::getPriceCurrent,
                TokenPriceDTO::getPercentChange1h,
                TokenPriceDTO::getPercentChange24h,
                TokenPriceDTO::getPercentChange7d,
                TokenPriceDTO::getPercentChange30d,
                TokenPriceDTO::getPercentChange60d,
                TokenPriceDTO::getPercentChange90d,
                TokenPriceDTO::getLastUpdate
        ).containsExactly(
                tuple(
                        priceBTC.getId(),
                        priceBTC.getPriceCurrent(),
                        priceBTC.getPercentChange1h(),
                        priceBTC.getPercentChange24h(),
                        priceBTC.getPercentChange7d(),
                        priceBTC.getPercentChange30d(),
                        priceBTC.getPercentChange60d(),
                        priceBTC.getPercentChange90d(),
                        priceBTC.getLastUpdate()
                ),
                tuple(
                        priceETH.getId(),
                        priceETH.getPriceCurrent(),
                        priceETH.getPercentChange1h(),
                        priceETH.getPercentChange24h(),
                        priceETH.getPercentChange7d(),
                        priceETH.getPercentChange30d(),
                        priceETH.getPercentChange60d(),
                        priceETH.getPercentChange90d(),
                        priceETH.getLastUpdate()
                )
        );
    }

    @Test
    void whenGetAllCryptocurrenciesStatus500_thenReturnEmptyListCryptocurrencyDTO(ResponseCryptoPriceService responseCryptoPriceService) {
        mockWebServer.enqueue(
                responseCryptoPriceService.getAllCryptocurrenciesHttpStatus500()
        );

        var expected = underTest.getTokens();

        assertThat(expected).hasSize(0);
    }

    @Test
    void whenGetCryptocurrenciesByNameStatus200_thenReturnListCryptocurrencyDTO(ResponseCryptoPriceService responseCryptoPriceService,
                                                                                DataCryptocurrency dataCryptocurrency) throws JsonProcessingException {
        mockWebServer.enqueue(
                responseCryptoPriceService.getAllCryptocurrenciesHttpStatusOK()
        );

        var expected = underTest.getTokensByName(List.of("BTC", "ETH"));

        var cryptocurrencyBTC = dataCryptocurrency.getCryptocurrencyBTC();
        var cryptocurrencyETH = dataCryptocurrency.getCryptocurrencyETH();

        assertThat(expected)
                .hasSize(2)
                .extracting(
                        TokenDTO::getId,
                        TokenDTO::getName,
                        TokenDTO::getSymbol,
                        TokenDTO::getCoinMarketId,
                        TokenDTO::getLastUpdate
                ).containsExactly(
                        tuple(
                                cryptocurrencyBTC.getId(),
                                cryptocurrencyBTC.getName(),
                                cryptocurrencyBTC.getSymbol(),
                                cryptocurrencyBTC.getCoinMarketId(),
                                cryptocurrencyBTC.getLastUpdate()
                        ),
                        tuple(
                                cryptocurrencyETH.getId(),
                                cryptocurrencyETH.getName(),
                                cryptocurrencyETH.getSymbol(),
                                cryptocurrencyETH.getCoinMarketId(),
                                cryptocurrencyETH.getLastUpdate()
                        )
                );

        var priceBTC = cryptocurrencyBTC.getPrice();
        var priceETH = cryptocurrencyETH.getPrice();

        assertThat(expected).extracting(
                TokenDTO::getPrice
        ).extracting(
                TokenPriceDTO::getId,
                TokenPriceDTO::getPriceCurrent,
                TokenPriceDTO::getPercentChange1h,
                TokenPriceDTO::getPercentChange24h,
                TokenPriceDTO::getPercentChange7d,
                TokenPriceDTO::getPercentChange30d,
                TokenPriceDTO::getPercentChange60d,
                TokenPriceDTO::getPercentChange90d,
                TokenPriceDTO::getLastUpdate
        ).containsExactly(
                tuple(
                        priceBTC.getId(),
                        priceBTC.getPriceCurrent(),
                        priceBTC.getPercentChange1h(),
                        priceBTC.getPercentChange24h(),
                        priceBTC.getPercentChange7d(),
                        priceBTC.getPercentChange30d(),
                        priceBTC.getPercentChange60d(),
                        priceBTC.getPercentChange90d(),
                        priceBTC.getLastUpdate()
                ),
                tuple(
                        priceETH.getId(),
                        priceETH.getPriceCurrent(),
                        priceETH.getPercentChange1h(),
                        priceETH.getPercentChange24h(),
                        priceETH.getPercentChange7d(),
                        priceETH.getPercentChange30d(),
                        priceETH.getPercentChange60d(),
                        priceETH.getPercentChange90d(),
                        priceETH.getLastUpdate()
                )
        );
    }

    @Test
    void whenGetCryptocurrenciesByNameStatus500_thenReturnEmptyListCryptocurrencyDTO(ResponseCryptoPriceService responseCryptoPriceService) {
        mockWebServer.enqueue(
                responseCryptoPriceService.getAllCryptocurrenciesHttpStatus500()
        );

        var expected = underTest.getTokensByName(List.of("BTC", "ETH"));

        assertThat(expected).hasSize(0);
    }
}