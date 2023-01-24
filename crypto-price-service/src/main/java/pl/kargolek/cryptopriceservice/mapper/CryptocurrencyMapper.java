package pl.kargolek.cryptopriceservice.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pl.kargolek.cryptopriceservice.dto.client.CryptocurrencyQuoteDTO;
import pl.kargolek.cryptopriceservice.dto.client.PriceQuoteDTO;
import pl.kargolek.cryptopriceservice.dto.controller.CryptocurrencyPostDTO;
import pl.kargolek.cryptopriceservice.dto.model.CryptocurrencyDTO;
import pl.kargolek.cryptopriceservice.dto.model.PriceDTO;
import pl.kargolek.cryptopriceservice.mapper.util.CycleAvoidingMappingContext;
import pl.kargolek.cryptopriceservice.mapper.util.MappingUtil;
import pl.kargolek.cryptopriceservice.model.Cryptocurrency;
import pl.kargolek.cryptopriceservice.model.Price;

/**
 * @author Karol Kuta-Orlowicz
 */

@Mapper(uses = MappingUtil.class)
public interface CryptocurrencyMapper {

    CryptocurrencyMapper INSTANCE = Mappers.getMapper(CryptocurrencyMapper.class);

    @Mapping(target = "cryptocurrency", source = "cryptocurrencyDTO")
    Price mapDtoToPriceEntity(PriceDTO priceDTO);

    @Mapping(target = "cryptocurrencyDTO", source = "cryptocurrency")
    PriceDTO mapEntityToPriceDto(Price price);

    @Mapping(target = "id", ignore = true)
    PriceDTO mapQuoteDtoToPriceDto(PriceQuoteDTO priceQuoteDTO);

    @Mapping(target = "priceDTO", source = "price")
    CryptocurrencyDTO mapEntityToCryptocurrencyDto(Cryptocurrency cryptocurrency,
                                                   @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "price", source = "priceDTO")
    Cryptocurrency mapDtoToCryptocurrencyEntity(CryptocurrencyDTO cryptocurrencyDTO);

    @Mappings({
            @Mapping(target = "priceDTO", source = "quote", qualifiedBy = MappingUtil.PriceMap.class)
    })
    CryptocurrencyDTO mapQuoteDtoToCryptocurrencyDto(CryptocurrencyQuoteDTO cryptocurrencyQuoteDTO);

    Cryptocurrency mapPostDtoToCryptocurrencyEntity(CryptocurrencyPostDTO cryptocurrencyPostDTO);

    CryptocurrencyPostDTO mapEntityToCryptocurrencyPostDto(Cryptocurrency cryptocurrency);

    @Mapping(target = "id", ignore = true)
    PriceDTO updateDtoByPriceQuoteDto(@MappingTarget PriceDTO priceDTO, PriceQuoteDTO priceQuoteDTO);

    @Mappings({
            @Mapping(target = "priceDTO", source = "quote", qualifiedBy = MappingUtil.PriceMap.class),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", ignore = true),
            @Mapping(target = "symbol", ignore = true),
            @Mapping(target = "coinMarketId", ignore = true)
    })
    CryptocurrencyDTO updateDtoByCryptocurrencyQuoteDto(@MappingTarget CryptocurrencyDTO cryptocurrencyDTO,
                                                        CryptocurrencyQuoteDTO cryptocurrencyQuoteDTO);
}