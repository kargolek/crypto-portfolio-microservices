package pl.kargolek.cryptopriceservice.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CryptocurrencyDTO {
    private Long id;
    private String name;
    private String symbol;
    private Long coinMarketId;
    private LocalDateTime lastUpdate;

    @JsonProperty("price")
    private PriceDTO priceDTO;
}
