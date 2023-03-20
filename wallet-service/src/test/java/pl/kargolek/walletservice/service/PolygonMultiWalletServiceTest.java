package pl.kargolek.walletservice.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.kargolek.walletservice.dto.WalletBalance;
import pl.kargolek.walletservice.dto.WalletMultiBalance;
import pl.kargolek.walletservice.exception.ExternalServiceCallException;
import pl.kargolek.walletservice.testutils.BaseParamTest;
import pl.kargolek.walletservice.testutils.extension.ExtMockEtherscanServer;
import pl.kargolek.walletservice.testutils.fixture.ResponseEtherscanService;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static pl.kargolek.walletservice.testutils.extension.ExtMockEtherscanServer.etherscanMockWebServer;

/**
 * @author Karol Kuta-Orlowicz
 */
@ExtendWith(ExtMockEtherscanServer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("IntegrationTest")
class PolygonMultiWalletServiceTest extends BaseParamTest {

    private static final int MOCK_SERVER_RES_NUM = 11;
    @Autowired
    private PolygonMultiWalletService underTestService;
    private final String wallet1 = "0x8111111111111111111111111111111111111111";
    private final String wallet2 = "0x8222222222222222222222222222222222222222";

    @DynamicPropertySource
    public static void registerProperty(DynamicPropertyRegistry registry) {
        registry.add("api.polygonscan.baseUrl", () -> etherscanMockWebServer.url("/").toString());
        registry.add("api.etherscan.fixedDelayMillis", () -> "1");
    }

    @Test
    void whenServiceRes200NormalBody_thenReturnMultiWalletBalance(ResponseEtherscanService responseEtherscanService) {
        etherscanMockWebServer.enqueue(
                responseEtherscanService.getMockedResStatus200()
        );
        var expected = underTestService.getMultiWalletBalance(wallet1 + "," + wallet2);

        var messageOK = "OK";
        var statusOK = "1";

        assertThat(expected)
                .extracting(WalletMultiBalance::getStatus, WalletMultiBalance::getMessage)
                .containsExactly(statusOK, messageOK);

        var balance1 = new BigDecimal("10000000000000000000");
        var balance2 = new BigDecimal("20000000000000000000");

        assertThat(expected.getResult().get(0))
                .extracting(WalletBalance::getAccount, WalletBalance::getQuantity)
                .containsExactly(wallet1, balance1);

        assertThat(expected.getResult().get(1))
                .extracting(WalletBalance::getAccount, WalletBalance::getQuantity)
                .containsExactly(wallet2, balance2);
    }

    @Test
    void whenServiceRes200EmptyResult_thenReturnMultiWalletBalanceResultEmpty(ResponseEtherscanService responseEtherscanService) {
        etherscanMockWebServer.enqueue(
                responseEtherscanService.getMockedResStatus200ResultEmptyArray()
        );
        var expected = underTestService.getMultiWalletBalance(wallet1 + "," + wallet2);

        assertThat(expected.getResult().isEmpty()).isTrue();
    }

    @Test
    void whenServiceRes200MaxLimitReachedOneTime_thenReturnDtoAfterRetryRequest(ResponseEtherscanService responseEtherscanService) {
        etherscanMockWebServer.enqueue(
                responseEtherscanService.getMockedResStatus200MaxLimitReached()
        );

        etherscanMockWebServer.enqueue(
                responseEtherscanService.getMockedResStatus200()
        );

        var expected = underTestService.getMultiWalletBalance(wallet1 + "," + wallet2);

        assertThat(expected.getResult().isEmpty()).isFalse();
    }

    @Test
    void whenServiceRes200MaxLimitReachedMoreTenTime_thenThrowExc(ResponseEtherscanService responseEtherscanService) {
        for (int i = 0; i < MOCK_SERVER_RES_NUM; i++) {
            etherscanMockWebServer.enqueue(
                    responseEtherscanService.getMockedResStatus200MaxLimitReached()
            );
        }

        assertThatThrownBy(() -> underTestService.getMultiWalletBalance(wallet1 + "," + wallet2))
                .isInstanceOf(ExternalServiceCallException.class);
    }

    @Test
    void whenServiceRes500_thenThrowExc(ResponseEtherscanService responseEtherscanService) {
        for (int i = 0; i < MOCK_SERVER_RES_NUM; i++) {
            etherscanMockWebServer.enqueue(
                    responseEtherscanService.getMockedResStatus500()
            );
        }

        assertThatThrownBy(() -> underTestService.getMultiWalletBalance(wallet1 + "," + wallet2))
                .isInstanceOf(ExternalServiceCallException.class);
    }

    @Test
    void whenServiceRes400_thenThrowExc(ResponseEtherscanService responseEtherscanService) {
        for (int i = 0; i < MOCK_SERVER_RES_NUM; i++) {
            etherscanMockWebServer.enqueue(
                    responseEtherscanService.getMockedResStatus400()
            );
        }

        assertThatThrownBy(() -> underTestService.getMultiWalletBalance(wallet1 + "," + wallet2))
                .isInstanceOf(ExternalServiceCallException.class);
    }

}