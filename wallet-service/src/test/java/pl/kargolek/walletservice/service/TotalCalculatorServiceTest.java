package pl.kargolek.walletservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.kargolek.walletservice.dto.UserTotalBalance;
import pl.kargolek.walletservice.testutils.BaseParamTest;
import pl.kargolek.walletservice.testutils.fixture.DataUserWallet;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karol Kuta-Orlowicz
 */
@Tag("UnitTest")
class TotalCalculatorServiceTest extends BaseParamTest {

    private TotalCalculatorService underTest;

    @BeforeEach
    void setUp() {
        underTest = new TotalCalculatorService();
    }

    @Test
    void whenQuantityPositiveBalancePositive_thenReturnCorrectValueCount(DataUserWallet data){
        var userWallet = data.getUserWalletOne();

        var expected = underTest.calcQuantityBalance(userWallet);

        assertThat(expected).extracting(
                UserTotalBalance::getTotalQuantity,
                UserTotalBalance::getTotalBalance
        ).contains(
                new BigDecimal("33.333333333"),
                new BigDecimal("6600.66")
        );
    }

    @Test
    void whenQuantityZeroBalanceZero_thenReturnCorrectValueZero(DataUserWallet data){
        var userWallet = data.getUserWalletBalanceZero();

        var expected = underTest.calcQuantityBalance(userWallet);

        assertThat(expected).extracting(
                UserTotalBalance::getTotalQuantity,
                UserTotalBalance::getTotalBalance
        ).contains(
                new BigDecimal("0"),
                new BigDecimal("0")
        );
    }

    @Test
    void whenQuantityNullBalanceNull_thenReturnCorrectValueZero(DataUserWallet data){
        var userWallet = data.getUserWalletBalanceNull();

        var expected = underTest.calcQuantityBalance(userWallet);

        assertThat(expected).extracting(
                UserTotalBalance::getTotalQuantity,
                UserTotalBalance::getTotalBalance
        ).contains(
                new BigDecimal("0"),
                new BigDecimal("0")
        );
    }
}