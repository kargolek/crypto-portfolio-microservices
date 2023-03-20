package pl.kargolek.walletservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import pl.kargolek.walletservice.validation.impl.RequestBodyValidatorImpl;
import pl.kargolek.walletservice.exception.ExternalServiceCallException;
import reactor.util.retry.RetryBackoffSpec;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

/**
 * @author Karol Kuta-Orlowicz
 */
@Configuration
public class WebClientConfig {

    @Value("${api.etherscan.baseUrl}")
    private String etherscanURL;

    @Value("${api.polygonscan.baseUrl}")
    private String polygonURL;

    @Value("${api.etherscan.maxRetryAttempts}")
    private String maxRetryAttempts;

    @Value("${api.etherscan.fixedDelayMillis}")
    private String fixedDelayMillis;

    @Value("${api.etherscan.validateBodyFieldName}")
    private String validateFieldName;

    @Value("${api.etherscan.validationValue}")
    private String validationValue;

    @Bean("etherscanWebClient")
    public WebClient etherscanWebClient() {
        return WebClient.builder()
                .baseUrl(etherscanURL)
                .build();
    }

    @Bean("polygonscanWebClient")
    public WebClient polygonWebClient() {
        return WebClient.builder()
                .baseUrl(polygonURL)
                .build();
    }

    @Bean("etherscanRetrySpec")
    public RetryBackoffSpec etherscanRetrySpec(){
        return RetrySpec.fixedDelay(Long.parseLong(maxRetryAttempts), Duration.ofMillis(Long.parseLong(fixedDelayMillis)))
                .filter(e -> e instanceof RuntimeException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                        new ExternalServiceCallException("Call external service reached out max retries value")));
    }

    @Bean("etherscanValidateBody")
    public RequestBodyValidatorImpl etherscanValidateBody(){
        return new RequestBodyValidatorImpl(validateFieldName, validationValue);
    }
}
