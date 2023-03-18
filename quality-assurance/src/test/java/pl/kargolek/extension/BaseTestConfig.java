package pl.kargolek.extension;

import org.junit.jupiter.api.extension.ExtendWith;
import pl.kargolek.extension.devtools.DevTools;
import pl.kargolek.extension.driver.WebDriverExtension;
import pl.kargolek.extension.logs.BrowserLogsExtension;
import pl.kargolek.extension.properties.TestPropertiesExtension;
import pl.kargolek.extension.recording.VideoRecording;
import pl.kargolek.extension.screenshot.TakeScreenshot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Karol Kuta-Orlowicz
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({WebDriverExtension.class, BrowserLogsExtension.class, TestPropertiesExtension.class})
@VideoRecording
@TakeScreenshot
@DevTools
public @interface BaseTestConfig {
}
