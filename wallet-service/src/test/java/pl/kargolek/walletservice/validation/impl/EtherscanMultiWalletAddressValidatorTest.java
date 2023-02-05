package pl.kargolek.walletservice.validation.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.kargolek.walletservice.exception.InvalidAddressException;
import pl.kargolek.walletservice.testutils.extension.EthereumWalletsResolverExtension;
import pl.kargolek.walletservice.testutils.fixture.EthereumInvalidWallets;
import pl.kargolek.walletservice.testutils.fixture.EthereumValidWallets;
import pl.kargolek.walletservice.testutils.fixture.EthereumWalletsData;
import pl.kargolek.walletservice.validation.MultiWalletAddressValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Karol Kuta-Orlowicz
 */

@ExtendWith(EthereumWalletsResolverExtension.class)
class EtherscanMultiWalletAddressValidatorTest {

    private MultiWalletAddressValidator underTest;

    @BeforeEach
    void setUp() {
        var ethWalletValidator = new EthereumWalletAddressValidator();
        underTest = new EtherscanMultiWalletAddressValidator(ethWalletValidator);
    }

    @Test
    public void when20WalletsValid_thenReturnTrue(EthereumWalletsData data) {
        assertThat(underTest.isValidAddresses(data.WALLETS_20_ALL_VALID)).isTrue();
    }

    @Test
    public void when19WalletsValid_thenReturnTrue(EthereumWalletsData data) {
        assertThat(underTest.isValidAddresses(data.WALLETS_19_ALL_VALID)).isTrue();
    }

    @Test
    public void when21WalletsValid_thenThrowInvalidAddressException(EthereumWalletsData data) {
        assertThatThrownBy(() -> underTest.isValidAddresses(data.WALLETS_21_ALL_VALID))
                .isInstanceOf(InvalidAddressException.class);
    }

    @Test
    public void when20WalletsValidOneInvalid_thenThrowInvalidAddressException(EthereumWalletsData data) {
        assertThatThrownBy(() -> underTest.isValidAddresses(data.WALLETS_20_ONE_INVALID))
                .isInstanceOf(InvalidAddressException.class);
    }

    @Test
    public void when1WalletsInvalid_thenThrowInvalidAddressException() {
        assertThatThrownBy(() -> underTest.isValidAddresses(EthereumInvalidWallets.WALLET_1.getAddress()))
                .isInstanceOf(InvalidAddressException.class);
    }

    @Test
    public void whenWalletsEmpty_thenThrowInvalidAddressException() {
        assertThatThrownBy(() -> underTest.isValidAddresses(""))
                .isInstanceOf(InvalidAddressException.class);
    }

    @Test
    public void whenWalletsNULL_thenThrowInvalidAddressException() {
        assertThatThrownBy(() -> underTest.isValidAddresses(null))
                .isInstanceOf(InvalidAddressException.class);
    }

    @Test
    public void whenWalletsWhiteSpace_thenThrowInvalidAddressException() {
        var multiWalletsWhiteSpace = EthereumValidWallets.WALLET_1 + ", " + EthereumValidWallets.WALLET_2;
        assertThatThrownBy(() -> underTest.isValidAddresses(multiWalletsWhiteSpace))
                .isInstanceOf(InvalidAddressException.class);
    }

    @Test
    public void whenWalletsInvalidCharacter_thenThrowInvalidAddressException() {
        var multiWalletsWhiteSpace = EthereumValidWallets.WALLET_1 + "!" + EthereumValidWallets.WALLET_2;
        assertThatThrownBy(() -> underTest.isValidAddresses(multiWalletsWhiteSpace))
                .isInstanceOf(InvalidAddressException.class);
    }
}