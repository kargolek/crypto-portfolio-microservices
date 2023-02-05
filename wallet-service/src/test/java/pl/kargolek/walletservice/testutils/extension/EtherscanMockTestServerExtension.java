package pl.kargolek.walletservice.testutils.extension;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.extension.*;

/**
 * @author Karol Kuta-Orlowicz
 */
public class EtherscanMockTestServerExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    public static MockWebServer etherscanMockWebServer;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        etherscanMockWebServer = new MockWebServer();
        etherscanMockWebServer.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        etherscanMockWebServer.close();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return null;
    }
}
