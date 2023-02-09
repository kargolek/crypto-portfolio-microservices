package pl.kargolek.walletservice.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Karol Kuta-Orlowicz
 */
@Data
@Accessors(chain = true)
public class UserWallet {
    private String name;
    private String symbol;
    private List<UserBalance> balance;
}
