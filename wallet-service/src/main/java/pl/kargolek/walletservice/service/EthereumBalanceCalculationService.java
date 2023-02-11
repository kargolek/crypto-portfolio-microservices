package pl.kargolek.walletservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.kargolek.walletservice.dto.UserWallet;
import pl.kargolek.walletservice.dto.WalletMultiBalance;
import pl.kargolek.walletservice.mapper.UserWalletMapper;
import pl.kargolek.walletservice.util.CryptoType;
import pl.kargolek.walletservice.util.CryptoUnitConvert;
import pl.kargolek.walletservice.validation.MultiWalletAddressValidator;

import java.util.List;

/**
 * @author Karol Kuta-Orlowicz
 */
@Service
public class EthereumBalanceCalculationService extends BalanceCalculationService<WalletMultiBalance> {

    @Autowired
    private WalletBalanceService<WalletMultiBalance> walletBalanceService;

    @Autowired
    private CryptoUnitConvert cryptoUnitConvert;

    @Autowired
    private MultiWalletAddressValidator walletsValidator;

    private final UserWalletMapper mapper = UserWalletMapper.INSTANCE;

    @Value("${api.etherscan.maxWalletsPerRequest}")
    private String maxWalletsPerRequest;

    public EthereumBalanceCalculationService(@Autowired WalletBalanceService<WalletMultiBalance> walletBalanceService) {
        super(walletBalanceService);
    }

    @Override
    public List<UserWallet> callWalletsBalanceCalculation(String wallets) {
        walletsValidator.isValidAddresses(wallets);
        var tokenDTO = this.getCryptoPrice(CryptoType.ETHEREUM);
        return this.walletSubListsStream(wallets, Integer.parseInt(maxWalletsPerRequest))
                .parallel()
                .map(this::getWalletsBalances)
                .flatMap(List::stream)
                .map(mapper::toUserWallet)
                .map(userWallet -> this.calculateUserBalances(userWallet, tokenDTO, CryptoType.ETHEREUM))
                .map(userWallet -> mapper.updateUserWallet(userWallet, tokenDTO))
                .toList();
    }

}
