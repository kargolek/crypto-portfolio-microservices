package pl.kargolek.extension.properties;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import pl.kargolek.util.PropertiesLoader;

/**
 * @author Karol Kuta-Orlowicz
 */
public class TestPropertiesExtension implements ParameterResolver {

    private static final String TEST_RESOURCES_PATH = Thread
            .currentThread()
            .getContextClassLoader()
            .getResource("")
            .getPath();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(PropertiesLoader.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return new PropertiesLoader(TEST_RESOURCES_PATH, "conf.properties");
    }
}
