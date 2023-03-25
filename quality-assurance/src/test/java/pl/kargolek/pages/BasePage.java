package pl.kargolek.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;

/**
 * @author Karol Kuta-Orlowicz
 */
public class BasePage {

    public final WebDriver driver;
    public final Actions action;
    public final FluentWait<WebDriver> fluentWait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.action = new Actions(driver);
        this.fluentWait = new FluentWait<>(driver);

        PageFactory.initElements(driver, this);
    }

    public WebElement waitForElementVisibility(WebElement element, Duration timeout) {
        return new WebDriverWait(this.driver, timeout).until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForElementVisibility(WebElement element, Duration timeout, Duration interval) {
        return this.fluentWait.withTimeout(timeout)
                .pollingEvery(interval)
                .until(ExpectedConditions.visibilityOf(element));
    }

    @SafeVarargs
    public final WebElement waitForElementVisibility(WebElement element, Duration timeout, Duration interval, Class<? extends Throwable>... clazz) {
        return this.fluentWait.withTimeout(timeout)
                .pollingEvery(interval)
                .ignoreAll(Arrays.asList(clazz))
                .until(ExpectedConditions.visibilityOf(element));
    }

}
