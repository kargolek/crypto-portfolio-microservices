package pl.kargolek.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.groups.Tuple;
import pl.kargolek.data.TestData;
import pl.kargolek.data.dto.CryptocurrencyDTO;
import pl.kargolek.data.dto.CryptocurrencyTableDTO;
import pl.kargolek.data.dto.PlatformDTO;
import pl.kargolek.data.dto.PriceDTO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.groups.Tuple.tuple;

/**
 * @author Karol Kuta-Orlowicz
 */
public class DefaultAssertion {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private static final TestData data = new TestData();

    public static void assertJsonDataCryptoPriceService(String jsonData,
                                                        String jsonDataName,
                                                        CryptocurrencyTableDTO tokenTableData) {
        var jsonCryptocurrencyDTO = mapJsonData(jsonData);
        switch (jsonDataName.toLowerCase()) {
            case "ethereum-default" -> {
                assertEthereumDefault(jsonCryptocurrencyDTO);
                assertPriceDataDefault(jsonCryptocurrencyDTO);
            }
            case "token-table" -> {
                if (tokenTableData.getId() == -1 || tokenTableData.getPriceId() == -1) {
                    assertTokenTableNoIds(jsonCryptocurrencyDTO, tokenTableData);
                } else
                    assertTokenTable(jsonCryptocurrencyDTO, tokenTableData);
            }
            default -> throw new RuntimeException(
                    String.format("Unable to find provided %s json data name to make assertion", jsonData));
        }
    }

    public static void assertArrayJsonDataCryptoPriceService(String response,
                                                             String jsonDataName,
                                                             List<CryptocurrencyTableDTO> tableData) {
        var cryptocurrencyDTOS = mapArrayJsonData(response);
        switch (jsonDataName.toLowerCase()) {
            case "tokens-table" -> assertArrayTokenTable(cryptocurrencyDTOS, tableData);
            default -> throw new RuntimeException(
                    String.format("Unable to find provided %s json data name to make assertion", response));
        }
    }

