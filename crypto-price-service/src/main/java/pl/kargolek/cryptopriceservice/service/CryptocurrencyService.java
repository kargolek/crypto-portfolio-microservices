package pl.kargolek.cryptopriceservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kargolek.cryptopriceservice.dto.client.CryptocurrencyMapDTO;
import pl.kargolek.cryptopriceservice.dto.client.MapDataDTO;
import pl.kargolek.cryptopriceservice.exception.CryptocurrencyNotFoundException;
import pl.kargolek.cryptopriceservice.model.Cryptocurrency;
import pl.kargolek.cryptopriceservice.model.Price;
import pl.kargolek.cryptopriceservice.repository.CryptocurrencyRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CryptocurrencyService {

    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final MarketApiClientService marketApiClientService;

    public Cryptocurrency addCryptocurrency(Cryptocurrency cryptocurrency) {
        if (cryptocurrency.getCoinMarketId() == null)
            cryptocurrency.setCoinMarketId(getCoinMarketId(cryptocurrency).orElseThrow());

        var price = Price.builder()
                .cryptocurrency(cryptocurrency)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        cryptocurrency.setPrice(price);
        cryptocurrency.setLastUpdate(LocalDateTime.now(ZoneOffset.UTC));

        log.info(String.format("Adding new cryptocurrency name: %s, coinMarketCapId: %s",
                cryptocurrency.getName(),
                cryptocurrency.getCoinMarketId()));
        return cryptocurrencyRepository.save(cryptocurrency);
    }

    public List<Cryptocurrency> getCryptocurrencies() {
        log.info("Finding all cryptocurrencies");
        return cryptocurrencyRepository.findAll();
    }

    public Cryptocurrency getById(Long id) {
        log.info(String.format("Finding cryptocurrency by id: %d", id));
        return cryptocurrencyRepository.findById(id)
                .orElseThrow(() -> new CryptocurrencyNotFoundException(id));
    }

    public Cryptocurrency updateCryptocurrency(Long id, Cryptocurrency cryptocurrency) {
        var cryptocurrencyToUpdate = cryptocurrencyRepository.findById(id)
                .orElseThrow(() -> new CryptocurrencyNotFoundException(id));

        cryptocurrencyToUpdate.setName(cryptocurrency.getName());
        cryptocurrencyToUpdate.setSymbol(cryptocurrency.getSymbol());
        cryptocurrencyToUpdate.setCoinMarketId(cryptocurrency.getCoinMarketId());
        cryptocurrencyToUpdate.setLastUpdate(LocalDateTime.now(ZoneOffset.UTC));

        log.info(String.format("Updating cryptocurrency with id: %d, new name: %s, symbol: %s, coinMarketId: %d",
                cryptocurrencyToUpdate.getId(),
                cryptocurrency.getName(),
                cryptocurrency.getSymbol(),
                cryptocurrency.getCoinMarketId()));
        return cryptocurrencyRepository.save(cryptocurrencyToUpdate);
    }

    public void deleteCryptocurrency(Long id) {
        log.info(String.format("Deleting cryptocurrency id: %d", id));
        cryptocurrencyRepository.deleteById(id);
    }

    public List<Cryptocurrency> getByName(List<String> name) {
        log.info(String.format("Finding cryptocurrencies by name: %s", name));
        return cryptocurrencyRepository.findByName(name);
    }

    private Optional<Long> getCoinMarketId(Cryptocurrency cryptocurrency){
        return marketApiClientService.getCryptoMarketIdBySymbol(cryptocurrency.getSymbol())
                .map(MapDataDTO::getData)
                .orElseThrow()
                .stream()
                .filter(cryptocurrencyMapDTO -> cryptocurrencyMapDTO.getSymbol().equalsIgnoreCase(cryptocurrency.getSymbol()))
                .map(CryptocurrencyMapDTO::getCoinMarketId)
                .findFirst();
    }
}
