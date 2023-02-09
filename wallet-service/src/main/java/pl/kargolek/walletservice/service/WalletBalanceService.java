package pl.kargolek.walletservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import pl.kargolek.walletservice.client.ExternalRequestClient;
import pl.kargolek.walletservice.validation.RequestBodyValidator;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetryBackoffSpec;

/**
 * @author Karol Kuta-Orlowicz
 */
@Slf4j
public abstract class WalletBalanceService<T> {

    @Autowired
    private ExternalRequestClient externalRequestClient;
    private final WebClient webClient;
    private final RetryBackoffSpec retrySpec;
    private final RequestBodyValidator requestBodyValidator;

    public WalletBalanceService(WebClient webClient,
                                RetryBackoffSpec retrySpec,
                                RequestBodyValidator requestBodyValidator) {
        this.webClient = webClient;
        this.retrySpec = retrySpec;
        this.requestBodyValidator = requestBodyValidator;
    }

    public abstract T getMultiWalletBalance(String wallets);

    Mono<T> getMultiBalanceMono(String wallets) {
        log.info("Calling request for multi wallet balance");
        return this.externalRequestClient.getRequest(
                this.webClient,
                this.setupUriComponents(wallets),
                mappedClazz(),
                this.requestBodyValidator,
                this.retrySpec
        );
    }

    abstract Class<T> mappedClazz();

    abstract UriComponents setupUriComponents(String wallets);

}
