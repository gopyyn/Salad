package com.gopyyn.salad.stepdefinitions;

import com.gopyyn.salad.core.SaladCommands;
import com.gopyyn.salad.enums.MatchType;
import com.gopyyn.salad.enums.TimeUnit;
import com.gopyyn.salad.enums.VerifyType;
import com.gopyyn.salad.utils.AlertUtils;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.*;

public class WebPageStepdefs {
    @Given("^(?:.*)go to \"(.+)\"$")
    @When("^(?:.*)visit \"(.+)\"$")
    @And("^(?:.*)open \"(.+)\"$")
    public void gotoUrl(String path) {
        SaladCommands.goTo(path);
    }

    @Then("^(?:.*)displays \"(.+)\"$")
    public void displays(String css) {
        SaladCommands.verifyDisplayed(css);
    }

    @Given("^(?:.*)(?:enter) \"(.+)\"(?:.*)\"(.+)\"$")
    @When("^(?:.*)(?:input) \"(.+)\"(?:.*)\"(.+)\"$")
    @And("^(?:.*)(?:type) \"(.+)\"(?:.*)\"(.+)\"$")
    public void enter(String displayName, String value) {
        SaladCommands.enter(displayName, value);
    }

    @Given("^(?:.*)select \"(.+)\" \"(.+)\"$")
    public void select(String displayName, String value) {
        SaladCommands.select(displayName, value);
    }

    @When("^(?:.*)click \"(.+)\"$")
    public void click(String name) {
       SaladCommands.click(name);
    }

    @When("^(?:.*)click \"(.+)\"[(\\d+)]")
    public void clickWithIndex(String name, int nthOccurrence) {
        SaladCommands.click(name, nthOccurrence);
    }

//    @And("wait {int} {timeUnit}")
    @Given("^(?:.*)wait (\\d+) (SECONDS|MINUTES|HOURS)$")
    @And("^(?:.*)wait (\\d+) (MILLI_SECONDS)$")
    public void wait(long waitTime, TimeUnit unit) throws InterruptedException {
        SaladCommands.wait(waitTime, unit);
    }

//    @And("^(?:.*?)wait until {anyOrAll} \"(.+)\" is {verifyType}") //commented as cucumber java plugin is not showing all verifyType options
    @When("^(?:.*?)wait until (ANY|ALL)? \"(.+)\" (?:is)? (CLICKABLE|VISIBLE|INVISIBLE|ENABLED|DISABLED)$")
    public void waitUntil(MatchType matchType, String text, VerifyType type) {
        SaladCommands.waitUntilForElements(text, matchType, type);
    }

//    @And("^(?:.*?)wait until \"(.+)\" is/was {verifyType}")
    @When("^(?:.*?)wait until \"(.+)\" (?:is)? (CLICKABLE|VISIBLE|INVISIBLE|ENABLED|DISABLED)$")
    public void waitUntil(String text, String condition) {
        SaladCommands.waitUntil(text, VerifyType.fromValue(condition));
    }

    @Given("^(?:.*?)wait until \"(.+)\" (?:is)? (!VISIBLE)$")
    @When("^(?:.*?)wait until \"(.+)\" (?:is)? (!ENABLED)$")
    @Then("^(?:.*?)wait until \"(.+)\" (?:is)? (!CLICKABLE)$")
    @And("^(?:.*?)wait until \"(.+)\" (?:is)? (NOT_CLICKABLE)$")
    public void waitUntilWithNot(String text, String condition) {
        waitUntil(text, condition);
    }

    @And("^(?:.*?)wait until (PAGELOAD)$")
    public void waitUntil(VerifyType type) throws InterruptedException {
        SaladCommands.waitUntil(type);
    }

    @Given("^(?:.*)hover and click \"(.+)\" \"(.+)\"$")
    public void hoverAndClick(String selector, String clickText) {
        SaladCommands.hoverAndClick(selector, clickText);
    }

