package pl.kargolek.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

/**
 * @author Karol Kuta-Orlowicz
 */
@Slf4j
public class WebDriverResolver {

    public static final String WEBDRIVER = "webDriver";

    public WebDriver getStoredWebDriver(ExtensionContext extensionContext) {
        var driver = extensionContext
                .getStore(ExtensionContext.Namespace.GLOBAL)
                .get(WEBDRIVER, WebDriver.class);
        if (driver == null)
            throw new RuntimeException("Unable get webDriver instance from global store");
        return driver;
    }
}
