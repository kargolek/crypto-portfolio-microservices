package pl.kargolek.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.kargolek.data.TestData;
import pl.kargolek.data.script.TestDataSql;
import pl.kargolek.extension.BaseTestConfigBeforeAll;
import pl.kargolek.extension.assertion.SoftAssertion;
import pl.kargolek.extension.data.TestDataProvider;
import pl.kargolek.extension.visual.VisualCompare;
import pl.kargolek.pages.InitPages;
import pl.kargolek.util.TestProperty;
import pl.kargolek.util.VisualCompareService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karol Kuta-Orlowicz
 */
@Tag("VisualCompareTest")
@BaseTestConfigBeforeAll
@TestDataProvider
@SoftAssertion
@VisualCompare
public class ExampleTest {

    private String baseURL;
    private InitPages pages;

    @BeforeEach
    public void setup(TestProperty property, InitPages pages, TestDataSql dataSql) {
        this.baseURL = property.getAppBaseURL();
        this.pages = pages;

        dataSql.deleteCryptocurrenciesData();
        dataSql.insertEthereumData();
        dataSql.insertAvalancheData();
        dataSql.insertPolygonData();
    }

    @Test
    public void whenCompareSameElement_thenPass(TestData data, VisualCompareService service) {
        var page = this.pages.getHomePage()
                .open(this.baseURL)
                .inputWalletsClearText()
                .inputWallets(data.getEthereumTestNetWallet().getAddress())
                .enterKeyPress()
                .getTotalValuePage();

        var trendLineChartContainer = page.waitForStopAnimation()
                .getTrendLineChartContainer();

        var result = service.compareElement(trendLineChartContainer, "trend_line_chart_container");

        assertThat(result).isEqualTo(0.0);

    }

    @Test
    public void whenCompareDiffElement_thenFail(TestData data, VisualCompareService service) {
        var page = this.pages.getHomePage()
                .open(this.baseURL)
                .inputWalletsClearText()
                .inputWallets(data.getEthereumTestNetWallet().getAddress())
                .enterKeyPress()
                .getTotalValuePage();

        var otherElement = page.waitForTotalValueTextChange()
                .waitForStopAnimation()
                .getTotalValueDiffPercent();

        var result = service.compareElement(otherElement, "trend_line_chart_container");

        assertThat(result).isGreaterThan(0.0);

    }

    @Test
    public void whenCompareSameElementWithDiffData_thenFail(TestData data, VisualCompareService service) {
        var page = this.pages.getHomePage()
                .open(this.baseURL)
                .inputWalletsClearText()
                .inputWallets(data.getEthereumTestNetWalletEmpty().getAddress())
                .enterKeyPress()
                .getTotalValuePage();

        var trendLineChartContainer = page.waitForStopAnimation()
                .getTrendLineChartContainer();

        var result = service.compareElement(trendLineChartContainer, "trend_line_chart_container");

        assertThat(result).isGreaterThan(0.0);
    }

}
