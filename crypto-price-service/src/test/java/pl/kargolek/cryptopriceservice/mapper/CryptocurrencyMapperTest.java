package pl.kargolek.cryptopriceservice.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kargolek.cryptopriceservice.dto.client.CryptocurrencyQuoteDTO;
import pl.kargolek.cryptopriceservice.dto.client.PriceQuoteDTO;
import pl.kargolek.cryptopriceservice.dto.controller.CryptocurrencyPostDTO;
import pl.kargolek.cryptopriceservice.dto.model.CryptocurrencyDTO;
import pl.kargolek.cryptopriceservice.dto.model.PriceDTO;
import pl.kargolek.cryptopriceservice.mapper.util.CycleAvoidingMappingContext;
import pl.kargolek.cryptopriceservice.model.Cryptocurrency;
import pl.kargolek.cryptopriceservice.model.Price;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karol Kuta-Orlowicz
 */
@Tag("UnitTest")
class CryptocurrencyMapperTest {
    private static final String FIAT_CURRENCY_USD = "USD";
    private final CryptocurrencyMapper mapper = Mappers.getMapper(CryptocurrencyMapper.class);
    private Cryptocurrency cryptocurrency;
    private CryptocurrencyDTO cryptocurrencyDTO;
    private PriceDTO priceDTO;
    private CryptocurrencyQuoteDTO cryptocurrencyQuoteDTO;
    private PriceQuoteDTO priceQuoteDTO;

    private CryptocurrencyPostDTO cryptocurrencyPostDTO;

    @BeforeEach
    void setUp() {
        Price price = Price.builder()
                .id(1L)
                .priceCurrent(new BigDecimal("18000.54321"))
                .percentChange1h(new BigDecimal("0.5"))
                .build();

        cryptocurrency = Cryptocurrency.builder()
                .id(1L)
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(1L)
                .price(price)
                .build();
        price.setCryptocurrency(cryptocurrency);

        priceDTO = new PriceDTO()
                .setId(2L)
                .setPriceCurrent(new BigDecimal("2000.2000"))
                .setPercentChange1h(new BigDecimal("0.0001"));

        cryptocurrencyDTO = new CryptocurrencyDTO()
                .setId(2L)
                .setName("Ethereum")
                .setSymbol("ETH")
                .setCoinMarketId(1027L)
                .setPriceDTO(priceDTO);

        priceQuoteDTO = new PriceQuoteDTO()
                .setPriceCurrent(new BigDecimal("0.90"))
                .setPercentChange1h(new BigDecimal("1.5"));

        cryptocurrencyQuoteDTO = new CryptocurrencyQuoteDTO()
                .setName("MATIC")
                .setSymbol("MATIC")
                .setCoinMarketId(2045L)
                .setQuote(Map.of(FIAT_CURRENCY_USD, priceQuoteDTO));

        cryptocurrencyPostDTO = new CryptocurrencyPostDTO()
                .setName("MATIC")
                .setSymbol("MATIC")
                .setCoinMarketId(2045L);
    }

    @Test
    void whenMapDto_thenReturnPriceEntity() {
        var entity = mapper.mapDtoToPriceEntity(priceDTO);

        assertThat(entity).extracting(
                Price::getId,
                Price::getPriceCurrent,
                Price::getPercentChange1h
        ).containsExactly(
                priceDTO.getId(),
                priceDTO.getPriceCurrent(),
                priceDTO.getPercentChange1h()
        );
    }

    @Test
    void whenMapQuotes_thenReturnPriceDTO() {
        var dto = mapper.mapQuoteDtoToPriceDto(priceQuoteDTO);

        assertThat(dto).extracting(
                PriceDTO::getId,
                PriceDTO::getPriceCurrent,
                PriceDTO::getPercentChange1h
        ).containsExactly(
                null,
                cryptocurrencyQuoteDTO.getQuote().get(FIAT_CURRENCY_USD).getPriceCurrent(),
                cryptocurrencyQuoteDTO.getQuote().get(FIAT_CURRENCY_USD).getPercentChange1h()
        );
    }

    @Test
    void whenMapEntity_thenReturnCryptocurrencyDTO() {
        var dto = mapper.mapEntityToCryptocurrencyDto(cryptocurrency, new CycleAvoidingMappingContext());

        assertThat(dto).extracting(
                CryptocurrencyDTO::getId,
                CryptocurrencyDTO::getName,
                CryptocurrencyDTO::getSymbol,
                CryptocurrencyDTO::getCoinMarketId
        ).containsExactly(
                cryptocurrency.getId(),
                cryptocurrency.getName(),
                cryptocurrency.getSymbol(),
                cryptocurrency.getCoinMarketId()
        );

        assertThat(dto.getPriceDTO())
                .extracting(
                        PriceDTO::getId,
                        PriceDTO::getPriceCurrent,
                        PriceDTO::getPercentChange1h
                ).containsExactly(
                        cryptocurrency.getPrice().getId(),
                        cryptocurrency.getPrice().getPriceCurrent(),
                        cryptocurrency.getPrice().getPercentChange1h()
                );
    }

