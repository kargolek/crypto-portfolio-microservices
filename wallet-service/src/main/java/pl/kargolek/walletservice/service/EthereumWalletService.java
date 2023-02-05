package pl.kargolek.walletservice.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.kargolek.walletservice.client.ExternalWebClient;
import pl.kargolek.walletservice.client.validation.ValidateBody;
import pl.kargolek.walletservice.dto.MultiWalletBalance;
import pl.kargolek.walletservice.validation.MultiWalletAddressValidator;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.RetryBackoffSpec;

/**
 * @author Karol Kuta-Orlowicz
 */
@Component
@NoArgsConstructor
@Slf4j
public class EthereumWalletService {

    @Autowired
    private ExternalWebClient externalWebClient;

    @Autowired
    @Qualifier("etherscanWebClient")
    private WebClient webClient;

    @Autowired
    @Qualifier("etherscanRetrySpec")
    private RetryBackoffSpec etherscanRetrySpec;

    @Value("${api.etherscan.apiKey}")
    private String apiKey;

    @Autowired
    @Qualifier("etherscanMultiWalletAddressValidator")
    private MultiWalletAddressValidator multiWalletAddressValidator;

    @Autowired
    @Qualifier("etherscanValidateBody")
    private ValidateBody validateBody;

    public MultiWalletBalance getMultiBalance(String wallets) {
        log.info("Calling multi wallet balance request");
        multiWalletAddressValidator.isValidAddresses(wallets);
        return getMultiWalletBalance(wallets);
    }

    private MultiWalletBalance getMultiWalletBalance(String wallets) {
        var scheduler = Schedulers.boundedElastic();
        return this.getMonoMultiWalletBalance(wallets)
                .subscribeOn(scheduler)
                .block();
    }

    private Mono<MultiWalletBalance> getMonoMultiWalletBalance(String wallets) {
        var uriComponents = this.setUriComponentsMultiWallets(wallets);
        return externalWebClient.getRequest(
                this.webClient,
                uriComponents,
                MultiWalletBalance.class,
                this.validateBody,
                this.etherscanRetrySpec);
    }

    private UriComponents setUriComponentsMultiWallets(String wallets) {
        return UriComponentsBuilder.newInstance()
                .queryParam("module", "account")
                .queryParam("action", "balancemulti")
                .queryParam("address", wallets)
                .queryParam("apiKey", apiKey)
                .build();
    }
}
