package pl.kargolek.walletservice.service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import pl.kargolek.walletservice.client.CryptoPriceServiceClient;
import pl.kargolek.walletservice.dto.TokenDTO;
import pl.kargolek.walletservice.dto.UserBalance;
import pl.kargolek.walletservice.dto.UserWallet;
import pl.kargolek.walletservice.exception.NoSuchCryptoPriceDataException;
import pl.kargolek.walletservice.util.CryptoType;
import pl.kargolek.walletservice.util.CryptoUnitConvert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Karol Kuta-Orlowicz
 */
public abstract class BalanceCalculationService<T> {

    @Autowired
    private CryptoPriceServiceClient cryptoPriceServiceClient;

    @Autowired
    private CryptoUnitConvert convertUnit;

    private final WalletBalanceService<T> walletBalanceService;

    public BalanceCalculationService(WalletBalanceService<T> walletBalanceService) {
        this.walletBalanceService = walletBalanceService;
    }

    protected Stream<List<String>> walletSubListsStream(String wallets, int maxWalletsPerRequest) {
        var walletsList = Arrays.asList(wallets.trim().split(","));
        return Streams.stream(Iterables.partition(walletsList, maxWalletsPerRequest))
                .map(mapped -> List.of(String.join(",", mapped)));
    }

    protected abstract List<UserWallet> callWalletsBalanceCalculation(String wallets);

    protected List<T> getWalletsBalances(List<String> wallets) {
        return wallets.stream()
                .parallel()
                .map(this.walletBalanceService::getMultiWalletBalance)
                .collect(Collectors.toList());
    }

    protected TokenDTO getCryptoPrice(CryptoType cryptoType) {
        var name = cryptoType.getName();
        return cryptoPriceServiceClient.getTokensByName(List.of(name)).stream()
                .filter(tokenDTO -> tokenDTO.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchCryptoPriceDataException(name));
    }

    protected UserWallet calculateUserBalances(UserWallet userWallet, TokenDTO tokenDTO, CryptoType cryptoType) {
        var userBalances = userWallet.getBalance()
                .stream()
                .map(userBalance -> this.convertUnit(userBalance, cryptoType))
                .map(userBalance -> calculateUserBalance(userBalance, tokenDTO))
                .toList();
        return userWallet.setBalance(userBalances);
    }

    private UserBalance convertUnit(UserBalance userBalance, CryptoType cryptoType) {
        var convertedQuantity = this.convertUnit.convert(userBalance.getQuantity().toPlainString(), cryptoType);
        return userBalance.setQuantity(convertedQuantity);
    }

    private UserBalance calculateUserBalance(UserBalance userBalance, TokenDTO tokenDTO) {
        var quantity = userBalance.getQuantity();
        var priceDTO = tokenDTO.getPrice();

        var balance = this.calculateBalance(quantity, priceDTO.getPriceCurrent());
        var balance1h = this.calcBalancePercentChange(balance, priceDTO.getPercentChange1h());
        var balance24h = this.calcBalancePercentChange(balance, priceDTO.getPercentChange24h());
        var balance7d = this.calcBalancePercentChange(balance, priceDTO.getPercentChange7d());
        var balance30d = this.calcBalancePercentChange(balance, priceDTO.getPercentChange30d());
        var balance60d = this.calcBalancePercentChange(balance, priceDTO.getPercentChange60d());
        var balance90d = this.calcBalancePercentChange(balance, priceDTO.getPercentChange90d());

        return userBalance
                .setBalance(balance)
                .setBalance1h(balance1h)
                .setBalance24h(balance24h)
                .setBalance7d(balance7d)
                .setBalance30d(balance30d)
                .setBalance60d(balance60d)
                .setBalance90d(balance90d);
    }

    private BigDecimal calculateBalance(BigDecimal walletBalance, BigDecimal currentPrice) {
        var currentPriceOptional = Optional.ofNullable(currentPrice).orElseThrow(NoSuchCryptoPriceDataException::new);
        var balance = walletBalance.multiply(currentPriceOptional);
        return balance.setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calcBalancePercentChange(BigDecimal balance, BigDecimal percent) {
        var percentOptional = Optional.ofNullable(percent).orElseThrow(NoSuchCryptoPriceDataException::new);
        var diff = balance.multiply(percentOptional.divide(new BigDecimal("100")));
        var balance_change = balance.add(diff);
        return balance_change.setScale(2, RoundingMode.HALF_EVEN);
    }
}
