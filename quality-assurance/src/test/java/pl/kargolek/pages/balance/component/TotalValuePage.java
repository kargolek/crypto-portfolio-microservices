package pl.kargolek.pages.balance.component;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pl.kargolek.pages.BasePage;

import java.time.Duration;

/**
 * @author Karol Kuta-Orlowicz
 */
public class TotalValuePage extends BasePage {

    @FindBy(css = ".total-value-container .total-value-data")
    private WebElement totalValue;

    public TotalValuePage(WebDriver driver) {
        super(driver);
    }

    @Step("Get total value text")
    public String getTotalValueText() {
        return this.waitForElementVisibility(totalValue, Duration.ofSeconds(10)).getText();
    }

    @Step("Wait for text change from 0.00 USD")
    public TotalValuePage waitForTotalValueTextChange() {
        this.waitForElementVisibility(totalValue, Duration.ofSeconds(10));
        this.waitForElementTextToChange(totalValue, "0.00 USD\nTotal value", Duration.ofSeconds(6));
        return this;
    }
}
