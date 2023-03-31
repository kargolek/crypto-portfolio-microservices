package pl.kargolek.tests;

import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import pl.kargolek.data.TestData;
import pl.kargolek.data.script.TestDataSql;
import pl.kargolek.extension.BaseTestConfig;
import pl.kargolek.extension.assertion.SoftAssertion;
import pl.kargolek.extension.data.TestDataProvider;
import pl.kargolek.extension.javascript.JavascriptDriverExecutor;
import pl.kargolek.pages.InitPages;
import pl.kargolek.util.JavascriptDriver;
import pl.kargolek.util.TestProperty;
import pl.kargolek.util.WebDriverUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karol Kuta-Orlowicz
 */

@Tag("TokenBalanceUI")
@Epic("Token balance")
@Feature("Token balance UI")
@BaseTestConfig
@TestDataProvider
@SoftAssertion
@JavascriptDriverExecutor
public class BalanceUITest {

    private static final String RED_LOW_VALUE = "rgba(255, 77, 0, 1)";
    private static final String GREEN_HIGHER_VALUE = "rgba(7, 243, 107, 1)";
    private String appBaseURL;
    private InitPages pages;

    @BeforeEach
    public void setup(TestProperty property, InitPages pages, TestDataSql testDataSql) {
        this.appBaseURL = property.getAppBaseURL();
        this.pages = pages;
        testDataSql.deleteCryptocurrenciesData();
        testDataSql.insertEthereumData();
        testDataSql.insertPolygonData();
        testDataSql.insertAvalancheData();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user I want to see balance component for ETH")
    @Description("ETH balance component for valid wallet")
    public void whenWalletValid_thenEthBalanceComponent(TestData data) {
        var component = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(data.getWalletAddressValid())
                .enterKeyPress()
                .getEthBalanceAmountContainer();

        assertThat(component.isDisplayed()).isTrue();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user I want to see balance component for MATIC")
    @Description("MATIC balance component for valid wallet")
    public void whenWalletValid_thenMaticBalanceComponent(TestData data) {
        var component = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(data.getWalletAddressValid())
                .enterKeyPress()
                .getPolygonBalanceAmountContainer();

        assertThat(component.isDisplayed()).isTrue();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user I want to see balance component for AVAX")
    @Description("AVAX balance component for valid wallet")
    public void whenWalletValid_thenAvaxBalanceComponent(TestData data) {
        var component = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(data.getWalletAddressValid())
                .enterKeyPress()
                .getAvalancheAmountContainer();

        assertThat(component.isDisplayed()).isTrue();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user I can see total amount value for token balance")
    @Description("Total amount count amounts from all of the wallets provided by user for each token")
    public void whenWalletValid_thenTotalAmount(TestData data, SoftAssertions softAssertions) {
        var balancePage = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWalletsClearText()
                .inputWallets(data.getWalletAddressValid())
                .enterKeyPress();

        var ethTotalAmount = balancePage.getEthereumTableAmountPage()
                .getTotalAmountText();
        var polygonTotalAmount = balancePage.getPolygonTableAmountPage()
                .getTotalAmountText();
        var avalancheTotalAmount = balancePage.getAvalancheTableAmountPage()
                .getTotalAmountText();

        softAssertions.assertThat(ethTotalAmount)
                .isNotBlank();
        softAssertions.assertThat(polygonTotalAmount)
                .isNotBlank();
        softAssertions.assertThat(avalancheTotalAmount)
                .isNotBlank();
        softAssertions.assertAll();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user I can see symbol name value for token balance")
    public void whenWalletValid_thenSymbolName(TestData data, SoftAssertions softAssertions) {
        var balancePage = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(data.getWalletAddressValid())
                .enterKeyPress();

        var ethSymbolName = balancePage.getEthereumTableAmountPage()
                .getSymbolText();
        var polygonSymbolName = balancePage.getPolygonTableAmountPage()
                .getSymbolText();
        var avalancheSymbolName = balancePage.getAvalancheTableAmountPage()
                .getSymbolText();

        softAssertions.assertThat(ethSymbolName)
                .isNotBlank();
        softAssertions.assertThat(polygonSymbolName)
                .isNotBlank();
        softAssertions.assertThat(avalancheSymbolName)
                .isNotBlank();
        softAssertions.assertAll();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user I can see total asset value for token balance")
    @Description("Total asset value based on the value of each wallet for each token")
    public void whenWalletValid_thenTotalValue(TestData data, SoftAssertions softAssertions) {
        var balancePage = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(data.getWalletAddressValid())
                .enterKeyPress();

        var ethTotalValue = balancePage.getEthereumTableAmountPage()
                .getTotalValueText();
        var polygonTotalValue = balancePage.getPolygonTableAmountPage()
                .getTotalValueText();
        var avalancheTotalValue = balancePage.getAvalancheTableAmountPage()
                .getTotalValueText();

        softAssertions.assertThat(ethTotalValue)
                .isNotBlank();
        softAssertions.assertThat(polygonTotalValue)
                .isNotBlank();
        softAssertions.assertThat(avalancheTotalValue)
                .isNotBlank();
        softAssertions.assertAll();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user I can switch view of wallet balance details")
    @Description("View can be switch 1. [Address, Amount, Value] 2. [1H, 24h, 7D] 3. [30D, 60D, 90D]")
    public void whenPressSwitchWalletBalanceDetailView_thenViewIsChanged(TestData data, SoftAssertions softAssertions) {
        var balancePage = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(data.getEthereumTestNetWallet().getAddress())
                .enterKeyPress();

        //Address
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getHeaderAddress()
                        .isDisplayed())
                .isTrue();

        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCellAddress()
                        .isDisplayed())
                .isTrue();

        //Amount
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getHeaderAmount()
                        .isDisplayed())
                .isTrue();

        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCellAmount()
                        .isDisplayed())
                .isTrue();

        //Token value
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getHeaderTokenValue()
                        .isDisplayed())
                .isTrue();

        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCellTokenValue()
                        .isDisplayed())
                .isTrue();

        //Open explorer button
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCellOpenExplorerButton()
                        .isDisplayed())
                .isTrue();


        balancePage.getEthereumTableActionBarPage()
                .clickSwitchViewButton();

        //1h
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getHeader1h()
                        .isDisplayed())
                .isTrue();

        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCell1hValue()
                        .isDisplayed())
                .isTrue();

        //24h
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getHeader24h()
                        .isDisplayed())
                .isTrue();

        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCell24hValue()
                        .isDisplayed())
                .isTrue();

        //7d
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getHeader7d()
                        .isDisplayed())
                .isTrue();

        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCell7dValue()
                        .isDisplayed())
                .isTrue();

        //Open explorer button
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCellOpenExplorerButton()
                        .isDisplayed())
                .isTrue();


        balancePage.getEthereumTableActionBarPage()
                .clickSwitchViewButton();

        //30d
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getHeader30d()
                        .isDisplayed())
                .isTrue();

        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCell30dValue()
                        .isDisplayed())
                .isTrue();

        //60d
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getHeader60d()
                        .isDisplayed())
                .isTrue();

        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCell60dValue()
                        .isDisplayed())
                .isTrue();

        //90d
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getHeader90d()
                        .isDisplayed())
                .isTrue();

        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCell90dValue()
                        .isDisplayed())
                .isTrue();

        //Open explorer button
        softAssertions.assertThat(balancePage.getEthereumTableBalancePage()
                        .getFirstCellOpenExplorerButton()
                        .isDisplayed())
                .isTrue();

        softAssertions.assertAll();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("As user I can see wallets counter")
    public void whenMultipleWallets_thenCounterShowsTheNumber(TestData data) {
        var walletsData = data.getWalletAddressValid() + ","
                + data.getWalletAddressValid() + ","
                + data.getWalletAddressValid() + ","
                + data.getWalletAddressValid();

        var balancePage = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(walletsData)
                .enterKeyPress();

        var numberWalletsText = balancePage.getEthereumTableActionBarPage()
                .getNumberWalletsText();

        assertThat(numberWalletsText).isEqualTo("#Address: 4");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user I can see addresses that I provided on the /wallet page")
    @Description("Address is reduce to 11 char.")
    public void whenMultipleWallets_thenAddressesAreInTheProperColumn(TestData data, SoftAssertions softAssertions) {
        var walletData = data.getEthereumTestNetWallet().getAddress() + "," +
                data.getWalletAddressValid();

        var balancePage = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(walletData)
                .enterKeyPress();

        var addressFirst = balancePage.getEthereumTableBalancePage()
                .getFirstCellAddress()
                .getText();

        var addressSecond = balancePage.getEthereumTableBalancePage()
                .getLastCellAddress()
                .getText();

        softAssertions.assertThat(addressFirst)
                .isEqualTo(data.getEthereumTestNetWallet()
                        .getAddress()
                        .substring(0, 10));

        softAssertions.assertThat(addressSecond)
                .isEqualTo(data.getWalletAddressValid()
                        .substring(0, 10));

        softAssertions.assertAll();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("As user in system I am able to open explorer page for ETH wallet address that has been provided")
    @Description("Wallet explorers for test: ETH: https://goerli.etherscan.io/address/. " +
            "All addresses belong to the testnet for each token.")
    public void whenClickOnOpenExplorerForEthereumWalletAddress_thenOpenExplorerPage(TestData data, WebDriver driver) {
        var balancePage = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(data.getWalletAddressValid())
                .enterKeyPress();

        var currentWindow = driver.getWindowHandle();

        balancePage.getEthereumTableBalancePage()
                .getFirstCellOpenExplorerButton()
                .click();
        WebDriverUtil.switchToNextTab(driver, currentWindow);

        assertThat(driver.getCurrentUrl())
                .contains(
                        data.getExplorerAddressTestnetEthereum()
                );
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("As user in system I am able to open explorer page for MATIC wallet address that has been provided")
    @Description("Wallet explorer for test: MATIC: https://mumbai.polygonscan.com/address/" +
            "All addresses belong to the testnet.")
    public void whenClickOnOpenExplorerForPolygonWalletAddress_thenOpenExplorerPage(TestData data, WebDriver driver) {
        var balancePage = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(data.getWalletAddressValid())
                .enterKeyPress();

        var currentWindow = driver.getWindowHandle();

        balancePage.getPolygonTableBalancePage()
                .getFirstCellOpenExplorerButton()
                .click();

        WebDriverUtil.switchToNextTab(driver, currentWindow);

        assertThat(driver.getCurrentUrl())
                .contains(
                        data.getExplorerAddressTestnetPolygon()
                );
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("As user in system I am able to open explorer page for AVAX wallet address that has been provided")
    @Description("Wallet explorer for test: AVAX: https://testnet.snowtrace.io/address/. " +
            "All addresses belong to the testnet.")
    public void whenClickOnOpenExplorerForAvalancheWalletAddress_thenOpenExplorerPage(TestData data, WebDriver driver) {
        var balancePage = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(data.getWalletAddressValid())
                .enterKeyPress();

        var currentWindow = driver.getWindowHandle();

        balancePage.getAvalancheTableBalancePage()
                .getFirstCellOpenExplorerButton()
                .click();

        WebDriverUtil.switchToNextTab(driver, currentWindow);

        assertThat(driver.getCurrentUrl())
                .contains(
                        data.getExplorerAddressTestnetAvalanche()
                );
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("As user when I refresh the balance page, data should be maintained")
    public void whenRefreshThePage_thenDataMaintain(TestData data, WebDriver driver) {
        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(data.getAvalancheTestNetWallet().getAddress())
                .enterKeyPress();

        var totalAmountTextBefore = this.pages.getWalletBalancePage()
                .getEthereumTableAmountPage()
                .getTotalAmountText();

        WebDriverUtil.refreshPage(driver);

        var totalAmountTextAfter = this.pages.getWalletBalancePage()
                .getEthereumTableAmountPage()
                .getTotalAmountText();

        assertThat(totalAmountTextBefore).isEqualTo(totalAmountTextAfter);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("As user when I navigate back and forward, data should be maintained")
    public void whenNavigateBackAndForwardPage_thenDataMaintain(TestData data, WebDriver driver) {
        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(data.getAvalancheTestNetWallet().getAddress())
                .enterKeyPress();

        var totalAmountTextBefore = this.pages.getWalletBalancePage()
                .getEthereumTableAmountPage()
                .getTotalAmountText();

        WebDriverUtil.navigateBack(driver);
        WebDriverUtil.navigateForward(driver);

        var totalAmountTextAfter = this.pages.getWalletBalancePage()
                .getEthereumTableAmountPage()
                .getTotalAmountText();

        assertThat(totalAmountTextBefore).isEqualTo(totalAmountTextAfter);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("As user I can scroll down page when I provide multiple wallets")
    public void whenMultipleAddress_thenCanScrollToLastElementOnPage
            (TestData data, WebDriver driver, JavascriptDriver jsDriver, SoftAssertions softAssertions) {
        var wallets = data.getAvalancheTestNetWallet().getAddress() + "," +
                data.getAvalancheTestNetWallet().getAddress() + "," +
                data.getAvalancheTestNetWallet().getAddress() + "," +
                data.getAvalancheTestNetWallet().getAddress() + "," +
                data.getAvalancheTestNetWallet().getAddress() + "," +
                data.getAvalancheTestNetWallet().getAddress() + "," +
                data.getAvalancheTestNetWallet().getAddress() + "," +
                data.getAvalancheTestNetWallet().getAddress() + "," +
                data.getAvalancheTestNetWallet().getAddress() + "," +
                data.getAvalancheTestNetWallet().getAddress();

        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(wallets)
                .enterKeyPress();

        var browserHeight = driver.manage().window().getSize().getHeight();

        var element = this.pages.getWalletBalancePage()
                .getAvalancheTableBalancePage()
                .getLastCellAddress();

        softAssertions.assertThat(jsDriver.getTopPositionInViewport(element))
                .isGreaterThan(browserHeight);

        this.pages.getWalletBalancePage().getAction()
                .moveToElement(element)
                .perform();

        softAssertions.assertThat(jsDriver.getTopPositionInViewport(element))
                .isLessThan(browserHeight);

        softAssertions.assertAll();
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Story("As user I can see color red indication for lower 1h, 24h, 7d, 30d, 60d, 90d past values")
    @Description("Lower past token values should have color red: rgba(255, 77, 0, 1)")
    public void whenCurrentPriceHigherThanPastPrices_thenValuesAreRed(TestData data, SoftAssertions softAssertions) {
        var wallets = data.getAvalancheTestNetWallet().getAddress() + "," +
                data.getAvalancheTestNetWallet().getAddress();

        var ethTableActionBar = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(wallets)
                .enterKeyPress()
                .getEthereumTableActionBarPage();

        ethTableActionBar.clickSwitchViewButton();

        var element1hCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell1hValue()
                .getCssValue("color");

        var element24hCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell24hValue()
                .getCssValue("color");

        var element7dCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell7dValue()
                .getCssValue("color");

        ethTableActionBar.clickSwitchViewButton();

        var element30dCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell30dValue()
                .getCssValue("color");

        var element60dCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell60dValue()
                .getCssValue("color");

        var element90dCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell90dValue()
                .getCssValue("color");

        softAssertions.assertThat(element1hCssColorValue)
                .isEqualTo(RED_LOW_VALUE);

        softAssertions.assertThat(element24hCssColorValue)
                .isEqualTo(RED_LOW_VALUE);

        softAssertions.assertThat(element7dCssColorValue)
                .isEqualTo(RED_LOW_VALUE);

        softAssertions.assertThat(element30dCssColorValue)
                .isEqualTo(RED_LOW_VALUE);

        softAssertions.assertThat(element60dCssColorValue)
                .isEqualTo(RED_LOW_VALUE);

        softAssertions.assertThat(element90dCssColorValue)
                .isEqualTo(RED_LOW_VALUE);

        softAssertions.assertAll();
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Story("As user I can see color green indication for higher 1h, 24h, 7d, 30d, 60d, 90d past values")
    @Description("Higher past token values should have color green: rgba(7, 243, 107, 1)")
    public void whenCurrentPriceLowerThanPastPrices_thenValuesAreGreen(TestDataSql dataSql,
                                                                       TestData data,
                                                                       SoftAssertions softAssertions) {
        dataSql.deleteCryptocurrenciesData();
        dataSql.insertEthereumDataPricePastNegative();
        dataSql.insertPolygonData();
        dataSql.insertAvalancheData();

        var wallets = data.getAvalancheTestNetWallet().getAddress() + "," +
                data.getAvalancheTestNetWallet().getAddress();

        var ethTableActionBar = this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWallets(wallets)
                .enterKeyPress()
                .getEthereumTableActionBarPage();

        ethTableActionBar.clickSwitchViewButton();

        var element1hCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell1hValue()
                .getCssValue("color");

        var element24hCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell24hValue()
                .getCssValue("color");

        var element7dCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell7dValue()
                .getCssValue("color");

        ethTableActionBar.clickSwitchViewButton();

        var element30dCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell30dValue()
                .getCssValue("color");

        var element60dCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell60dValue()
                .getCssValue("color");

        var element90dCssColorValue = this.pages.getWalletBalancePage()
                .getEthereumTableBalancePage()
                .getFirstCell90dValue()
                .getCssValue("color");

        softAssertions.assertThat(element1hCssColorValue)
                .isEqualTo(GREEN_HIGHER_VALUE);

        softAssertions.assertThat(element24hCssColorValue)
                .isEqualTo(GREEN_HIGHER_VALUE);

        softAssertions.assertThat(element7dCssColorValue)
                .isEqualTo(GREEN_HIGHER_VALUE);

        softAssertions.assertThat(element30dCssColorValue)
                .isEqualTo(GREEN_HIGHER_VALUE);

        softAssertions.assertThat(element60dCssColorValue)
                .isEqualTo(GREEN_HIGHER_VALUE);

        softAssertions.assertThat(element90dCssColorValue)
                .isEqualTo(GREEN_HIGHER_VALUE);

        softAssertions.assertAll();
    }
}