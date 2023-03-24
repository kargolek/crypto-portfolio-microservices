package pl.kargolek.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import pl.kargolek.data.TestData;
import pl.kargolek.data.script.TestDataSql;
import pl.kargolek.extension.BaseTestConfigBeforeAll;
import pl.kargolek.extension.data.TestDataProvider;
import pl.kargolek.pages.InitPages;
import pl.kargolek.util.TestProperty;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karol Kuta-Orlowicz
 */

@Tags({
 @Tag("InputWalletUI"),
 @Tag("ToastErrorHandler")
})
@Epic("Wallet provider")
@Feature("Error handler")
@BaseTestConfigBeforeAll
@TestDataProvider
public class WalletProviderErrorHandlerTest {

    private static final String TOAST_TITLE_INFO = "Info";
    private static final String TOAST_INFO_WALLET_SYNTAX = "Please provide correct wallets data. Check wallet address syntax.";
    private static final String TOAST_ERROR_TITLE = "Error";
    private static final String TOAST_ERROR_MESSAGE = "Internal system error. Please try again later.";
    private String appBaseURL;
    private InitPages pages;

    @BeforeAll
    public static void setupAll(TestDataSql testDataSql){
        testDataSql.deleteCryptocurrenciesData();
        testDataSql.insertEthereumData();
        testDataSql.insertPolygonData();
    }

    @BeforeEach
    public void setupEach(TestProperty testProperty, InitPages initPages){
        this.appBaseURL = testProperty.getAppBaseURL();
        this.pages = initPages;
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user, when send blank wallet address, info should be raise")
    @Description("For blank address system should inform user about wrong action")
    public void whenBlankWallet_thenInfoToast(){
        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWalletsClearText()
                .inputWallets("")
                .enterKeyPress();

        var toastTitle = this.pages.getToastPage()
                .getToastTitle();
        var toastMessage = this.pages.getToastPage()
                .getToastMessage();

        assertThat(toastTitle).isEqualTo(TOAST_TITLE_INFO);
        assertThat(toastMessage).isEqualTo(TOAST_INFO_WALLET_SYNTAX);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user, when send too short wallet address, info should be raise")
    @Description("For too short address system should inform user about wrong action")
    public void whenToShortWalletAddress_thenInfoToast(TestData data){
        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWalletsClearText()
                .inputWallets(data.getWalletAddressToShort())
                .enterKeyPress();

        var toastTitle = this.pages.getToastPage()
                .getToastTitle();
        var toastMessage = this.pages.getToastPage()
                .getToastMessage();

        assertThat(toastTitle).isEqualTo(TOAST_TITLE_INFO);
        assertThat(toastMessage).isEqualTo(TOAST_INFO_WALLET_SYNTAX);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user, when send too long wallet address, info should be raise")
    @Description("For too long address system should inform user about wrong action")
    public void whenToLongWalletAddress_thenInfoToast(TestData data){
        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWalletsClearText()
                .inputWallets(data.getWalletAddressToLong())
                .enterKeyPress();

        var toastTitle = this.pages.getToastPage()
                .getToastTitle();
        var toastMessage = this.pages.getToastPage()
                .getToastMessage();

        assertThat(toastTitle).isEqualTo(TOAST_TITLE_INFO);
        assertThat(toastMessage).isEqualTo(TOAST_INFO_WALLET_SYNTAX);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user, when send invalid wallet address, info should be raise")
    @Description("For invalid address system should inform user about wrong action")
    public void whenInvalidWalletAddress_thenInfoToast(TestData data){
        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWalletsClearText()
                .inputWallets(data.getWalletAddressInvalid())
                .enterKeyPress();

        var toastTitle = this.pages.getToastPage()
                .getToastTitle();
        var toastMessage = this.pages.getToastPage()
                .getToastMessage();

        assertThat(toastTitle).isEqualTo(TOAST_TITLE_INFO);
        assertThat(toastMessage).isEqualTo(TOAST_INFO_WALLET_SYNTAX);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user, when send wallet address with illegal char, info should be raise")
    @Description("For wallet address with illegal char system should inform user about wrong action")
    public void whenWalletAddressIllegalChar_thenInfoToast(TestData data){
        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWalletsClearText()
                .inputWallets(data.getWalletAddressIllegalChar())
                .enterKeyPress();

        var toastTitle = this.pages.getToastPage()
                .getToastTitle();
        var toastMessage = this.pages.getToastPage()
                .getToastMessage();

        assertThat(toastTitle).isEqualTo(TOAST_TITLE_INFO);
        assertThat(toastMessage).isEqualTo(TOAST_INFO_WALLET_SYNTAX);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user, when send multiple wallet addresses with incorrect syntax, info should be raise")
    @Description("For incorrect wallets syntax '%s,%s' system should inform user about wrong action")
    public void whenIncorrectWalletsSyntax_thenInfoToast(TestData data){
        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWalletsClearText()
                .inputWallets(data.getWalletAddressValid() + ", " + data.getWalletAddressValid())
                .enterKeyPress();

        var toastTitle = this.pages.getToastPage()
                .getToastTitle();
        var toastMessage = this.pages.getToastPage()
                .getToastMessage();

        assertThat(toastTitle).isEqualTo(TOAST_TITLE_INFO);
        assertThat(toastMessage).isEqualTo(TOAST_INFO_WALLET_SYNTAX);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user, when send wallet addresses and ETH, MATIC price is not available, then system show error toast")
    @Description("For case of crypto price is not available")
    public void whenNoEthPolygonPrice_thenSystemError(TestData data, TestDataSql sqlData, WebDriver driver){
        sqlData.deleteCryptocurrenciesData();
        driver.navigate().refresh();

        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWalletsClearText()
                .inputWallets(data.getWalletAddressValid())
                .enterKeyPress();

        var toastTitle = this.pages.getToastPage()
                .getToastTitle();
        var toastMessage = this.pages.getToastPage()
                .getToastMessage();

        assertThat(toastTitle).isEqualTo(TOAST_ERROR_TITLE);
        assertThat(toastMessage).isEqualTo(TOAST_ERROR_MESSAGE);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("As user, when send wallet addresses and only ETH price is not available, then system show error toast")
    @Description("For case of crypto price is not available")
    public void whenNoEthPrice_thenSystemError(TestData data, TestDataSql sqlData, WebDriver driver){
        sqlData.deleteCryptocurrenciesData();
        sqlData.insertEthereumData();
        driver.navigate().refresh();

        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWalletsClearText()
                .inputWallets(data.getWalletAddressValid())
                .enterKeyPress();

        var toastTitle = this.pages.getToastPage()
                .getToastTitle();
        var toastMessage = this.pages.getToastPage()
                .getToastMessage();

        assertThat(toastTitle).isEqualTo(TOAST_ERROR_TITLE);
        assertThat(toastMessage).isEqualTo(TOAST_ERROR_MESSAGE);
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("As user, when send invalid wallet addresses, then app should maintain the current address path")
    @Description("System navigate to /balance and if error occurred then should get back to /wallet path")
    public void whenInvalidAddress_thenUrlPathMaintain(TestData data, WebDriver driver){
        this.pages.getHomePage()
                .open(this.appBaseURL)
                .inputWalletsClearText()
                .inputWallets(data.getWalletAddressInvalid())
                .enterKeyPress();
        this.pages.getInputWalletPage()
                .getAppTitleText();

        assertThat(driver.getCurrentUrl()).isEqualTo(this.appBaseURL + "/wallet");
    }
}
