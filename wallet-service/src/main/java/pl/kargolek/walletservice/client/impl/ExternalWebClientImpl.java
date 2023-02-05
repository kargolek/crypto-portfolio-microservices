package pl.kargolek.walletservice.client.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import pl.kargolek.walletservice.client.ExternalWebClient;
import pl.kargolek.walletservice.client.validation.ValidateBody;
import pl.kargolek.walletservice.exception.WebClientStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetryBackoffSpec;

/**
 * @author Karol Kuta-Orlowicz
 */
@Component
public class ExternalWebClientImpl implements ExternalWebClient {

    @Override
    public <T> Mono<T> getRequest(WebClient webClient,
                                  UriComponents uriComponents,
                                  Class<T> responseWrapper,
                                  ValidateBody validateBody,
                                  RetryBackoffSpec retrySpec) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.query(uriComponents.getQuery()).build())
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError,
                        clientResponse -> Mono.error(new WebClientStatusException(clientResponse.statusCode())))
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(new WebClientStatusException(clientResponse.statusCode())))
                .bodyToMono(responseWrapper)
                .flatMap(validateBody::validateBodyField)
                .retryWhen(retrySpec);
    }
}
