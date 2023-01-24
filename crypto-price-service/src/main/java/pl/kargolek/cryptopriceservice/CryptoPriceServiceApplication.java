package pl.kargolek.cryptopriceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class CryptoPriceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoPriceServiceApplication.class, args);
    }

}
