package pl.kargolek.cryptopriceservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import pl.kargolek.cryptopriceservice.dto.client.CryptocurrencyMapDTO;
import pl.kargolek.cryptopriceservice.dto.client.MapDataDTO;
import pl.kargolek.cryptopriceservice.exception.CryptocurrencyNotFoundException;
import pl.kargolek.cryptopriceservice.model.Cryptocurrency;
import pl.kargolek.cryptopriceservice.repository.CryptocurrencyRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
class CryptocurrencyServiceUnitTest {

    @Mock
    private CryptocurrencyRepository cryptocurrencyRepository;

    @Mock
    private MarketApiClientService marketApiClientService;

    @InjectMocks
    private CryptocurrencyService underTestService;

    private Cryptocurrency cryptocurrency;
    private MapDataDTO mapDataDTO;

    @BeforeEach
    public void setup() {
        cryptocurrency = Cryptocurrency.builder()
                .id(1L)
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(RandomUtils.nextLong())
                .lastUpdate(LocalDateTime.now())
                .build();

        var cryptocurrencyMapDTO = new CryptocurrencyMapDTO()
                .setName("Bitcoin")
                .setSymbol("BTC")
                .setCoinMarketId(1L);

        mapDataDTO = new MapDataDTO()
                .setData(List.of(cryptocurrencyMapDTO));
    }

    @Test
    void whenAddCryptoCurrency_thenReturnEntity() {
        when(cryptocurrencyRepository.save(cryptocurrency))
                .thenReturn(cryptocurrency);

        var expected = underTestService.addCryptocurrency(cryptocurrency);

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId,
                        Cryptocurrency::getLastUpdate)
                .containsExactly(
                        cryptocurrency.getId(),
                        cryptocurrency.getName(),
                        cryptocurrency.getSymbol(),
                        cryptocurrency.getCoinMarketId(),
                        cryptocurrency.getLastUpdate()
                );
    }

    @Test
    void whenAddCryptoCurrencyWithoutMarketId_thenReturnEntity() {
        when(marketApiClientService.getCryptoMarketIdBySymbol("BTC"))
                .thenReturn(Optional.of(mapDataDTO));

        when(cryptocurrencyRepository.save(cryptocurrency))
                .thenReturn(cryptocurrency);

        cryptocurrency.setCoinMarketId(null);

        var expected = underTestService.addCryptocurrency(cryptocurrency);

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId,
                        Cryptocurrency::getLastUpdate)
                .containsExactly(
                        cryptocurrency.getId(),
                        cryptocurrency.getName(),
                        cryptocurrency.getSymbol(),
                        cryptocurrency.getCoinMarketId(),
                        cryptocurrency.getLastUpdate()
                );
    }

    @Test
    void whenGetAllCryptocurrencies_thenReturnListCryptocurrencies() {
        when(cryptocurrencyRepository.findAll())
                .thenReturn(Collections.singletonList(cryptocurrency));

        var expected = underTestService.getCryptocurrencies();

        assertThat(expected)
                .hasSize(1)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getCoinMarketId)
                .containsExactly(
                        tuple(1L,
                                "Bitcoin",
                                cryptocurrency.getCoinMarketId()
                        )
                );
    }

    @Test
    void whenGetCryptocurrencyById_thenReturnExpectedCryptocurrency() {
        when(cryptocurrencyRepository.findById(1L))
                .thenReturn(Optional.of(cryptocurrency));

        var expected = underTestService.getById(1L);

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId,
                        Cryptocurrency::getLastUpdate)
                .containsExactly(
                        cryptocurrency.getId(),
                        cryptocurrency.getName(),
                        cryptocurrency.getSymbol(),
                        cryptocurrency.getCoinMarketId(),
                        cryptocurrency.getLastUpdate()
                );
    }

    @Test
    void whenGetCryptocurrencyByNotExistId_thenThrowCryptocurrencyNotFoundExc() {
        var id = 1L;
        when(cryptocurrencyRepository.findById(id))
                .thenThrow(new CryptocurrencyNotFoundException(id));

        assertThatThrownBy(() -> underTestService.getById(id))
                .isInstanceOf(CryptocurrencyNotFoundException.class)
                .hasMessage("Unable to find cryptocurrency with id: " + id);
    }

    @Test
    void whenUpdateCryptocurrency_thenShouldUpdateProperValuesOfEntity() {
        var cryptoCurrencyUpdate = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(RandomUtils.nextLong())
                .build();

        when(cryptocurrencyRepository.findById(1L))
                .thenReturn(Optional.of(cryptocurrency));
        when(cryptocurrencyRepository.save(cryptocurrency))
                .thenReturn(cryptocurrency);

        var expected = underTestService.updateCryptocurrency(1L, cryptoCurrencyUpdate);

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol
                )
                .containsExactly("Ethereum", "ETH");
    }

    @Test
    void whenNotUpdateCryptoById_thenThrowCryptocurrencyNotFoundExc() {
        var cryptoCurrencyUpdate = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(RandomUtils.nextLong())
                .build();

        assertThatThrownBy(() -> underTestService.updateCryptocurrency(1L, cryptoCurrencyUpdate))
                .isInstanceOf(CryptocurrencyNotFoundException.class)
                .hasMessage("Unable to find cryptocurrency with id: 1");
    }

    @Test
    void whenDeleteCryptocurrencyById_thenDeleteByIdRepositoryMethodPerform() {
        var id = 1L;

        underTestService.deleteCryptocurrency(id);

        verify(cryptocurrencyRepository).deleteById(id);
    }

    @Test
    void whenGetCryptocurrencyByName_thenReturnCryptocurrencySuccessful() {
        var cryptoName = "Bitcoin";
        when(cryptocurrencyRepository.findByName(List.of(cryptoName)))
                .thenReturn(List.of(cryptocurrency));

        var expected = underTestService.getByName(List.of(cryptoName));

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId,
                        Cryptocurrency::getLastUpdate)
                .containsExactly(
                        tuple(
                                cryptocurrency.getId(),
                                cryptocurrency.getName(),
                                cryptocurrency.getSymbol(),
                                cryptocurrency.getCoinMarketId(),
                                cryptocurrency.getLastUpdate()
                        ));
    }

    @Test
    void whenNotFoundCryptocurrencyByName_thenThrowCryptocurrencyNotFoundExc() {
        var cryptoName = "Ethereum";

        when(cryptocurrencyRepository.findByName(List.of(cryptoName)))
                .thenThrow(new CryptocurrencyNotFoundException(cryptoName));

        assertThatThrownBy(() -> underTestService.getByName(List.of(cryptoName)))
                .isInstanceOf(CryptocurrencyNotFoundException.class)
                .hasMessage("Unable to find cryptocurrency with name: " + cryptoName);
    }
}