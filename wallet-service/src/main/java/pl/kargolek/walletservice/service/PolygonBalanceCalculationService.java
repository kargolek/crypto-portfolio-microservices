package pl.kargolek.walletservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.kargolek.walletservice.dto.UserWallet;
import pl.kargolek.walletservice.dto.WalletMultiBalance;
import pl.kargolek.walletservice.mapper.UserWalletMapper;
import pl.kargolek.walletservice.util.CryptoType;
import pl.kargolek.walletservice.validation.MultiWalletAddressValidator;

import java.util.List;

/**
 * @author Karol Kuta-Orlowicz
 */
@Service
public class PolygonBalanceCalculationService extends BalanceCalculationService<WalletMultiBalance> {

    @Autowired
    private MultiWalletAddressValidator walletsValidator;

    private final UserWalletMapper mapper = UserWalletMapper.INSTANCE;

    @Value("${api.etherscan.maxWalletsPerRequest}")
    private String maxWalletsPerRequest;

    public PolygonBalanceCalculationService(@Autowired PolygonMultiWalletService polygonMultiWalletService) {
        super(polygonMultiWalletService);
    }

    @Override
    public UserWallet callWalletsBalanceCalculation(String wallets) {
        walletsValidator.isValidAddresses(wallets);
        var tokenDTO = this.getCryptoPrice(CryptoType.POLYGON);
        var userWallets = this.walletSubListsStream(wallets, Integer.parseInt(maxWalletsPerRequest))
                .parallel()
                .map(this::getWalletsBalances)
                .flatMap(List::stream)
                .map(mapper::toUserWallet)
                .map(userWallet -> this.calculateUserBalances(userWallet, tokenDTO, CryptoType.POLYGON))
                .map(userWallet -> mapper.updateUserWallet(userWallet, tokenDTO))
                .toList();
        var mergedUserWallet = this.mergeUserWallet(userWallets);
        return this.calculateTotal(mergedUserWallet);
    }
}
