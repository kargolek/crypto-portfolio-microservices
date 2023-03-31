package pl.kargolek.extension.driver;

import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import pl.kargolek.extension.util.AnnotationResolver;
import pl.kargolek.util.PropertiesLoader;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Karol Kuta-Orlowicz
 */
public class WebDriverExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private WebDriver driver;
    private final String STORE_WEBDRIVER_KEY = "webDriver";
    private final AnnotationResolver annotationResolver = new AnnotationResolver();

    private final PropertiesLoader confProperties = new PropertiesLoader(
            Thread.currentThread().getContextClassLoader().getResource("").getPath(),
            "conf.properties"
    );

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (annotationResolver.getSeleniumWebDriverAnnotation(context).isBeforeAll()){
            this.initWebDriver(context);
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (!annotationResolver.getSeleniumWebDriverAnnotation(context).isBeforeAll()){
            initWebDriver(context);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (!annotationResolver.getSeleniumWebDriverAnnotation(context).isBeforeAll()){
            this.tearDownDriver(context);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (annotationResolver.getSeleniumWebDriverAnnotation(context).isBeforeAll()){
            this.tearDownDriver(context);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(WebDriver.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return this.driver;
    }

    private void initWebDriver(ExtensionContext context) throws MalformedURLException {
        var hubURL = confProperties.getPropertyValue("selenium.hub.url");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setCapability("browserVersion", "110.0");
        chromeOptions.setCapability("platformName", "LINUX");

        driver = new RemoteWebDriver(new URL(hubURL), chromeOptions, false);
        driver.manage().window().maximize();

        context.getStore(ExtensionContext.Namespace.GLOBAL).put(STORE_WEBDRIVER_KEY, driver);
    }

    private void tearDownDriver(ExtensionContext context){
        driver.quit();
        context.getStore(ExtensionContext.Namespace.GLOBAL).remove(STORE_WEBDRIVER_KEY);
    }
}
