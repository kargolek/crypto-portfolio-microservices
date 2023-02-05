package pl.kargolek.walletservice.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.kargolek.walletservice.dto.MultiWalletBalance;
import pl.kargolek.walletservice.dto.WalletBalance;
import pl.kargolek.walletservice.exception.ExternalServiceCallException;
import pl.kargolek.walletservice.testutils.extension.EtherscanMockResponseExtension;
import pl.kargolek.walletservice.testutils.extension.EtherscanMockTestServerExtension;
import pl.kargolek.walletservice.testutils.fixture.EtherscanServiceMockResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static pl.kargolek.walletservice.testutils.extension.EtherscanMockTestServerExtension.etherscanMockWebServer;

/**
 * @author Karol Kuta-Orlowicz
 */
@ExtendWith(EtherscanMockResponseExtension.class)
@ExtendWith(EtherscanMockTestServerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("IntegrationTest")
class EthereumWalletServiceTest {

    private static final int MOCK_SERVER_RES_NUM = 11;
    @Autowired
    private EthereumWalletService ethereumWalletService;
    private final String wallet1 = "0x8111111111111111111111111111111111111111";
    private final String wallet2 = "0x8222222222222222222222222222222222222222";

    @DynamicPropertySource
    public static void registerProperty(DynamicPropertyRegistry registry) {
        registry.add("api.etherscan.baseUrl", () -> etherscanMockWebServer.url("/").toString());
        registry.add("api.etherscan.fixedDelayMillis", () -> "1");
    }

    @Test
    void whenServiceRes200NormalBody_thenReturnMultiWalletBalance(EtherscanServiceMockResponse etherscanServiceMockResponse) {
        etherscanMockWebServer.enqueue(
                etherscanServiceMockResponse.getMockedResStatus200()
        );
        var expected = ethereumWalletService.getMultiBalance(wallet1 + "," + wallet2);

        String messageOK = "OK";
        String statusOK = "1";
        assertThat(expected).extracting(
                MultiWalletBalance::getStatus,
                MultiWalletBalance::getMessage
        ).containsExactly(
                statusOK,
                messageOK
        );

        String balance1 = "100000";
        String balance2 = "200000";
        assertThat(expected.getResult()).extracting(
                WalletBalance::getAccount,
                WalletBalance::getBalance
        ).containsExactly(
                tuple(
                        wallet1,
                        balance1
                ),
                tuple(
                        wallet2,
                        balance2
                )
        );
    }


    @Test
    void whenServiceRes200EmptyResult_thenReturnMultiWalletBalanceResultEmpty(EtherscanServiceMockResponse etherscanServiceMockResponse) {
        etherscanMockWebServer.enqueue(
                etherscanServiceMockResponse.getMockedResStatus200ResultEmptyArray()
        );
        var expected = ethereumWalletService.getMultiBalance(wallet1 + "," + wallet2);

        assertThat(expected.getResult()).isEmpty();
    }

    @Test
    void whenServiceRes200MaxLimitReachedOneTime_thenReturnDtoAfterRetryRequest(EtherscanServiceMockResponse etherscanServiceMockResponse) {
        etherscanMockWebServer.enqueue(
                etherscanServiceMockResponse.getMockedResStatus200MaxLimitReached()
        );

        etherscanMockWebServer.enqueue(
                etherscanServiceMockResponse.getMockedResStatus200()
        );

        var expected = ethereumWalletService.getMultiBalance(wallet1 + "," + wallet2);

        assertThat(expected.getResult()).isNotEmpty();
    }

    @Test
    void whenServiceRes200MaxLimitReachedMoreTenTime_thenThrowExc(EtherscanServiceMockResponse etherscanServiceMockResponse) {
        for (int i = 0; i < MOCK_SERVER_RES_NUM; i++) {
            etherscanMockWebServer.enqueue(
                    etherscanServiceMockResponse.getMockedResStatus200MaxLimitReached()
            );
        }

        assertThatThrownBy(() -> ethereumWalletService.getMultiBalance(wallet1 + "," + wallet2))
                .isInstanceOf(ExternalServiceCallException.class);
    }

    @Test
    void whenServiceRes500_thenThrowExc(EtherscanServiceMockResponse etherscanServiceMockResponse) {
        for (int i = 0; i < MOCK_SERVER_RES_NUM; i++) {
            etherscanMockWebServer.enqueue(
                    etherscanServiceMockResponse.getMockedResStatus500()
            );
        }

        assertThatThrownBy(() -> ethereumWalletService.getMultiBalance(wallet1 + "," + wallet2))
                .isInstanceOf(ExternalServiceCallException.class);
    }

    @Test
    void whenServiceRes400_thenThrowExc(EtherscanServiceMockResponse etherscanServiceMockResponse) {
        for (int i = 0; i < MOCK_SERVER_RES_NUM; i++) {
            etherscanMockWebServer.enqueue(
                    etherscanServiceMockResponse.getMockedResStatus400()
            );
        }

        assertThatThrownBy(() -> ethereumWalletService.getMultiBalance(wallet1 + "," + wallet2))
                .isInstanceOf(ExternalServiceCallException.class);
    }

}