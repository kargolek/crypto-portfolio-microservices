package pl.kargolek.extension.javascript;

import org.junit.jupiter.api.extension.*;
import pl.kargolek.extension.util.AnnotationResolver;
import pl.kargolek.util.JavascriptDriver;
import pl.kargolek.util.WebDriverResolver;

/**
 * @author Karol Kuta-Orlowicz
 */
public class JavascriptDriverExtension implements ParameterResolver, BeforeAllCallback, BeforeEachCallback {

    private JavascriptDriver javascriptDriver;
    private final WebDriverResolver webDriverResolver = new WebDriverResolver();
    private final AnnotationResolver annotationResolver = new AnnotationResolver();

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        if (annotationResolver.getSeleniumWebDriverAnnotation(extensionContext).isBeforeAll()) {
            initDriver(extensionContext);
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        if (!annotationResolver.getSeleniumWebDriverAnnotation(extensionContext).isBeforeAll()) {
            initDriver(extensionContext);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(JavascriptDriver.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return this.javascriptDriver;
    }

    private void initDriver(ExtensionContext context) {
        var driver = this.webDriverResolver.getStoredWebDriver(context);
        this.javascriptDriver = JavascriptDriver
                .builder(driver)
                .createJavascriptExecutor()
                .build();
    }
}
