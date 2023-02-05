package pl.kargolek.walletservice.client;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import pl.kargolek.walletservice.client.validation.ValidateBody;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetryBackoffSpec;

/**
 * @author Karol Kuta-Orlowicz
 */
public interface ExternalWebClient {

    <T> Mono<T> getRequest(WebClient webClient,
                           UriComponents uriComponents,
                           Class<T> responseWrapper,
                           ValidateBody validateBody,
                           RetryBackoffSpec retrySpec);

}
