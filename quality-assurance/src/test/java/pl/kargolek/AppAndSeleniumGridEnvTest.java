package pl.kargolek;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import pl.kargolek.utils.PropertiesLoader;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppAndSeleniumGridEnvTest {

    private String hubURL;
    private String appBaseURL;
    private WebDriver driver;

    private final PropertiesLoader confProperties = new PropertiesLoader(
            Thread.currentThread().getContextClassLoader().getResource("").getPath(),
            "conf.properties"
    );

    @BeforeEach
    public void setUp() throws MalformedURLException {
        this.hubURL = confProperties.getPropertyValue("selenium.hub.url");
        this.appBaseURL = confProperties.getPropertyValue("app.base.url");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setCapability("browserVersion", "110.0");
        chromeOptions.setCapability("platformName", "LINUX");

        driver = new RemoteWebDriver(new URL(this.hubURL), chromeOptions, false);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

        driver.get(this.appBaseURL);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    void whenOpenCryptoPortfolioApp_thenTitleCryptoPortfolio() throws InterruptedException {
        var title = driver.findElement(By.cssSelector(".title"));
        var titleText = title.getAttribute("innerText");

        assertTrue(title.isDisplayed());
        assertTrue(titleText.equalsIgnoreCase("crypto portfolio"));

        var logs = driver.manage().logs().get(LogType.BROWSER);
        logs.getAll()
                .forEach(System.out::println);

        Thread.sleep(30000);
    }
}
