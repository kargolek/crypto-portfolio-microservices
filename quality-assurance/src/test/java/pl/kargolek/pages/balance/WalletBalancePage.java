package pl.kargolek.pages.balance;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pl.kargolek.pages.BasePage;

import java.time.Duration;

/**
 * @author Karol Kuta-Orlowicz
 */
public class WalletBalancePage extends BasePage {

    @FindBy(xpath = ".//app-ethereum-balance//div[@class='amount-container']")
    private WebElement ethAmountContainer;
    public WalletBalancePage(WebDriver driver) {
        super(driver);
    }

    @Step("Get total amounts container of ethereum token")
    public WebElement getEthBalanceAmountContainer(){
        return this.waitForElementVisibility(ethAmountContainer, Duration.ofSeconds(20));
    }

}
