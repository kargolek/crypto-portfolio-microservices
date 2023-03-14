package pl.kargolek.extension;

import io.qameta.allure.Allure;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Karol Kuta-Orlowicz
 */
public class VideoRecordExt implements BeforeEachCallback, AfterEachCallback {

    private final File tempVideo = new File(System.getProperty("user.dir") + "/target", "chrome_video.mp4");

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        startDockerContainer("quality-assurance_chrome_video_1");
        Thread.sleep(1200);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        stopDockerContainer("quality-assurance_chrome_video_1");

        if(!tempVideo.exists()){
            throw new RuntimeException("Unable to locate temp video");
        }

        var renamedVid = new File(System.getProperty("user.dir") + "/target", "video-" + RandomUtils.nextLong() + ".mp4");
        tempVideo.renameTo(renamedVid);
        try {
            byte[] byteArray = IOUtils.toByteArray(new FileInputStream(renamedVid));
            Allure.addAttachment("video attachment", "video/mp4", new ByteArrayInputStream(byteArray), "mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDockerContainer(String containerName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("docker", "start", containerName);
        Process process = pb.start();
        process.waitFor();
    }

    public void stopDockerContainer(String containerName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("docker", "stop", containerName);
        Process process = pb.start();
        process.waitFor();
    }
}
