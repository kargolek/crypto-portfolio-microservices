package pl.kargolek.walletservice.client.validation;

import reactor.core.publisher.Mono;

/**
 * @author Karol Kuta-Orlowicz
 */
public interface ValidateBody {

    <T> Mono<T> validateBodyField(T bodyWrapped);
}
