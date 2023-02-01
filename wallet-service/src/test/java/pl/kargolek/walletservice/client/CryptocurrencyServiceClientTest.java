package pl.kargolek.walletservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.kargolek.walletservice.config.CryptoPriceMockServerConfig;
import pl.kargolek.walletservice.config.MockWebServerInitializer;
import pl.kargolek.walletservice.dto.CryptocurrencyDTO;
import pl.kargolek.walletservice.dto.PriceDTO;
import pl.kargolek.walletservice.extension.CryptoPriceMockResponseExtension;
import pl.kargolek.walletservice.extension.CryptocurrencyResolverExtension;
import pl.kargolek.walletservice.util.CryptoPriceServiceMockResponse;
import pl.kargolek.walletservice.util.CryptocurrencyDataResolver;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

/**
 * @author Karol Kuta-Orlowicz
 */
@ExtendWith(CryptoPriceMockResponseExtension.class)
@ExtendWith(CryptocurrencyResolverExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {MockWebServerInitializer.class}, classes = {CryptoPriceMockServerConfig.class})
@Tag("IntegrationTest")
class CryptocurrencyServiceClientTest {

    @Autowired
    private CryptocurrencyServiceClient underTest;

    @Autowired
    private MockWebServer mockWebServer;

    @Test
    void whenGetAllCryptocurrenciesStatus200_thenReturnListCryptocurrencyDTO(CryptoPriceServiceMockResponse cryptoPriceServiceMockResponse,
                                                                             CryptocurrencyDataResolver cryptocurrencyDataResolver) throws JsonProcessingException {
        mockWebServer.enqueue(
                cryptoPriceServiceMockResponse.getAllCryptocurrenciesHttpStatusOK()
        );

        var expected = underTest.getCryptocurrencies();

        var cryptocurrencyBTC = cryptocurrencyDataResolver.getCryptocurrencyBTC();
        var cryptocurrencyETH = cryptocurrencyDataResolver.getCryptocurrencyETH();

        assertThat(expected)
                .hasSize(2)
                .extracting(
                        CryptocurrencyDTO::getId,
                        CryptocurrencyDTO::getName,
                        CryptocurrencyDTO::getSymbol,
                        CryptocurrencyDTO::getCoinMarketId,
                        CryptocurrencyDTO::getLastUpdate
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
                CryptocurrencyDTO::getPrice
        ).extracting(
                PriceDTO::getId,
                PriceDTO::getPriceCurrent,
                PriceDTO::getPercentChange1h,
                PriceDTO::getPercentChange24h,
                PriceDTO::getPercentChange7d,
                PriceDTO::getPercentChange30d,
                PriceDTO::getPercentChange60d,
                PriceDTO::getPercentChange90d,
                PriceDTO::getLastUpdate
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
    void whenGetAllCryptocurrenciesStatus500_thenReturnEmptyListCryptocurrencyDTO(CryptoPriceServiceMockResponse cryptoPriceServiceMockResponse,
                                                                                  CryptocurrencyDataResolver cryptocurrencyDataResolver) {
        mockWebServer.enqueue(
                cryptoPriceServiceMockResponse.getAllCryptocurrenciesHttpStatus500()
        );

        var expected = underTest.getCryptocurrencies();

        assertThat(expected).hasSize(0);
    }

    @Test
    void whenGetCryptocurrenciesByNameStatus200_thenReturnListCryptocurrencyDTO(CryptoPriceServiceMockResponse cryptoPriceServiceMockResponse,
                                                                                CryptocurrencyDataResolver cryptocurrencyDataResolver) throws JsonProcessingException {
        mockWebServer.enqueue(
                cryptoPriceServiceMockResponse.getAllCryptocurrenciesHttpStatusOK()
        );

        var expected = underTest.getCryptocurrenciesByName(List.of("BTC", "ETH"));

        var cryptocurrencyBTC = cryptocurrencyDataResolver.getCryptocurrencyBTC();
        var cryptocurrencyETH = cryptocurrencyDataResolver.getCryptocurrencyETH();

        assertThat(expected)
                .hasSize(2)
                .extracting(
                        CryptocurrencyDTO::getId,
                        CryptocurrencyDTO::getName,
                        CryptocurrencyDTO::getSymbol,
                        CryptocurrencyDTO::getCoinMarketId,
                        CryptocurrencyDTO::getLastUpdate
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
                CryptocurrencyDTO::getPrice
        ).extracting(
                PriceDTO::getId,
                PriceDTO::getPriceCurrent,
                PriceDTO::getPercentChange1h,
                PriceDTO::getPercentChange24h,
                PriceDTO::getPercentChange7d,
                PriceDTO::getPercentChange30d,
                PriceDTO::getPercentChange60d,
                PriceDTO::getPercentChange90d,
                PriceDTO::getLastUpdate
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
    void whenGetCryptocurrenciesByNameStatus500_thenReturnEmptyListCryptocurrencyDTO(CryptoPriceServiceMockResponse cryptoPriceServiceMockResponse,
                                                                                     CryptocurrencyDataResolver cryptocurrencyDataResolver) {
        mockWebServer.enqueue(
                cryptoPriceServiceMockResponse.getAllCryptocurrenciesHttpStatus500()
        );

        var expected = underTest.getCryptocurrenciesByName(List.of("BTC", "ETH"));

        assertThat(expected).hasSize(0);
    }
}