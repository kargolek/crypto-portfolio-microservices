package pl.kargolek.extension.recording;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import pl.kargolek.util.ContainerNameResolver;
import pl.kargolek.util.ContainerProcessHandler;
import pl.kargolek.util.PathResolver;
import pl.kargolek.util.ReportAttachment;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Karol Kuta-Orlowicz
 */
@Slf4j
class VideoRecordExtension implements BeforeEachCallback, AfterTestExecutionCallback {

    private final ContainerProcessHandler handler = new ContainerProcessHandler();
    private final ReportAttachment reportAttachment = new ReportAttachment();
    private final File tempVideo = new File(PathResolver.TARGET_PATH, "temp_video.mp4");

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        handler.startDockerContainer(ContainerNameResolver.CHROME_VIDEO_CONTAINER_NAME);
        Thread.sleep(1200);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        handler.stopDockerContainer(ContainerNameResolver.CHROME_VIDEO_CONTAINER_NAME);

        if (validateVideoTempFile()) {
            var attachmentVideo = new File(PathResolver.TARGET_PATH, "video-" + RandomUtils.nextLong() + ".mp4");
            tempVideo.renameTo(attachmentVideo);

            if (isVideoOnFailedTest(extensionContext)) {
                if (isTestFailed(extensionContext)) {
                    reportAttachment.createAttachment(attachmentVideo, "Video attachment", ReportAttachment.AttachmentType.VIDEO_MP4);
                }
            } else {
                reportAttachment.createAttachment(attachmentVideo, "Video attachment", ReportAttachment.AttachmentType.VIDEO_MP4);
            }
        }
    }

    private boolean validateVideoTempFile() {
        if (!tempVideo.exists()) {
            log.error("Video temp file doesn't exist. Attach video file process skipped.");
            return false;
        } else
            return true;
    }

    private boolean isVideoOnFailedTest(ExtensionContext extensionContext) {
        var annotationMethod = extensionContext
                .getTestMethod()
                .orElseThrow()
                .getAnnotation(VideoRecording.class);

        var annotationClass = extensionContext
                .getTestClass()
                .orElseThrow()
                .getAnnotation(VideoRecording.class);
        if (annotationClass != null) {
            return annotationClass.onFailedTest();
        }

        if (annotationMethod != null) {
            return annotationMethod.onFailedTest();
        }

        var videoRecordAnnotations = Arrays.stream(extensionContext
                        .getTestClass()
                        .orElseThrow()
                        .getAnnotations())
                .map(annotation -> annotation.annotationType().getAnnotation(VideoRecording.class))
                .filter(Objects::nonNull)
                .toList();

        if (videoRecordAnnotations.size() > 0)
            return videoRecordAnnotations.get(0).onFailedTest();

        return false;
    }

    private boolean isTestFailed(ExtensionContext extensionContext) {
        return extensionContext
                .getExecutionException()
                .isPresent();
    }
}
