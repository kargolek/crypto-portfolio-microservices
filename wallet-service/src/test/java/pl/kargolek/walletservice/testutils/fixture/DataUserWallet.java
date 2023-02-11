package pl.kargolek.walletservice.testutils.fixture;

import pl.kargolek.walletservice.dto.UserBalance;
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

    public UserBalance getUserBalance2(){
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

    public UserWallet getUserWalletOne(){
        return new UserWallet()
                .setName("Ethereum")
                .setSymbol("ETH")
                .setBalance(List.of(getUserBalance1(), getUserBalance2()));
    }

}
