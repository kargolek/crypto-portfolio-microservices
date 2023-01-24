package pl.kargolek.cryptopriceservice.dto.controller;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author Karol Kuta-Orlowicz
 */
@Data
@Accessors(chain = true)
public class CryptocurrencyPostDTO {

    @NotEmpty(message = "Name cannot be an empty")
    @Size(min = 2, max = 100, message = "Name length exceeds range [2,100]")
    private String name;

    @NotEmpty(message = "Symbol cannot be an empty")
    @Size(min = 2, max = 20, message = "Symbol length exceeds range [2,20]")
    private String symbol;

    @Min(value = 1, message = "Coin market ID must be greater than 0")
    private Long coinMarketId;
}
