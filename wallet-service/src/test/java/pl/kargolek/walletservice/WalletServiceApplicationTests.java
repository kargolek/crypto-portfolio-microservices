package pl.kargolek.walletservice;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import pl.kargolek.walletservice.client.CryptoPriceServiceClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("SmokeTest")
class WalletServiceApplicationTests {

	@Autowired
	private CryptoPriceServiceClient cryptoPriceServiceClient;
	@Autowired
	@Qualifier("etherscanWebClient")
	private WebClient etherscanWebClient;

	@Test
	void contextLoads() {
		assertThat(etherscanWebClient).isNotNull();
		assertThat(cryptoPriceServiceClient).isNotNull();
	}
}
