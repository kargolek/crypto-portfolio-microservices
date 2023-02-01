package pl.kargolek.walletservice;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kargolek.walletservice.client.CryptocurrencyServiceClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("SmokeTest")
class WalletServiceApplicationTests {

	@Autowired
	private CryptocurrencyServiceClient cryptocurrencyServiceClient;

	@Test
	void contextLoads() {
		assertThat(cryptocurrencyServiceClient).isNotNull();
	}
}
