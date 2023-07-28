package pl.kargolek.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import pl.kargolek.extension.BaseTestConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karol Kuta-Orlowicz
 */
@Tag("exampleTag")
@BaseTestConfig
public class ExampleTest {

    @Test
    void test1(WebDriver driver) throws InterruptedException {
        driver.get("http://google.com");
        Thread.sleep(2000);
        assertThat(true).isFalse();
    }

}
