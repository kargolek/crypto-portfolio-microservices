package pl.kargolek.cryptopriceservice.service;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import pl.kargolek.cryptopriceservice.exception.PriceNotFoundException;
import pl.kargolek.cryptopriceservice.extension.MySqlTestContainerExtension;
import pl.kargolek.cryptopriceservice.model.Cryptocurrency;
import pl.kargolek.cryptopriceservice.model.Price;
import pl.kargolek.cryptopriceservice.repository.PriceRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Karol Kuta-Orlowicz
 */
@ExtendWith(MySqlTestContainerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Tag("IntegrationTest")
@ActiveProfiles("test")
class PriceServiceIntegrationTest {

    @Autowired
    private PriceService underTestService;
    @Autowired
    private CryptocurrencyService cryptocurrencyService;

    @Autowired
    private PriceRepository priceRepository;

    private Cryptocurrency cryptocurrency;

    @BeforeEach
    public void setup() {
        cryptocurrency = Cryptocurrency.builder()
                .name(RandomStringUtils.random(20))
                .symbol(RandomStringUtils.random(8))
                .coinMarketId(RandomUtils.nextLong())
                .build();
    }

    @Test
    void whenUpdatePrice_thenPriceShouldUpdatedSuccessful() {
        var cryptoEntity = cryptocurrencyService.addCryptocurrency(cryptocurrency);
        var priceEntity = cryptoEntity.getPrice();
        priceEntity.setPriceCurrent(new BigDecimal("100100100100.1002"));

        var expected = underTestService.updatePrices(List.of(priceEntity));

        assertThat(expected)
                .extracting(
                        Price::getId,
                        Price::getPriceCurrent,
                        Price::getPercentChange1h
                ).containsExactly(
                        Tuple.tuple(
                                priceEntity.getId(),
                                new BigDecimal("100100100100.1002"),
                                null
                        )
                );

        assertThat(expected.stream().map(Price::getLastUpdate).findFirst().orElseThrow())
                .isEqualToIgnoringNanos(priceEntity.getLastUpdate());
    }

    @Test
    void whenUpdatePricesByEmptyList_thenReturnEmptyEntitiesList() {
        var cryptoEntity = cryptocurrencyService.addCryptocurrency(cryptocurrency);
        var priceEntity = cryptoEntity.getPrice();
        priceEntity.setPriceCurrent(new BigDecimal("100100100100.1002"));

        var expected = underTestService.updatePrices(List.of());

        assertThat(expected).hasSize(0);
    }

    @Test
    void whenUpdatePricesByNotExistPrice_thenThrowPriceNotFoundExc() {
        var cryptoEntity = cryptocurrencyService.addCryptocurrency(cryptocurrency);
        var priceEntity = cryptoEntity.getPrice();
        priceEntity.setPriceCurrent(new BigDecimal("100100100100.1002"));

        var priceNotExist = Price.builder()
                .id(120L)
                .priceCurrent(new BigDecimal("120.0"))
                .cryptocurrency(cryptoEntity)
                .build();

        assertThatThrownBy(() -> underTestService.updatePrices(List.of(priceNotExist)))
                .isInstanceOf(PriceNotFoundException.class);
    }
}