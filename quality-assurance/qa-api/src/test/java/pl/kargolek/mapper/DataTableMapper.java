package pl.kargolek.mapper;

import pl.kargolek.data.dto.CryptocurrencyTableDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * @author Karol Kuta-Orlowicz
 */
public class DataTableMapper {

    public static List<CryptocurrencyTableDTO> map(List<Map<String, String>> entries){
        return entries.stream()
                .map(DataTableMapper::map)
                .toList();
    }

    private static CryptocurrencyTableDTO map(Map<String, String> entry){
        var cryptocurrencyId = (entry.get("id") == null) ? -1L : Long.parseLong(entry.get("id"));

        var priceId = (entry.get("priceId") == null) ? -1L : Long.parseLong(entry.get("priceId"));

        var priceCurrent = (entry.get("priceCurrent") == null) ? null :
                new BigDecimal(entry.get("priceCurrent")).setScale(12, RoundingMode.HALF_EVEN);

        var percentChange1h = (entry.get("percentChange1h") == null) ? null :
                new BigDecimal(entry.get("percentChange1h")).setScale(12, RoundingMode.HALF_EVEN);

        var percentChange24h = (entry.get("percentChange24h") == null) ? null :
                new BigDecimal(entry.get("percentChange24h")).setScale(12, RoundingMode.HALF_EVEN);

        var percentChange7d = (entry.get("percentChange7d") == null) ? null :
                new BigDecimal(entry.get("percentChange7d")).setScale(12, RoundingMode.HALF_EVEN);

        var percentChange30d = (entry.get("percentChange30d") == null) ? null :
                new BigDecimal(entry.get("percentChange30d")).setScale(12, RoundingMode.HALF_EVEN);

        var percentChange60d = (entry.get("percentChange60d") == null) ? null :
                new BigDecimal(entry.get("percentChange60d")).setScale(12, RoundingMode.HALF_EVEN);

        var percentChange90d = (entry.get("percentChange90d") == null) ? null :
                new BigDecimal(entry.get("percentChange90d")).setScale(12, RoundingMode.HALF_EVEN);

        return CryptocurrencyTableDTO.builder()
                .id(cryptocurrencyId)
                .name(entry.get("name"))
                .symbol(entry.get("symbol"))
                .coinMarketId(Long.parseLong(entry.get("coinMarketId")))
                .platform(entry.get("platform"))
                .tokenAddress(entry.get("tokenAddress"))
                .priceId(priceId)
                .priceCurrent(priceCurrent)
                .percentChange1h(percentChange1h)
                .percentChange24h(percentChange24h)
                .percentChange7d(percentChange7d)
                .percentChange30d(percentChange30d)
                .percentChange60d(percentChange60d)
                .percentChange90d(percentChange90d)
                .build();
    }


}
