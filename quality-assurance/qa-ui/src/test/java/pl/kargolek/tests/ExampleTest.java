package pl.kargolek.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import pl.kargolek.extension.BaseTestConfig;

/**
 * @author Karol Kuta-Orlowicz
 */
@Tag("exampleTag")
@BaseTestConfig
public class ExampleTest {

    @Test
    void test1(WebDriver driver) {
        var browser = System.getProperty("browser");
        System.out.println("Browser:" + browser);
    }

}
