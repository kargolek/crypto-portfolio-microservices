package pl.kargolek;

import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import pl.kargolek.extension.BaseTestConfig;
import pl.kargolek.util.DevToolsDriver;
import pl.kargolek.util.PropertiesLoader;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Epic_example_data")
@Feature("Feature_class_example_data")
@BaseTestConfig
@Slf4j
public class ExampleClassTest {

    public final WebDriver driver;
    public final DevToolsDriver devToolsDriver;
    private String appBaseURL;

    public ExampleClassTest(WebDriver driver, DevToolsDriver devToolsDriver) {
        this.driver = driver;
        this.devToolsDriver = devToolsDriver;
    }

    @BeforeEach
    public void setUp(PropertiesLoader propertiesLoader) {
        this.appBaseURL = propertiesLoader.getPropertyValue("app.base.url");
    }

    @Test
    @Story("Example story 1")
    @Description("Test description 1")
    @Link(name = "Trello task 1", url = "https://trello.com/c/jJ9wxTTR/53-cpa-53-as-test-developer-i-can-get-allure-test-report-after-execute-qa-tests-and-i-can-view-it-via-gh-pages")
    @Severity(SeverityLevel.CRITICAL)
    void whenOpenCryptoPortfolioApp_thenTitleCryptoPortfolio() {
        this.driver.get(this.appBaseURL);

        var title = driver.findElement(By.cssSelector(".title"));
        var titleText = title.getAttribute("innerText");

        assertTrue(title.isDisplayed());
        assertTrue(titleText.equalsIgnoreCase("crypto portfolio"));
    }

    @Test
    @Story("Example story 2")
    @Description("Example description 2")
    @Link(name = "Trello task 1", url = "https://trello.com/c/jJ9wxTTR/53-cpa-53-as-test-developer-i-can-get-allure-test-report-after-execute-qa-tests-and-i-can-view-it-via-gh-pages")
    @Severity(SeverityLevel.CRITICAL)
    void whenTypeWalletAddressAndPressEnter_thenBalanceViewShouldBeOpen() {
        this.driver.get(this.appBaseURL);

        var input = driver.findElement(By.cssSelector(".wallet-input-container input"));
        input.sendKeys("0xcA8Fa8f0b631EcdB18Cda619C4Fc9d197c8aFfCa");
        input.sendKeys(Keys.ENTER);

        var container = driver.findElement(By.cssSelector(".amount-container"));

        assertTrue(container.isDisplayed());
    }

}
