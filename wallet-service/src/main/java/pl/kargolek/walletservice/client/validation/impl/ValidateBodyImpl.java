package pl.kargolek.walletservice.client.validation.impl;

import lombok.extern.slf4j.Slf4j;
import pl.kargolek.walletservice.client.validation.ValidateBody;
import pl.kargolek.walletservice.exception.ResponseFieldException;
import reactor.core.publisher.Mono;

/**
 * @author Karol Kuta-Orlowicz
 */
@Slf4j
public class ValidateBodyImpl implements ValidateBody {

    private final String bodyFieldName;
    private final Object fieldValueCompare;

    public ValidateBodyImpl(String bodyFieldName, Object fieldValueCompare) {
        this.bodyFieldName = bodyFieldName;
        this.fieldValueCompare = fieldValueCompare;
    }

    @Override
    public <T> Mono<T> validateBodyField(T bodyWrapped) {
        try {
            var field = bodyWrapped.getClass().getDeclaredField(bodyFieldName);
            field.setAccessible(true);
            var wrappedField = field.get(bodyWrapped);
            if (!wrappedField.equals(fieldValueCompare))
                return Mono.error(new ResponseFieldException(bodyFieldName, wrappedField.toString()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return Mono.just(bodyWrapped);
    }
}
