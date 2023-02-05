package pl.kargolek.walletservice.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import pl.kargolek.walletservice.client.CryptocurrencyServiceClient;
import pl.kargolek.walletservice.dto.CryptocurrencyDTO;

import java.util.List;

/**
 * @author Karol Kuta-Orlowicz
 */
@Component
@Slf4j
public class CryptocurrencyServiceClientFallbackFactory implements FallbackFactory<CryptocurrencyServiceClient> {

    @Override
    public CryptocurrencyServiceClient create(Throwable cause) {
        return new CryptocurrencyServiceClient() {
            @Override
            public List<CryptocurrencyDTO> getCryptocurrencies() {
                log.error("Error occurred in cryptocurrency price service client, cause:{}", cause.getMessage());
                return List.of();
            }

            @Override
            public List<CryptocurrencyDTO> getCryptocurrenciesByName(List<String> name) {
                log.error("Error occurred in cryptocurrency price service client, cause:{}", cause.getMessage());
                return List.of();
            }
        };
    }
}
