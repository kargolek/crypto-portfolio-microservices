package pl.kargolek.extension.devtools;

import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.devtools.v110.network.model.MonotonicTime;
import pl.kargolek.util.DevToolsDriver;
import pl.kargolek.util.ReportAttachment;
import pl.kargolek.util.WebDriverResolver;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * @author Karol Kuta-Orlowicz
 */
public class DevToolsExtension implements ParameterResolver, BeforeAllCallback, AfterAllCallback, AfterEachCallback {
    private DevToolsDriver devToolsDriver;
    private final ReportAttachment reportAttachment = new ReportAttachment();
    private final WebDriverResolver webDriverResolver = new WebDriverResolver();

    @Override
    public void beforeAll(ExtensionContext context) {
        var webDriver = webDriverResolver.getStoredWebDriver(context);
        this.devToolsDriver = DevToolsDriver
                .builder(webDriver)
                .createConnection()
                .build();
        this.devToolsDriver.setNetworkRequestListener();
        this.devToolsDriver.setNetworkResponsesListener();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        var requestsData = this.devToolsDriver.getRequests().stream()
                .map(requestModel -> String.format("\nRequest:[%s]\n%s URL:%s\nHeader:%s\nPayload:%s\n",
                        this.convert(requestModel.timestamp()),
                        requestModel.request().getMethod(),
                        requestModel.request().getUrl(),
                        requestModel.request().getHeaders(),
                        requestModel.request().getPostData().orElse("No post data")))
                .collect(Collectors.joining(System.lineSeparator()));

        var responseData = this.devToolsDriver.getResponses().stream()
                .map(responseModel -> String.format("\nResponse: [%s]\nURL:%s\nStatus:%s %s\nHeader:%s\nBody:%s\n",
                        this.convert(responseModel.timestamp()),
                        responseModel.response().getUrl(),
                        responseModel.response().getStatus(),
                        responseModel.response().getStatusText(),
                        responseModel.response().getHeaders(),
                        responseModel.body()))
                .collect(Collectors.joining(System.lineSeparator()));

        var attachedData = String.format("[REQUESTS DATA]:\n%s\n\n[RESPONSE DATA]:\n%s", requestsData, responseData);

        reportAttachment.createAttachment(
                attachedData.getBytes(StandardCharsets.UTF_8),
                "Network",
                ReportAttachment.AttachmentType.TEXT_LOG);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        this.devToolsDriver.closeConnection();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == DevToolsDriver.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return this.devToolsDriver;
    }

    private LocalDateTime convert(MonotonicTime timestamp) {
        Duration duration = Duration.of((long) (Double.parseDouble(timestamp.toString())), ChronoUnit.MILLIS);
        return LocalDateTime.now(ZoneOffset.UTC).minus(duration);
    }
}
