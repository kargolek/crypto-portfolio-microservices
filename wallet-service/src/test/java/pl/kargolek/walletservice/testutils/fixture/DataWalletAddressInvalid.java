package pl.kargolek.walletservice.testutils.fixture;

/**
 * @author Karol Kuta-Orlowicz
 */
public enum DataWalletAddressInvalid {
    WALLET_1("0x742d35Cc6634C0532925a3b844Bc454e4438f44"),
    WALLET_2("0x5A0b54D5dc17e0AadC383d2db43B0a0D3E029c4"),
    WALLET_3("0x742d35Cc6634C0532925a3b844Bc454e4438f4"),
    WALLET_4("0x5A0b54D5dc17e0AadC383d2db43B0a0D3E029c"),
    WALLET_5("0x742d35Cc6634C0532925a3b844Bc454e4438f"),
    WALLET_6("0x742d35Cc6634C0532925a3b844Bc454e4438f44"),
    WALLET_7("0x5A0b54D5dc17e0AadC383d2db43B0a0D3E029c4"),
    WALLET_8("0x742d35Cc6634C0532925a3b844Bc454e4438f4"),
    WALLET_9("0x5A0b54D5dc17e0AadC383d2db43B0a0D3E029c"),
    WALLET_10("0x742d35Cc6634C0532925a3b844Bc454e4438f"),
    WALLET_11("0x5A0b54D5dc17e0AadC383d2db43B0a0D3E029c3"),
    WALLET_12("0x742d35Cc6634C0532925a3b844Bc454e4438f4"),
    WALLET_13("0x5A0b54D5dc17e0AadC383d2db43B0a0D3E029c4"),
    WALLET_14("0x5A0b54D5dc17e0AadC383d2db43B0a0D3E0294"),
    WALLET_15("0xW742d35Cc6634C0532925a3b844Bc454e4438f4b"),
    WALLET_16("0115A0b54D5dc17e0AadC383d2db43B0a0D3E029cb"),
    WALLET_17("0x742d35Cc6634C0532925a3b844Bc454e4438f43"),
    WALLET_18("0x5A0b54D5dc17e0AadC383d2db43B0a0D3E0294b"),
    WALLET_19("0x742d35Cc6634C0532925a3b844Bc454e4438f4c"),
    WALLET_20("0x5A0b54D5dc17e0AadC383d2db43B0a0D3E029c1"),
    WALLET_21("0x742d35Cc6634C0532925a3b844Bc454e4438f4d"),
    WALLET_22("0x5A0b54D5dc17e0AadC383d2db43B0a0D3E0294c"),
    WALLET_23("0x742d35Cc6634C0532925a3b844Bc454e4438f4e");

    private final String wallet;

    DataWalletAddressInvalid(String wallet) {
        this.wallet = wallet;
    }

    public String getAddress() {
        return wallet;
    }
}
