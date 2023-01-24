package pl.kargolek.cryptopriceservice.extension;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;

/**
 * @author Karol Kuta-Orlowicz
 */
public class MockWebServerExtension implements BeforeAllCallback, AfterAllCallback  {

    public static MockWebServer mockWebServer;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
       mockWebServer.shutdown();
    }
}
