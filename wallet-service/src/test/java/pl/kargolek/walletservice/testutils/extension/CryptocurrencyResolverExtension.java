package pl.kargolek.walletservice.testutils.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import pl.kargolek.walletservice.testutils.fixture.CryptocurrencyDataResolver;

/**
 * @author Karol Kuta-Orlowicz
 */
public class CryptocurrencyResolverExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == CryptocurrencyDataResolver.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return new CryptocurrencyDataResolver();
    }
}
