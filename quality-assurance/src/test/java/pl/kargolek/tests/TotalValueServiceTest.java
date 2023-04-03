package pl.kargolek.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.kargolek.data.TestData;
import pl.kargolek.data.script.TestDataSql;
import pl.kargolek.extension.BaseTestConfigBeforeAll;
import pl.kargolek.extension.data.TestDataProvider;
import pl.kargolek.extension.properties.TestProperties;
import pl.kargolek.pages.InitPages;
import pl.kargolek.util.TestProperty;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karol Kuta-Orlowicz
 */

@Tag("TotalValue")
@Epic("Token balance")
@Feature("Total value service")
@BaseTestConfigBeforeAll
@TestProperties
@TestDataProvider
public class TotalValueServiceTest {

    private String baseURL;
    private InitPages pages;

    @BeforeAll
    public static void setupAll(TestDataSql dataSql) {
        dataSql.deleteCryptocurrenciesData();
        dataSql.insertEthereumData();
        dataSql.insertAvalancheData();
        dataSql.insertPolygonData();
    }

    @BeforeEach
    public void setup(TestProperty property, InitPages pages) {
        this.baseURL = property.getAppBaseURL();
        this.pages = pages;
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("As user I want see proper calculation of my wallet with coins at the top of balance page")
    @Description("Total value is number which represents total value of each token balance")
    public void whenProvideOneWallet_thenProperTotalValueData(TestData data) {
        this.pages.getHomePage()
                .open(this.baseURL)
                .inputWalletsClearText()
                .inputWallets(data.getEthereumTestNetWallet().getAddress())
                .enterKeyPress();

        var result = countTotalValueBaseTestData(data, new BigDecimal("1"));

        var expected = this.pages.getWalletBalancePage()
                .getTotalValuePage()
                .waitForTotalValueTextChange()
                .getTotalValueText();

        assertThat(expected)
                .containsOnlyOnce(result);
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("As user I want see proper calculation of my wallet without coins at the top of balance page")
    @Description("Total value is number which represents total value of each token balance")
    public void whenProvideEmptyWallet_thenProperTotalValueData(TestData data) {
        this.pages.getHomePage()
                .open(this.baseURL)
                .inputWalletsClearText()
                .inputWallets(data.getEthereumTestNetWalletEmpty().getAddress())
                .enterKeyPress();

        var result = "0.00 USD";

        this.pages.getWalletBalancePage().getEthereumTableAmountPage().getTotalAmountText();

        var expected = this.pages.getWalletBalancePage()
                .getTotalValuePage()
                .getTotalValueText();

        assertThat(expected)
                .containsOnlyOnce(result);
    }

    private String countTotalValueBaseTestData(TestData data, BigDecimal multiplyBy) {
        var ethereumPrice = data.getEthereumPrice().getPriceCurrent();
        var ethereumValue = data.getEthereumTestNetWallet().getAmount().multiply(ethereumPrice);

        var polygonPrice = data.getPolygonPrice().getPriceCurrent();
        var polygonValue = data.getPolygonTestNetWallet().getAmount().multiply(polygonPrice);

        var avalanchePrice = data.getAvalanchePrice().getPriceCurrent();
        var avalancheValue = data.getAvalancheTestNetWallet().getAmount().multiply(avalanchePrice);

        var tempTotalValueCounted = ethereumValue.add(polygonValue).add(avalancheValue);
        var multiplyTotalValueCounter = tempTotalValueCounted.multiply(multiplyBy).toPlainString();

        return multiplyTotalValueCounter.substring(0, multiplyTotalValueCounter.indexOf(".") + 3) + " USD";
    }

}