    private static CryptocurrencyDTO mapJsonData(String json) {
        try {
            return mapper.readValue(json, CryptocurrencyDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Unable to map json data from response:%s", json));
        }
    }

    private static CryptocurrencyDTO[] mapArrayJsonData(String json) {
        try {
            return mapper.readValue(json, CryptocurrencyDTO[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Unable to map json data from response:%s", json));
        }
    }

    private static void assertEthereumDefault(CryptocurrencyDTO cryptocurrencyDTO) {
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(cryptocurrencyDTO.getId())
                .isPositive();

        assertions.assertThat(cryptocurrencyDTO.getName())
                .isEqualTo(data.getEthereum().getName());

        assertions.assertThat(cryptocurrencyDTO.getSymbol())
                .isEqualTo(data.getEthereum().getSymbol());

        assertions.assertThat(cryptocurrencyDTO.getCoinMarketId())
                .isEqualTo(data.getEthereum().getCoinMarketId());

        assertions.assertThat(cryptocurrencyDTO.getPlatform().getPlatform())
                .isEqualTo(data.getEthereum().getPlatform());

        assertions.assertThat(cryptocurrencyDTO.getPlatform().getTokenAddress())
                .isEqualTo(data.getEthereum().getTokenAddress());

        assertions.assertThat(cryptocurrencyDTO.getLastUpdate())
                .isBeforeOrEqualTo(data.getEthereum().getLastUpdate());

        assertions.assertAll();
    }

    private static void assertPriceDataDefault(CryptocurrencyDTO cryptocurrencyDTO) {
        var priceDTO = cryptocurrencyDTO.getPrice();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(priceDTO.getId())
                .isPositive();

        assertions.assertThat(priceDTO.getPriceCurrent())
                .isNull();

        assertions.assertThat(priceDTO.getPercentChange1h())
                .isNull();

        assertions.assertThat(priceDTO.getPercentChange24h())
                .isNull();

        assertions.assertThat(priceDTO.getPercentChange7d())
                .isNull();

        assertions.assertThat(priceDTO.getPercentChange30d())
                .isNull();

        assertions.assertThat(priceDTO.getPercentChange60d())
                .isNull();

        assertions.assertThat(priceDTO.getPercentChange90d())
                .isNull();

        assertions.assertThat(priceDTO.getLastUpdate())
                .isBefore(LocalDateTime.now(ZoneOffset.UTC));

        assertions.assertAll();
    }

    private static void assertArrayTokenTable(CryptocurrencyDTO[] cryptocurrencyDTOS,
                                              List<CryptocurrencyTableDTO> tableData) {

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(cryptocurrencyDTOS)
                .extracting(
                        CryptocurrencyDTO::getId,
                        CryptocurrencyDTO::getName,
                        CryptocurrencyDTO::getSymbol,
                        CryptocurrencyDTO::getCoinMarketId
                ).containsExactlyElementsOf(
                        mapCryptocurrencyInfoTableData(tableData)
                );

        assertions.assertThat(cryptocurrencyDTOS)
                .extracting(
                        CryptocurrencyDTO::getPlatform
                ).extracting(
                        PlatformDTO::getPlatform,
                        PlatformDTO::getTokenAddress
                ).containsExactlyElementsOf(
                        mapCryptocurrencyPlatformTableData(tableData)
                );

        assertions.assertThat(cryptocurrencyDTOS)
                .extracting(
                        CryptocurrencyDTO::getPrice
                ).extracting(
                        PriceDTO::getId,
                        PriceDTO::getPriceCurrent,
                        PriceDTO::getPercentChange1h,
                        PriceDTO::getPercentChange24h,
                        PriceDTO::getPercentChange7d,
                        PriceDTO::getPercentChange30d,
                        PriceDTO::getPercentChange60d,
                        PriceDTO::getPercentChange90d
                ).containsExactlyElementsOf(
                        mapCryptocurrencyPriceTableData(tableData)
                );

        assertions.assertAll();
    }

    private static List<Tuple> mapCryptocurrencyInfoTableData(List<CryptocurrencyTableDTO> tableDTOS) {
        return tableDTOS.stream()
                .map(item -> tuple(
                        item.getId(),
                        item.getName(),
                        item.getSymbol(),
                        item.getCoinMarketId()
                )).collect(Collectors.toList());
    }

    private static List<Tuple> mapCryptocurrencyPlatformTableData(List<CryptocurrencyTableDTO> tableDTOS) {
        return tableDTOS.stream()
                .map(item -> tuple(
                        item.getPlatform(),
                        item.getTokenAddress()
                )).collect(Collectors.toList());
    }

    private static List<Tuple> mapCryptocurrencyPriceTableData(List<CryptocurrencyTableDTO> tableDTOS) {
        return tableDTOS.stream()
                .map(item -> tuple(
                        item.getPriceId(),
                        item.getPriceCurrent(),
                        item.getPercentChange1h(),
                        item.getPercentChange24h(),
                        item.getPercentChange7d(),
                        item.getPercentChange30d(),
                        item.getPercentChange60d(),
                        item.getPercentChange90d()
                )).collect(Collectors.toList());
    }

    private static void assertTokenTable(CryptocurrencyDTO cryptocurrencyDTO,
                                         CryptocurrencyTableDTO cryptocurrencyTableDTO) {
        SoftAssertions assertions = new SoftAssertions();

        assertions.assertThat(cryptocurrencyDTO)
                .extracting(
                        CryptocurrencyDTO::getId,
                        CryptocurrencyDTO::getName,
                        CryptocurrencyDTO::getSymbol,
                        CryptocurrencyDTO::getCoinMarketId
                ).containsExactly(
                        cryptocurrencyTableDTO.getId(),
                        cryptocurrencyTableDTO.getName(),
                        cryptocurrencyTableDTO.getSymbol(),
                        cryptocurrencyTableDTO.getCoinMarketId()
                );

        assertions.assertThat(cryptocurrencyDTO)
                .extracting(
                        CryptocurrencyDTO::getPlatform
                ).extracting(
                        PlatformDTO::getPlatform,
                        PlatformDTO::getTokenAddress
                ).containsExactly(
                        cryptocurrencyTableDTO.getPlatform(),
                        cryptocurrencyTableDTO.getTokenAddress()
                );

        assertions.assertThat(cryptocurrencyDTO)
                .extracting(
                        CryptocurrencyDTO::getPrice
                ).extracting(
                        PriceDTO::getId,
                        PriceDTO::getPriceCurrent,
                        PriceDTO::getPercentChange1h,
                        PriceDTO::getPercentChange24h,
                        PriceDTO::getPercentChange7d,
                        PriceDTO::getPercentChange30d,
                        PriceDTO::getPercentChange60d,
                        PriceDTO::getPercentChange90d
                ).containsExactly(
                        cryptocurrencyTableDTO.getPriceId(),
                        cryptocurrencyTableDTO.getPriceCurrent(),
                        cryptocurrencyTableDTO.getPercentChange1h(),
                        cryptocurrencyTableDTO.getPercentChange24h(),
                        cryptocurrencyTableDTO.getPercentChange7d(),
                        cryptocurrencyTableDTO.getPercentChange30d(),
                        cryptocurrencyTableDTO.getPercentChange60d(),
                        cryptocurrencyTableDTO.getPercentChange90d()
                );

        assertions.assertAll();

    }

    private static void assertTokenTableNoIds(CryptocurrencyDTO cryptocurrencyDTO,
                                              CryptocurrencyTableDTO cryptocurrencyTableDTO) {
        SoftAssertions assertions = new SoftAssertions();

        assertions.assertThat(cryptocurrencyDTO.getId())
                .isPositive();

        assertions.assertThat(cryptocurrencyDTO)
                .extracting(
                        CryptocurrencyDTO::getName,
                        CryptocurrencyDTO::getSymbol,
                        CryptocurrencyDTO::getCoinMarketId
                ).containsExactly(
                        cryptocurrencyTableDTO.getName(),
                        cryptocurrencyTableDTO.getSymbol(),
                        cryptocurrencyTableDTO.getCoinMarketId()
                );

        assertions.assertThat(cryptocurrencyDTO)
                .extracting(
                        CryptocurrencyDTO::getPlatform
                ).extracting(
                        PlatformDTO::getPlatform,
                        PlatformDTO::getTokenAddress
                ).containsExactly(
                        cryptocurrencyTableDTO.getPlatform(),
                        cryptocurrencyTableDTO.getTokenAddress()
                );

        assertions.assertThat(cryptocurrencyDTO.getPrice().getId())
                .isPositive();

        assertions.assertThat(cryptocurrencyDTO)
                .extracting(
                        CryptocurrencyDTO::getPrice
                ).extracting(
                        PriceDTO::getPriceCurrent,
                        PriceDTO::getPercentChange1h,
                        PriceDTO::getPercentChange24h,
                        PriceDTO::getPercentChange7d,
                        PriceDTO::getPercentChange30d,
                        PriceDTO::getPercentChange60d,
                        PriceDTO::getPercentChange90d
                ).containsExactly(
                        cryptocurrencyTableDTO.getPriceCurrent(),
                        cryptocurrencyTableDTO.getPercentChange1h(),
                        cryptocurrencyTableDTO.getPercentChange24h(),
                        cryptocurrencyTableDTO.getPercentChange7d(),
                        cryptocurrencyTableDTO.getPercentChange30d(),
                        cryptocurrencyTableDTO.getPercentChange60d(),
                        cryptocurrencyTableDTO.getPercentChange90d()
                );

        assertions.assertAll();
    }

}
