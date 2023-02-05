package pl.kargolek.walletservice.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import pl.kargolek.walletservice.dto.MultiWalletBalance;
import pl.kargolek.walletservice.dto.WalletBalance;

import java.io.IOException;

/**
 * @author Karol Kuta-Orlowicz
 */
@Slf4j
public class DeserializerWalletBalance extends JsonDeserializer<MultiWalletBalance> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public MultiWalletBalance deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        var status = node.get("status").asText();
        var message = node.get("message").asText();

        var resultNode = node.get("result");

        if (resultNode == null) {
            return new MultiWalletBalance()
                    .setStatus(status)
                    .setMessage(message)
                    .setResult(null);
        }

        if (resultNode.isArray()) {
            var resultAsWalletBalance = mapper.readValue(resultNode.toString(), WalletBalance[].class);
            return new MultiWalletBalance()
                    .setStatus(status)
                    .setMessage(message)
                    .setResult(resultAsWalletBalance);
        }

        return new MultiWalletBalance()
                .setStatus(status)
                .setMessage(message)
                .setResult(null);
    }
}
