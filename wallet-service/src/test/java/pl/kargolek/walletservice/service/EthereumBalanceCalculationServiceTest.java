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
import pl.kargolek.walletservice.dto.UserTotalBalance;
import pl.kargolek.walletservice.dto.UserWallet;
import pl.kargolek.walletservice.exception.ExternalServiceCallException;
import pl.kargolek.walletservice.exception.InvalidAddressException;
import pl.kargolek.walletservice.exception.NoSuchCryptoPriceDataException;
import pl.kargolek.walletservice.testutils.BaseParamTest;
import pl.kargolek.walletservice.testutils.config.ConfigCryptoPriceMockServer;
import pl.kargolek.walletservice.testutils.config.InitializerCryptoPriceMockWebServer;
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

@ExtendWith(ExtMockEtherscanServer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {InitializerCryptoPriceMockWebServer.class}, classes = {ConfigCryptoPriceMockServer.class})
@Tag("IntegrationTest")
class EthereumBalanceCalculationServiceTest extends BaseParamTest {

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
    void whenCallBalanceCalcEthMultiWallet_thenReturnUserWallet(ResponseEtherscanService ethMockResponse,
                                                                    ResponseCryptoPriceService cryptoMockResponse) throws JsonProcessingException {

        cryptoPriceMockWebServer.enqueue(cryptoMockResponse.getAllCryptocurrenciesHttpStatusOK());
        etherscanMockWebServer.enqueue(ethMockResponse.getMockedResStatus200());

        var expected = ethereumBalanceCalculationService.callWalletsBalanceCalculation(WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2);

        assertThat(expected)
                .extracting(UserWallet::getName, UserWallet::getSymbol)
                .contains("Ethereum", "ETH");

        assertThat(expected.getBalance())
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
                                new BigDecimal("18005.00"),
                                new BigDecimal("16114.48"),
                                new BigDecimal("16024.45"),
                                new BigDecimal("15934.42"),
                                new BigDecimal("15844.40"),
                                new BigDecimal("15754.38"),
                                new BigDecimal("15664.35")
                        ),
                        tuple(WALLET_ADDRESS_2,
                                new BigDecimal("20"),
                                new BigDecimal("36010.00"),
                                new BigDecimal("32228.95"),
                                new BigDecimal("32048.90"),
                                new BigDecimal("31868.85"),
                                new BigDecimal("31688.80"),
                                new BigDecimal("31508.75"),
                                new BigDecimal("31328.70")
                        )
                );

        assertThat(expected.getTotal())
                .extracting(UserTotalBalance::getTotalQuantity, UserTotalBalance::getTotalBalance)
                .contains(new BigDecimal("30"), new BigDecimal("54015.00"));
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
                .isInstanceOf(NoSuchCryptoPriceDataException.class)
                .hasMessageContaining("Unable to get price for crypto: Ethereum");
    }

    @Test
    void whenWalletAddressIsInvalid_thenThrowCustomExc() {
        assertThatThrownBy(() -> ethereumBalanceCalculationService.callWalletsBalanceCalculation("0x8123," + WALLET_ADDRESS_2))
                .isInstanceOf(InvalidAddressException.class)
                .hasMessageContaining("Address is invalid for crypto ETH and address 0x8123, message: address is invalid");
    }

    @Test
    void whenWalletsMoreThan20_thenReturnOneUserWalletWithMergedBalances(ResponseEtherscanService ethMockResponse,
                                                       ResponseCryptoPriceService cryptoMockResponse) throws JsonProcessingException {

        cryptoPriceMockWebServer.enqueue(cryptoMockResponse.getAllCryptocurrenciesHttpStatusOK());
        etherscanMockWebServer.enqueue(ethMockResponse.getMockedResStatus200Valid20Addresses());
        etherscanMockWebServer.enqueue(ethMockResponse.getMockedResStatus200());

        var wallets = WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2 + "," +
                WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2 + "," +
                WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2 + "," +
                WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2 + "," +
                WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2 + "," +
                WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2 + "," +
                WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2 + "," +
                WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2 + "," +
                WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2 + "," +
                WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2 + "," +
                WALLET_ADDRESS_1 + "," + WALLET_ADDRESS_2;

        var expected = ethereumBalanceCalculationService.callWalletsBalanceCalculation(wallets);

        var walletsCount = wallets.split(",").length;

        assertThat(expected.getBalance())
                .hasSize(walletsCount);
    }
}