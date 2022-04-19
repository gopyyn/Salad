package com.salad.stepdefinitions;

import com.salad.core.SaladCommands;
import com.salad.enums.MatchType;
import com.salad.enums.TimeUnit;
import com.salad.enums.VerifyType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class WebPageStepdefs {
    @Given("goto {string}")
    public void gotoUrl(String path) {
        SaladCommands.goTo(path);
    }

    @Then("displays {string}")
    public void displays(String css) {
        SaladCommands.verifyDisplayed(css);
    }

    @Given("enter {string} {string}")
    public void enter(String displayName, String value) {
        SaladCommands.enter(displayName, value);
    }

    @Given("select {string} {string}")
    public void select(String displayName, String value) {
        SaladCommands.select(displayName, value);
    }

    @When("click {string}")
    public void click(String name) {
       SaladCommands.safeClick(name, 1);
    }

    @When("click {string}[{int}]")
    public void clickWithIndex(String name, int nthOccurrence) {
        SaladCommands.safeClick(name, nthOccurrence);
    }

    @And("wait {int} {timeUnit}")
    public void wait(long waitTime, TimeUnit unit) throws InterruptedException {
        SaladCommands.wait(waitTime, unit);
    }

    @And("waitUntil {anyOrAll} {string} is/was {verifyType}")
    public void waitUntil(MatchType matchType, String text, VerifyType type) {
        SaladCommands.waitUntilForElements(text, matchType, type);
    }

    @And("waitUntil {string} is/was {verifyType}")
    public void waitUntil(String text, VerifyType condition) {
        SaladCommands.waitUntil(text, condition);
    }

    @And("waitUntil {string}")
    public void waitUntil(VerifyType type) throws InterruptedException {
        SaladCommands.waitUntil(type);
    }

    @Given("hoverAndClick {string} {string}")
    public void hoverAndClick(String selector, String clickText) {
        SaladCommands.hoverAndClick(selector, clickText);
    }

    @Given("onPage {string}")
    public void onPage(String url) {
        SaladCommands.assertPageUrl(url);
    }

//    @When("verify {string} {matchType} {string}") //commented as cucumber java plugin is not showing all matchType options
    @When("^verify \"(.+)\" (EQUALS|CONTAINS) \"(.+)\"$")
    @When("^verify \"(.+)\" (NOT_EQUALS) \"(.+)\"$")
    @When("^verify \"(.+)\" (NOT_CONTAINS) \"(.+)\"$")
    @When("^verify \"(.+)\" (GREATER_THAN) \"(.+)\"$")
    @When("^verify \"(.+)\" (LESS_THAN) \"(.+)\"$")
    @When("^verify \"(.+)\" (GREATER_THAN_OR_EQUAL_TO) \"(.+)\"$")
    @When("^verify \"(.+)\" (LESS_THAN_OR_EQUAL_TO) \"(.+)\"$")
    @When("^verify \"(.+)\" (==|!=|!contains|>|<|>=|<=) \"(.+)\"$")
    public void verify(String expression, String operators, String rhs) {
        String lhs = SaladCommands.getElementValue(expression);
        SaladCommands.match(lhs, MatchType.fromValue(operators), rhs);
    }

//    @When("verify {string} (is) {verifyType}") //commented as cucumber java plugin is not showing all verifyType options
    @When("^verify \"(.+)\" (?:is)? (CLICKABLE|VISIBLE|INVISIBLE|ENABLED|DISABLED)$")
    @When("^verify \"(.+)\" (?:is)? (!VISIBLE)$")
    @When("^verify \"(.+)\" (?:is)? (!ENABLED)$")
    @When("^verify \"(.+)\" (?:is)? (!CLICKABLE)$")
    @When("^verify \"(.+)\" (?:is)? (NOT_CLICKABLE)$")
    public void verifyElement(String expression, String condition) {
        SaladCommands.verifyElement(expression, VerifyType.fromValue(condition));
    }

    @And("fill page {string}")
    public void fillPage(String data) {
        SaladCommands.fillPage(data);
    }

    @Given("setValue {word} = {string}")
    public void defineScenarioVar(String name, String value) {
        SaladCommands.setValue(name.replaceAll("['\"]", ""), value);
    }

    @And("alert {word}")
    public void alertAccept(String alertAction) {
        SaladCommands.alert(alertAction);
    }

    @And("switch window")
    public void switchWindow() {
        SaladCommands.switchWindow();
    }

    @And("switch window (to) {string}")
    public void switchWindow(String windowName) {
        SaladCommands.switchWindow(windowName);
    }

    @ParameterType("MILLI_SECOND|SECOND|MINUTE|HOUR")
    public TimeUnit timeUnit(String timeUnit) {
        return TimeUnit.valueOf(timeUnit);
    }

    @ParameterType("PAGELOAD|CLICKABLE|NOT_CLICKABLE|!CLICKABLE|VISIBLE|INVISIBLE|!VISIBLE|ENABLED|DISABLED|!ENABLED")
    public VerifyType verifyType(String condition) {
        return VerifyType.fromValue(condition);
    }

    @ParameterType("EQUALS|==|NOT_EQUALS|!=|CONTAINS|contains|NOT_CONTAINS|!contains|GREATER_THAN|>|LESS_THAN|<|GREATER_THAN_OR_EQUAL_TO|>=|LESS_THAN_OR_EQUAL_TO|<=|ANY|ALL")
    public MatchType matchType(String matchType) {
        return MatchType.fromValue(matchType);
    }
    
    @ParameterType("ANY|ALL")
    public MatchType anyOrAll(String matchType) {
        return MatchType.fromValue(matchType);
    }
}


