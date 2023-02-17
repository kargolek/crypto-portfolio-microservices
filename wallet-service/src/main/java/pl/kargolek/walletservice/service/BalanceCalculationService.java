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

    @Autowired
    private QuotaCalculatorService quotaCalculatorService;

    private final WalletBalanceService<T> walletBalanceService;

    public BalanceCalculationService(WalletBalanceService<T> walletBalanceService) {
        this.walletBalanceService = walletBalanceService;
    }

    protected abstract List<UserWallet> callWalletsBalanceCalculation(String wallets);

    protected Stream<List<String>> walletSubListsStream(String wallets, int maxWalletsPerRequest) {
        var walletsList = Arrays.asList(wallets.trim().split(","));
        return Streams.stream(Iterables.partition(walletsList, maxWalletsPerRequest))
                .map(mapped -> List.of(String.join(",", mapped)));
    }

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

    protected List<UserWallet> mergeUserWallet(List<UserWallet> userWallets){
        return userWallets.stream()
                .collect(Collectors.groupingBy(UserWallet::getName))
                .values().stream()
                .map(group -> group.stream().reduce((o1, o2) -> new UserWallet(o1.getName(), o1.getSymbol(),
                        Stream.concat(o1.getBalance().stream(), o2.getBalance().stream())
                                .collect(Collectors.toList()))))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
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

        var balance = quotaCalculatorService.calcBalanceFromPrice(quantity, priceDTO.getPriceCurrent());

        var balance1h = quotaCalculatorService.calcBalancePercentChange(balance, priceDTO.getPercentChange1h());
        var balance24h = quotaCalculatorService.calcBalancePercentChange(balance, priceDTO.getPercentChange24h());
        var balance7d = quotaCalculatorService.calcBalancePercentChange(balance, priceDTO.getPercentChange7d());
        var balance30d = quotaCalculatorService.calcBalancePercentChange(balance, priceDTO.getPercentChange30d());
        var balance60d = quotaCalculatorService.calcBalancePercentChange(balance, priceDTO.getPercentChange60d());
        var balance90d = quotaCalculatorService.calcBalancePercentChange(balance, priceDTO.getPercentChange90d());

        return userBalance
                .setBalance(balance)
                .setBalance1h(balance1h)
                .setBalance24h(balance24h)
                .setBalance7d(balance7d)
                .setBalance30d(balance30d)
                .setBalance60d(balance60d)
                .setBalance90d(balance90d);
    }
}