    @Test
    void whenMapQuotes_thenReturnCryptocurrencyDto() {
        var dto = mapper.mapQuoteDtoToCryptocurrencyDto(cryptocurrencyQuoteDTO);

        assertThat(dto).extracting(
                CryptocurrencyDTO::getId,
                CryptocurrencyDTO::getName,
                CryptocurrencyDTO::getSymbol,
                CryptocurrencyDTO::getCoinMarketId
        ).containsExactly(
                null,
                cryptocurrencyQuoteDTO.getName(),
                cryptocurrencyQuoteDTO.getSymbol(),
                cryptocurrencyQuoteDTO.getCoinMarketId()
        );

        assertThat(dto.getPriceDTO()).extracting(
                PriceDTO::getId,
                PriceDTO::getPriceCurrent,
                PriceDTO::getPercentChange1h
        ).containsExactly(
                null,
                cryptocurrencyQuoteDTO.getQuote().get(FIAT_CURRENCY_USD).getPriceCurrent(),
                cryptocurrencyQuoteDTO.getQuote().get(FIAT_CURRENCY_USD).getPercentChange1h()
        );
    }

    @Test
    void whenUpdateDtoByQuotes_thenReturnUpdatedPriceDto() {
        var dto = mapper.updateDtoByPriceQuoteDto(priceDTO, priceQuoteDTO);

        assertThat(dto).extracting(
                PriceDTO::getId,
                PriceDTO::getPriceCurrent,
                PriceDTO::getPercentChange1h
        ).containsExactly(
                priceDTO.getId(),
                cryptocurrencyQuoteDTO.getQuote().get(FIAT_CURRENCY_USD).getPriceCurrent(),
                cryptocurrencyQuoteDTO.getQuote().get(FIAT_CURRENCY_USD).getPercentChange1h()
        );
    }

    @Test
    void whenUpdateDtoByResponse_thenReturnUpdatedCryptocurrencyDto() {
        var dtoUpdated = mapper.updateDtoByCryptocurrencyQuoteDto(cryptocurrencyDTO,
                cryptocurrencyQuoteDTO);

        assertThat(dtoUpdated).extracting(
                CryptocurrencyDTO::getId,
                CryptocurrencyDTO::getName,
                CryptocurrencyDTO::getSymbol,
                CryptocurrencyDTO::getCoinMarketId
        ).containsExactly(
                cryptocurrencyDTO.getId(),
                cryptocurrencyDTO.getName(),
                cryptocurrencyDTO.getSymbol(),
                cryptocurrencyDTO.getCoinMarketId()
        );

        assertThat(dtoUpdated.getPriceDTO()).extracting(
                PriceDTO::getId,
                PriceDTO::getPriceCurrent,
                PriceDTO::getPercentChange1h
        ).containsExactly(
                cryptocurrencyDTO.getPriceDTO().getId(),
                cryptocurrencyQuoteDTO.getPriceResponseDTO().getPriceCurrent(),
                cryptocurrencyQuoteDTO.getPriceResponseDTO().getPercentChange1h()
        );
    }

    @Test
    void whenMapPostDTO_thenReturnEntity() {
        var expected = mapper.mapPostDtoToCryptocurrencyEntity(cryptocurrencyPostDTO);

        assertThat(expected)
                .isInstanceOf(Cryptocurrency.class)
                .extracting(
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId
                ).containsExactly(
                        cryptocurrencyPostDTO.getName(),
                        cryptocurrencyPostDTO.getSymbol(),
                        cryptocurrencyPostDTO.getCoinMarketId()
                );
    }

    @Test
    void whenMapEntity_thenReturnPostDTO() {
        var expected = mapper.mapEntityToCryptocurrencyPostDto(cryptocurrency);

        assertThat(expected)
                .isInstanceOf(CryptocurrencyPostDTO.class)
                .extracting(
                        CryptocurrencyPostDTO::getName,
                        CryptocurrencyPostDTO::getSymbol,
                        CryptocurrencyPostDTO::getCoinMarketId
                ).containsExactly(
                        cryptocurrency.getName(),
                        cryptocurrency.getSymbol(),
                        cryptocurrency.getCoinMarketId()
                );
    }
}