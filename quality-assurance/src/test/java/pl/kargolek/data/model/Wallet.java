package pl.kargolek.data.model;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Karol Kuta-Orlowicz
 */
@Builder
@Getter
public class Wallet {
    private String address;
    private String amount;
}
