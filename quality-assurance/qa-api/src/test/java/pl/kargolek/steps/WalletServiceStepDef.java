package pl.kargolek.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Karol Kuta-Orlowicz
 */
public class WalletServiceStepDef {

    @Given("the example something")
    public void theExampleSomething() {
    }

    @When("something happen")
    public void somethingHappen() {
    }

    @Then("result expected")
    public void resultExpected() {
    }

    @And("seconds result expected")
    public void secondsResultExpected() {
        assertTrue(true);
    }

    @Given("the example something data: {string}")
    public void theExampleSomethingDataDataTest(String arg0) {
        System.out.println(arg0);
    }

}
