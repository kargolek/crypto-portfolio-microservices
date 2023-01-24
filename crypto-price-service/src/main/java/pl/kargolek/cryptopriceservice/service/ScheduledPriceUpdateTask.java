package pl.kargolek.cryptopriceservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledPriceUpdateTask {

    @Autowired
    private PriceUpdateService priceUpdateService;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void scheduledUpdatePriceJob() {
        log.info("Running scheduled update price job");
        priceUpdateService.updateCryptocurrencyPrices();
    }

}