    @Given("^(?:.*)on page \"(.+)\"$")
    public void onPage(String url) {
        SaladCommands.assertPageUrl(url);
    }

//    @When("^(?:.*?)verify \"(.+)\" {matchType} \"(.+)\"$") //commented as cucumber java plugin is not showing all matchType options
    @When("^(?:.*?)verify \"(.+)\" (EQUALS|CONTAINS) \"(.+)\"$")
    @Given("^(?:.*?)verify \"(.+)\" (NOT_EQUALS) \"(.+)\"$")
    @Then("^(?:.*?)verify \"(.+)\" (NOT_CONTAINS) \"(.+)\"$")
    @But("^(?:.*?)verify \"(.+)\" (==|!=|!contains|>|<|>=|<=) \"(.+)?\"$")
    public void verify(String expression, String operators, String rhs) {
        SaladCommands.verify(expression, operators, rhs);
    }
    @When("^(?:.*?)verify \"(.+)\" (GREATER_THAN) \"(.+)\"$")
    @And("^(?:.*?)verify \"(.+)\" (LESS_THAN) \"(.+)\"$")
    @Then("^(?:.*?)verify \"(.+)\" (GREATER_THAN_OR_EQUAL_TO) \"(.+)\"$")
    @Given("^(?:.*?)verify \"(.+)\" (LESS_THAN_OR_EQUAL_TO) \"(.+)\"$")
    public void verifyGreaterLesser(String expression, String operators, String rhs) {
        verify(expression, operators, rhs);
    }

//    @When("^(?:.*?)verify \"(.+)\" (is) {verifyType}") //commented as cucumber java plugin (intellisense) is not showing all parameterType options
    @When("^(?:.*?)verify \"(.+)\" (?:is)? (CLICKABLE|VISIBLE|INVISIBLE|ENABLED|DISABLED)$")
    public void verifyElement(String expression, String condition) {
        SaladCommands.verifyElement(expression, VerifyType.fromValue(condition));
    }

    @Given("^(?:.*?)verify \"(.+)\" (?:is)? (!VISIBLE)$")
    @When("^(?:.*?)verify \"(.+)\" (?:is)? (!ENABLED)$")
    @Then("^(?:.*?)verify \"(.+)\" (?:is)? (!CLICKABLE)$")
    @And("^(?:.*?)verify \"(.+)\" (?:is)? (NOT_CLICKABLE)$")
    public void verifyElementWithNot(String expression, String condition) {
        verifyElement(expression, condition);
    }

    @And("^(?:.*)fill page \"(.+)\"$")
    public void fillPage(String data) {
        SaladCommands.fillPage(data);
    }

    @Given("^(?:.*)set value (\\w) = \"(.+)\"$")
    public void defineScenarioVar(String name, String value) {
        SaladCommands.setValue(name.replaceAll("^(?:.*)['\"]", ""), value);
    }

//    @And("^(?:.*)alert {alertAction}")
    @And("^(?:.*?)alert (accept|dismiss|send text|ACCEPT|DISMISS)$")
    @Given("^(?:.*?)alert (SEND_TEXT)$")
    public void alertAccept(String alertAction) {
        SaladCommands.alert(AlertUtils.Actions.fromValue(alertAction));
    }

    @And("^(?:.*)switch window")
    public void switchWindow() {
        SaladCommands.switchWindow();
    }

    @And("^(?:.*)switch window (to) \"(.+)\"$")
    public void switchWindow(String windowName) {
        SaladCommands.switchWindow(windowName);
    }

    @ParameterType("MILLI_SECONDS|SECONDS|MINUTES|HOURS")
    public TimeUnit timeUnit(String timeUnit) {
        return TimeUnit.valueOf(timeUnit);
    }

    @ParameterType("PAGELOAD|CLICKABLE|NOT_CLICKABLE|!CLICKABLE|VISIBLE|INVISIBLE|!VISIBLE|ENABLED|DISABLED|!ENABLED")
    public VerifyType verifyType(String condition) {
        return VerifyType.fromValue(condition);
    }
    @ParameterType("accept|dismiss|send text|ACCEPT|DISMISS|SEND_TEXT")
    public AlertUtils.Actions alertAction(String condition) {
        return AlertUtils.Actions.fromValue(condition);
    }

    @ParameterType("EQUALS|==|NOT_EQUALS|!=|CONTAINS|contains|NOT_CONTAINS|!contains|GREATER_THAN|>|LESS_THAN|<|GREATER_THAN_OR_EQUAL_TO|>=|LESS_THAN_OR_EQUAL_TO|<=")
    public MatchType matchType(String matchType) {
        return MatchType.fromValue(matchType);
    }
    
    @ParameterType("ANY|ALL")
    public MatchType anyOrAll(String matchType) {
        return MatchType.fromValue(matchType);
    }
}


