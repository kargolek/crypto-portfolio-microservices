package pl.kargolek.walletservice.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Karol Kuta-Orlowicz
 */
@Data
@Accessors(chain = true)
public class WalletBalance {
    private String account;
    private String balance;
}
