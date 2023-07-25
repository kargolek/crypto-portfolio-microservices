package pl.kargolek.extension.driver;

import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import pl.kargolek.extension.util.AnnotationResolver;
import pl.kargolek.util.RemoteWebDriverFactory;
import pl.kargolek.util.ReportEnvironment;
import pl.kargolek.util.TestProperty;

import java.net.MalformedURLException;

/**
 * @author Karol Kuta-Orlowicz
 */
public class WebDriverExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private WebDriver driver;
    private final String STORE_WEBDRIVER_KEY = "webDriver";
    private final AnnotationResolver annotationResolver = new AnnotationResolver();
    private final RemoteWebDriverFactory driverFactory = new RemoteWebDriverFactory();
    private final TestProperty testProperty = TestProperty.getInstance();
    private final ReportEnvironment reportEnvironment = new ReportEnvironment();

    private String browserName;
    private String browserVersion;
    private String osName;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (annotationResolver.getSeleniumWebDriverAnnotation(context)
                .isBeforeAll()) {
            this.initWebDriver(context);
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (!annotationResolver.getSeleniumWebDriverAnnotation(context)
                .isBeforeAll()) {
            initWebDriver(context);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (!annotationResolver.getSeleniumWebDriverAnnotation(context)
                .isBeforeAll()) {
            this.tearDownDriver(context);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (annotationResolver.getSeleniumWebDriverAnnotation(context).isBeforeAll()) {
            this.tearDownDriver(context);
        }

        reportEnvironment.writeAllureEnvProperties(browserName, browserVersion, osName);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .equals(WebDriver.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return this.driver;
    }

    private void initWebDriver(ExtensionContext context) throws MalformedURLException {
        var hubURL = testProperty.getSeleniumHubURL();
        var browserType = testProperty.getBrowserType();

        this.driver = driverFactory.getWebDriver(hubURL, browserType);
        this.driver.manage()
                .window()
                .maximize();

        context.getStore(ExtensionContext.Namespace.GLOBAL)
                .put(STORE_WEBDRIVER_KEY, driver);
        setBrowserName();
        setBrowserVersion();
        setOsName();
    }

    private void tearDownDriver(ExtensionContext context) {
        driver.quit();
        context.getStore(ExtensionContext.Namespace.GLOBAL)
                .remove(STORE_WEBDRIVER_KEY);
    }

    public void setBrowserName() {
        if (this.browserName == null) {
            this.browserName = ((RemoteWebDriver) driver).getCapabilities()
                    .getBrowserName();
        }
    }

    public void setBrowserVersion() {
        if (this.browserVersion == null) {
            this.browserVersion = ((RemoteWebDriver) driver).getCapabilities()
                    .getBrowserVersion();
        }
    }

    public void setOsName() {
        if (this.osName == null) {
            this.osName = ((RemoteWebDriver) driver).getCapabilities()
                    .getPlatformName()
                    .name();
        }
    }
}
