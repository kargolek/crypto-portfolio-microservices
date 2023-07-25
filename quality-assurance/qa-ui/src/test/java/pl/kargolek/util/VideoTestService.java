package pl.kargolek.util;

import java.io.IOException;

/**
 * @author Karol Kuta-Orlowicz
 */
public class VideoTestService {

    private final BrowserType browserType = TestProperty.getInstance().getBrowserType();
    private final ContainerProcessHandler handler = new ContainerProcessHandler();

    public void startRecord(int sleepAfterStartContainer) throws IOException, InterruptedException {
        if (browserType == BrowserType.CHROME) {
            handler.startDockerContainer(ContainerNameResolver.CHROME_VIDEO_CONTAINER_NAME);
        } else if (browserType == BrowserType.FIREFOX) {
            handler.startDockerContainer(ContainerNameResolver.FIREFOX_VIDEO_CONTAINER_NAME);
        }
        Thread.sleep(sleepAfterStartContainer);
    }

    public void stopRecord() throws IOException, InterruptedException {
        if (browserType == BrowserType.CHROME) {
            handler.stopDockerContainer(ContainerNameResolver.CHROME_VIDEO_CONTAINER_NAME);
        } else if (browserType == BrowserType.FIREFOX) {
            handler.stopDockerContainer(ContainerNameResolver.FIREFOX_VIDEO_CONTAINER_NAME);
        }
    }
}
