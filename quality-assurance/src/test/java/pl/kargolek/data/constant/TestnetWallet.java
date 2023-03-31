package pl.kargolek.data.constant;

import pl.kargolek.data.model.Wallet;

/**
 * @author Karol Kuta-Orlowicz
 */
public class TestnetWallet {
    public static final Wallet ETHEREUM_WALLET_1 = Wallet.builder()
            .address("0x8d81156294aBC787e5cBcc16774799Aeba574439")
            .amount("0.23519701419126102")
            .build();

    public static final Wallet POLYGON_WALLET_1 = Wallet.builder()
            .address("0x8d81156294aBC787e5cBcc16774799Aeba574439")
            .amount("0.4")
            .build();

    public static final Wallet AVALANCHE_WALLET_1 = Wallet.builder()
            .address("0x8d81156294aBC787e5cBcc16774799Aeba574439")
            .amount("1.5")
            .build();
}
