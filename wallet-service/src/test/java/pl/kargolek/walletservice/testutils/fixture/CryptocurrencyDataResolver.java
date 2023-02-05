package pl.kargolek.walletservice.testutils.fixture;

import pl.kargolek.walletservice.dto.CryptocurrencyDTO;
import pl.kargolek.walletservice.dto.PlatformDTO;
import pl.kargolek.walletservice.dto.PriceDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Karol Kuta-Orlowicz
 */
public class CryptocurrencyDataResolver {

    private final LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 21, 23, 12, 59, 999_999_999);

    public CryptocurrencyDTO getCryptocurrencyBTC() {
        return new CryptocurrencyDTO()
                .setId(1L)
                .setName("Bitcoin")
                .setSymbol("BTC")
                .setCoinMarketId(1L)
                .setPlatform(new PlatformDTO()
                        .setPlatform("platform_btc")
                        .setTokenAddress("token_address_btc"))
                .setPrice(new PriceDTO()
                        .setId(1L)
                        .setPriceCurrent(new BigDecimal("20000.5"))
                        .setPercentChange1h(new BigDecimal("0.5"))
                        .setPercentChange24h(new BigDecimal("1.0"))
                        .setPercentChange7d(new BigDecimal("1.5"))
                        .setPercentChange30d(new BigDecimal("2.0"))
                        .setPercentChange60d(new BigDecimal("2.5"))
                        .setPercentChange90d(new BigDecimal("3.0"))
                        .setLastUpdate(localDateTime)
                ).setLastUpdate(localDateTime);
    }

    public CryptocurrencyDTO getCryptocurrencyETH() {
        return new CryptocurrencyDTO()
                .setId(2L)
                .setName("Ethereum")
                .setSymbol("ETH")
                .setCoinMarketId(1027L)
                .setPlatform(new PlatformDTO()
                        .setPlatform("platform_eth")
                        .setTokenAddress("token_address_eth"))
                .setPrice(new PriceDTO()
                        .setId(2L)
                        .setPriceCurrent(new BigDecimal("1800.5"))
                        .setPercentChange1h(new BigDecimal("10.5"))
                        .setPercentChange24h(new BigDecimal("11.0"))
                        .setPercentChange7d(new BigDecimal("11.5"))
                        .setPercentChange30d(new BigDecimal("12.0"))
                        .setPercentChange60d(new BigDecimal("12.5"))
                        .setPercentChange90d(new BigDecimal("13.0"))
                        .setLastUpdate(localDateTime)
                ).setLastUpdate(localDateTime);
    }

    public CryptocurrencyDTO[] getCryptocurrencies() {
        return new CryptocurrencyDTO[]{getCryptocurrencyBTC(), getCryptocurrencyETH()};
    }
}
