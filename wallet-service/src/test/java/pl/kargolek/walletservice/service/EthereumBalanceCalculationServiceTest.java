package pl.kargolek.walletservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.kargolek.walletservice.dto.UserBalance;
import pl.kargolek.walletservice.dto.UserWallet;
import pl.kargolek.walletservice.exception.ExternalServiceCallException;
import pl.kargolek.walletservice.exception.InvalidAddressException;
import pl.kargolek.walletservice.exception.NoSuchCryptoPriceException;
import pl.kargolek.walletservice.testutils.config.ConfigCryptoPriceMockServer;
import pl.kargolek.walletservice.testutils.config.InitializerCryptoPriceMockWebServer;
import pl.kargolek.walletservice.testutils.extension.ExtCryptoPriceResponseResolver;
import pl.kargolek.walletservice.testutils.extension.ExtEtherscanResponseResolver;
import pl.kargolek.walletservice.testutils.extension.ExtMockEtherscanServer;
import pl.kargolek.walletservice.testutils.fixture.ResponseCryptoPriceService;
import pl.kargolek.walletservice.testutils.fixture.ResponseEtherscanService;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static pl.kargolek.walletservice.testutils.extension.ExtMockEtherscanServer.etherscanMockWebServer;

/**
 * @author Karol Kuta-Orlowicz
 */

@ExtendWith(ExtCryptoPriceResponseResolver.class)
@ExtendWith(ExtEtherscanResponseResolver.class)
@ExtendWith(ExtMockEtherscanServer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {InitializerCryptoPriceMockWebServer.class}, classes = {ConfigCryptoPriceMockServer.class})
@Tag("IntegrationTest")
class EthereumBalanceCalculationServiceTest {

    @Autowired
    private EthereumBalanceCalculationService ethereumBalanceCalculationService;

    @Autowired
    private MockWebServer cryptoPriceMockWebServer;

    private static final String WALLET_ADDRESS_1 = "0x8111111111111111111111111111111111111111";
    private static final String WALLET_ADDRESS_2 = "0x8222222222222222222222222222222222222222";

    @DynamicPropertySource
    public static void registerProperty(DynamicPropertyRegistry registry) {
        registry.add("api.etherscan.baseUrl", () -> etherscanMockWebServer.url("/").toString());
        registry.add("api.etherscan.fixedDelayMillis", () -> "1");
    }

    @Test
    void whenCallBalanceCalcEthMultiWallet_thenReturnListUserWallet(ResponseEtherscanService ethMockResponse,
                                                                    ResponseCryptoPriceService cryptoMockResponse) throws JsonProcessingException {

        cryptoPriceMockWebServer.enqueue(cryptoMockResponse.getAllCryptocurrenciesHttpStatusOK());
        etherscanMockWebServer.enqueue(ethMockResponse.getMockedResStatus200());

        var expected = ethereumBalanceCalculationService.callWalletsBalanceCalculation(WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2);

        assertThat(expected)
                .extracting(UserWallet::getName, UserWallet::getSymbol)
                .contains(tuple("Ethereum", "ETH"));

        assertThat(expected.get(0).getBalance())
                .extracting(
                        UserBalance::getWalletAddress,
                        UserBalance::getQuantity,
                        UserBalance::getBalance,
                        UserBalance::getBalance1h,
                        UserBalance::getBalance24h,
                        UserBalance::getBalance7d,
                        UserBalance::getBalance30d,
                        UserBalance::getBalance60d,
                        UserBalance::getBalance90d
                ).containsExactlyInAnyOrder(
                        tuple(WALLET_ADDRESS_1,
                                new BigDecimal("10"),
                                new BigDecimal("18005.0"),
                                new BigDecimal("19895.5250"),
                                new BigDecimal("19985.550"),
                                new BigDecimal("20075.5750"),
                                new BigDecimal("20165.600"),
                                new BigDecimal("20255.6250"),
                                new BigDecimal("20345.650")
                        ),
                        tuple(WALLET_ADDRESS_2,
                                new BigDecimal("20"),
                                new BigDecimal("36010.0"),
                                new BigDecimal("39791.0500"),
                                new BigDecimal("39971.100"),
                                new BigDecimal("40151.1500"),
                                new BigDecimal("40331.200"),
                                new BigDecimal("40511.2500"),
                                new BigDecimal("40691.300")
                        )
                );
    }

    @Test
    void whenEtherscanServiceReturn500_thenThrowCustomExc(ResponseEtherscanService ethMockResponse,
                                                          ResponseCryptoPriceService cryptoMockResponse) throws JsonProcessingException {

        cryptoPriceMockWebServer.enqueue(cryptoMockResponse.getAllCryptocurrenciesHttpStatusOK());

        for (int requestNum = 0; requestNum < 11; requestNum++)
            etherscanMockWebServer.enqueue(ethMockResponse.getMockedResStatus500());

        assertThatThrownBy(() -> ethereumBalanceCalculationService.callWalletsBalanceCalculation(WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2))
                .isInstanceOf(ExternalServiceCallException.class);
    }

    @Test
    void whenCryptoPriceServiceReturn500_thenThrowCustomExc(ResponseEtherscanService ethMockResponse,
                                                            ResponseCryptoPriceService cryptoMockResponse) {

        cryptoPriceMockWebServer.enqueue(cryptoMockResponse.getAllCryptocurrenciesHttpStatus500());
        etherscanMockWebServer.enqueue(ethMockResponse.getMockedResStatus200());

        assertThatThrownBy(() -> ethereumBalanceCalculationService.callWalletsBalanceCalculation(WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2))
                .isInstanceOf(NoSuchCryptoPriceException.class)
                .hasMessageContaining("Unable to get price for crypto: Ethereum");
    }

    @Test
    void whenWalletAddressIsInvalid_thenThrowCustomExc() {
        assertThatThrownBy(() -> ethereumBalanceCalculationService.callWalletsBalanceCalculation("0x8123," + WALLET_ADDRESS_2))
                .isInstanceOf(InvalidAddressException.class)
                .hasMessageContaining("Address is invalid for crypto ETH and address 0x8123, message: address is invalid");
    }
}