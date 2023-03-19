package pl.kargolek.walletservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kargolek.walletservice.dto.UserWallet;
import pl.kargolek.walletservice.service.PolygonBalanceCalculationService;

/**
 * @author Karol Kuta-Orlowicz
 */
@RestController
@RequestMapping(path = "api/v1/wallet/matic")
@AllArgsConstructor
public class PolygonWalletController {

    @Autowired
    private PolygonBalanceCalculationService polygonBalanceCalculationService;

    @GetMapping("/balance")
    public ResponseEntity<UserWallet> getEthWalletsBalance(@RequestParam(name = "wallets") String wallets){
        var userWallets = polygonBalanceCalculationService.callWalletsBalanceCalculation(wallets);
        return new ResponseEntity<>(userWallets, HttpStatus.OK);
    }

}
