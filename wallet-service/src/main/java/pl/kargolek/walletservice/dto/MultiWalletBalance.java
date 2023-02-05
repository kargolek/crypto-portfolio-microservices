package pl.kargolek.walletservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.experimental.Accessors;
import pl.kargolek.walletservice.mapper.DeserializerWalletBalance;

/**
 * @author Karol Kuta-Orlowicz
 */
@Data
@Accessors(chain = true)
@JsonDeserialize(using = DeserializerWalletBalance.class)
public class MultiWalletBalance {
    private String status;
    private String message;
    private WalletBalance[] result;
}
