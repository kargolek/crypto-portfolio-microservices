package pl.kargolek.walletservice.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Karol Kuta-Orlowicz
 */
@Data
@Accessors(chain = true)
@ToString
public class PlatformDTO {

    private String platform;
    private String tokenAddress;

}
