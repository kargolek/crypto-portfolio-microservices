package pl.kargolek.walletservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.kargolek.walletservice.dto.MultiWalletBalance;
import pl.kargolek.walletservice.testutils.fixture.EtherscanResponseResolver;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @author Karol Kuta-Orlowicz
 */
@Tag("UnitTest")
class DeserializerWalletBalanceTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void whenResponseIsAsExpected_thenObjectDeserializedProperly() throws JsonProcessingException {
        var expected = mapper.readValue(EtherscanResponseResolver.ETHERSCAN_MULTI_WALLET_RES_200_AS_EXPECTED,
                MultiWalletBalance.class);

        assertThat(expected).extracting(
                MultiWalletBalance::getStatus,
                MultiWalletBalance::getMessage
        ).allMatch(Objects::nonNull);

        assertThat(expected.getResult()).isNotNull();
    }

    @Test
    void whenResponseResultEmptyArray_thenObjectResultFieldEmpty() throws JsonProcessingException {
        var expected = mapper.readValue(EtherscanResponseResolver.ETHERSCAN_MULTI_WALLET_RES_200_RESULT_EMPTY_ARRAY,
                MultiWalletBalance.class);

        assertThat(expected.getResult()).isEmpty();
    }

    @Test
    void whenResponseResultString_thenObjectResultIsNull() throws JsonProcessingException {
        var expected = mapper.readValue(EtherscanResponseResolver.ETHERSCAN_MULTI_WALLET_RES_200_RESULT_AS_STRING,
                MultiWalletBalance.class);

        assertThat(expected.getResult()).isNull();
    }

    @Test
    void whenResponseResultIsNull_thenObjectResultIsNull() throws JsonProcessingException {
        var expected = mapper.readValue(EtherscanResponseResolver.ETHERSCAN_MULTI_WALLET_RES_200_RESULT_IS_NULL,
                MultiWalletBalance.class);

        assertThat(expected.getResult()).isNull();
    }
}