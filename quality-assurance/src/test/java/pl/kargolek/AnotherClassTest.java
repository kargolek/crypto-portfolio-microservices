package pl.kargolek;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import pl.kargolek.extension.VideoRecordExt;
import pl.kargolek.utils.PropertiesLoader;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Feature("An example another new feature")
@ExtendWith(VideoRecordExt.class)
public class AnotherClassTest {

    private String hubURL;
    private String appBaseURL;
    private WebDriver driver;

    private final PropertiesLoader confProperties = new PropertiesLoader(
            Thread.currentThread().getContextClassLoader().getResource("").getPath(),
            "conf.properties"
    );

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
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
    public void tearDown() throws IOException, InterruptedException {
        driver.quit();
    }

    @Test
    @Story("An example story 3")
    void whenOpenCryptoPortfolioApp_thenTitleCryptoPortfolio2() throws InterruptedException {
        var title = driver.findElement(By.cssSelector(".title"));
        var titleText = title.getAttribute("innerText");

        assertTrue(title.isDisplayed());
        assertTrue(titleText.equalsIgnoreCase("crypto portfolio"));

        var logs = driver.manage().logs().get(LogType.BROWSER);
        logs.getAll()
                .forEach(System.out::println);
    }

    @Test
    @Story("An example story 4")
    void whenTypeWalletAddressAndPressEnter_thenBalanceViewShouldBeOpen2() throws InterruptedException {
        var input = driver.findElement(By.cssSelector(".wallet-input-container input"));
        input.sendKeys("0xcA8Fa8f0b631EcdB18Cda619C4Fc9d197c8aFfCa");
        input.sendKeys(Keys.ENTER);

        var container = driver.findElement(By.cssSelector(".inline-container"));

        assertTrue(container.isDisplayed());

        var logs = driver.manage().logs().get(LogType.BROWSER);
        logs.getAll()
                .forEach(System.out::println);
    }
}
