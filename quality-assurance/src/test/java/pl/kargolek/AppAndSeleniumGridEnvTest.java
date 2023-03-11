package pl.kargolek;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppAndSeleniumGridEnvTest {

    private static final String GRID_URL = "http://localhost:4444";
    private WebDriver driver;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setCapability("browserVersion", "110.0");
        chromeOptions.setCapability("platformName", "LINUX");
        driver = new RemoteWebDriver(new URL(GRID_URL), chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    void whenOpenCryptoPortfolioApp_thenTitleCryptoPortfolio() {
        driver.get("http://0.0.0.0:4200");
        var title = driver.findElement(By.cssSelector(".title"));
        var titleText = title.getAttribute("innerText");
        assertTrue(title.isDisplayed());
        assertTrue(titleText.equalsIgnoreCase("crypto portfolio"));
    }

}
