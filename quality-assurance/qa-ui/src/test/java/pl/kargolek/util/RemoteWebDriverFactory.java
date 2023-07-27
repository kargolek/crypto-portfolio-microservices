package pl.kargolek.util;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Karol Kuta-Orlowicz
 */
@Slf4j
public class RemoteWebDriverFactory {

    public WebDriver getWebDriver(String hubURL, BrowserType browserType) throws MalformedURLException {
        switch (browserType) {
            case FIREFOX -> {
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                return new RemoteWebDriver(new URL(hubURL), firefoxOptions, false);
            }
            case EDGE -> {
                EdgeOptions edgeOptions = new EdgeOptions();
                return new RemoteWebDriver(new URL(hubURL), edgeOptions, false);
            }
            case MOBILE_CHROME -> {
                Map<String, String> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "Nexus 5");

                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
                return new RemoteWebDriver(new URL(hubURL), chromeOptions, false);
            }
            default -> {
                log.info("CHROME remote webdriver has been started");
                ChromeOptions chromeOptions = new ChromeOptions();
                return new RemoteWebDriver(new URL(hubURL), chromeOptions);
            }
        }
    }

}
