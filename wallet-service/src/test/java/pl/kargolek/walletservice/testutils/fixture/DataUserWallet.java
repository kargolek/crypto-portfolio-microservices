package pl.kargolek.walletservice.testutils.fixture;

import pl.kargolek.walletservice.dto.UserBalance;
import pl.kargolek.walletservice.dto.UserTotalBalance;
import pl.kargolek.walletservice.dto.UserWallet;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Karol Kuta-Orlowicz
 */
public class DataUserWallet {

    private final DataEthereumWallets wallets = new DataEthereumWallets();

    public UserBalance getUserBalance1(){
        return new UserBalance()
                .setWalletAddress(wallets.WALLETS_1_VALID)
                .setQuantity(new BigDecimal("11.111111111"))
                .setBalance(new BigDecimal("2200.22"))
                .setBalance1h(new BigDecimal("2300.32"))
                .setBalance24h(new BigDecimal("2400.42"))
                .setBalance7d(new BigDecimal("2500.52"))
                .setBalance30d(new BigDecimal("2600.62"))
                .setBalance60d(new BigDecimal("2700.72"))
                .setBalance90d(new BigDecimal("2800.82"));
    }

    public UserBalance getUserBalance2() {
        return new UserBalance()
                .setWalletAddress(wallets.WALLETS_ANOTHER_1_VALID)
                .setQuantity(new BigDecimal("22.222222222"))
                .setBalance(new BigDecimal("4400.44"))
                .setBalance1h(new BigDecimal("4500.52"))
                .setBalance24h(new BigDecimal("4600.62"))
                .setBalance7d(new BigDecimal("4700.72"))
                .setBalance30d(new BigDecimal("4800.82"))
                .setBalance60d(new BigDecimal("4900.92"))
                .setBalance90d(new BigDecimal("5000.02"));
    }

    public UserBalance getUserBalance1Zero() {
        return new UserBalance()
                .setWalletAddress(wallets.WALLETS_1_VALID)
                .setQuantity(BigDecimal.ZERO)
                .setBalance(BigDecimal.ZERO)
                .setBalance1h(BigDecimal.ZERO)
                .setBalance24h(BigDecimal.ZERO)
                .setBalance7d(BigDecimal.ZERO)
                .setBalance30d(BigDecimal.ZERO)
                .setBalance60d(BigDecimal.ZERO)
                .setBalance90d(BigDecimal.ZERO);
    }

    public UserBalance getUserBalance2Zero() {
        return new UserBalance()
                .setWalletAddress(wallets.WALLETS_ANOTHER_1_VALID)
                .setQuantity(BigDecimal.ZERO)
                .setBalance(BigDecimal.ZERO)
                .setBalance1h(BigDecimal.ZERO)
                .setBalance24h(BigDecimal.ZERO)
                .setBalance7d(BigDecimal.ZERO)
                .setBalance30d(BigDecimal.ZERO)
                .setBalance60d(BigDecimal.ZERO)
                .setBalance90d(BigDecimal.ZERO);
    }

    public UserBalance getUserBalance1Null() {
        return new UserBalance()
                .setWalletAddress(null)
                .setQuantity(null)
                .setBalance(null)
                .setBalance1h(null)
                .setBalance24h(null)
                .setBalance7d(null)
                .setBalance30d(null)
                .setBalance60d(null)
                .setBalance90d(null);
    }

    public UserBalance getUserBalance2Null() {
        return new UserBalance()
                .setWalletAddress(null)
                .setQuantity(null)
                .setBalance(null)
                .setBalance1h(null)
                .setBalance24h(null)
                .setBalance7d(null)
                .setBalance30d(null)
                .setBalance60d(null)
                .setBalance90d(null);
    }

    public UserTotalBalance getUserTotalBalance1And2(){
        return new UserTotalBalance()
                .setTotalQuantity(
                        getUserBalance1().getQuantity().add(getUserBalance2().getQuantity()))
                .setTotalBalance(getUserBalance1().getBalance().add(getUserBalance2().getBalance()));
    }


    public UserWallet getUserWalletOne() {
        return new UserWallet()
                .setName("Ethereum")
                .setSymbol("ETH")
                .setBalance(List.of(getUserBalance1(), getUserBalance2()))
                .setTotal(getUserTotalBalance1And2());
    }

    public UserWallet getUserWalletBalanceZero() {
        return new UserWallet()
                .setName("Ethereum")
                .setSymbol("ETH")
                .setBalance(List.of(getUserBalance1Zero(), getUserBalance2Zero()));
    }

    public UserWallet getUserWalletBalanceNull() {
        return new UserWallet()
                .setName("Ethereum")
                .setSymbol("ETH")
                .setBalance(List.of(getUserBalance1Null(), getUserBalance2Null()));
    }

}
