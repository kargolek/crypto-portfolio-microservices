package pl.kargolek.walletservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pl.kargolek.walletservice.dto.CryptocurrencyDTO;

import java.util.List;

/**
 * @author Karol Kuta-Orlowicz
 */
@FeignClient(name = "crypto-price-service", fallbackFactory = CryptocurrencyServiceClientFallbackFactory.class)
public interface CryptocurrencyServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "api/v1/cryptocurrency")
    List<CryptocurrencyDTO> getCryptocurrencies();

    @RequestMapping(method = RequestMethod.GET, value = "api/v1/cryptocurrency")
    List<CryptocurrencyDTO> getCryptocurrenciesByName(@RequestParam("name") List<String> name);


}
