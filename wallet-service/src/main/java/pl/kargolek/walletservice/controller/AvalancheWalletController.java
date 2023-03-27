package pl.kargolek.walletservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kargolek.walletservice.dto.UserWallet;
import pl.kargolek.walletservice.service.balance.avalanche.AvalancheBalanceService;

/**
 * @author Karol Kuta-Orlowicz
 */
@RestController
@RequestMapping(path = "api/v1/wallet/avax")
public class AvalancheWalletController {

    @Autowired
    private AvalancheBalanceService avalancheBalanceService;

    @GetMapping("/balance")
    public ResponseEntity<UserWallet> getAvaxWalletsBalance(@RequestParam(name = "wallets") String wallets){
        var userWallets = avalancheBalanceService.getMultiBalance(wallets);
        return new ResponseEntity<>(userWallets, HttpStatus.OK);
    }
}
