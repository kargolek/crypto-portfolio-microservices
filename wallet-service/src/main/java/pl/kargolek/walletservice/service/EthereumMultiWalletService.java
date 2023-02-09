package pl.kargolek.walletservice.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.kargolek.walletservice.validation.RequestBodyValidator;
import pl.kargolek.walletservice.dto.WalletMultiBalance;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.RetryBackoffSpec;

/**
 * @author Karol Kuta-Orlowicz
 */
@Component
public class EthereumMultiWalletService extends WalletBalanceService<WalletMultiBalance> {

    @Value("${api.etherscan.apiKey}")
    private String apiKey;

    public EthereumMultiWalletService(@Qualifier("etherscanWebClient") WebClient webClient,
                                      @Qualifier("etherscanRetrySpec") RetryBackoffSpec etherscanRetrySpec,
                                      @Qualifier("etherscanValidateBody") RequestBodyValidator requestBodyValidator) {
        super(webClient, etherscanRetrySpec, requestBodyValidator);
    }

    @Override
    public WalletMultiBalance getMultiWalletBalance(String wallets) {
        var scheduler = Schedulers.boundedElastic();
        return this.getMultiBalanceMono(wallets)
                .subscribeOn(scheduler)
                .block();
    }

    @Override
    Class<WalletMultiBalance> mappedClazz() {
        return WalletMultiBalance.class;
    }

    @Override
    protected UriComponents setupUriComponents(String wallets) {
        return UriComponentsBuilder.newInstance()
                .queryParam("module", "account")
                .queryParam("action", "balancemulti")
                .queryParam("address", wallets)
                .queryParam("apiKey", this.apiKey)
                .build();
    }

}
