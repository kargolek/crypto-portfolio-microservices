package pl.kargolek.steps;

import io.cucumber.java.en.Then;

/**
 * @author Karol Kuta-Orlowicz
 */
public class BaseStepsDef {

    @Then("result expected status {int}")
    public void resultExpectedStatus(Integer statusCode) {
        System.out.println("Status code: " + statusCode);
    }

}
