package pl.kargolek.walletservice.service;

import org.springframework.stereotype.Service;
import pl.kargolek.walletservice.dto.UserBalance;
import pl.kargolek.walletservice.dto.UserTotalBalance;
import pl.kargolek.walletservice.dto.UserWallet;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Karol Kuta-Orlowicz
 */

@Service
public class TotalCalculatorService {

    public UserTotalBalance calcQuantityBalance(UserWallet userWallet) {
        var totalQuantity = userWallet.getBalance().stream()
                .map(UserBalance::getQuantity)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalBalance = userWallet.getBalance().stream()
                .map(UserBalance::getBalance)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new UserTotalBalance(totalQuantity, totalBalance);
    }
}
