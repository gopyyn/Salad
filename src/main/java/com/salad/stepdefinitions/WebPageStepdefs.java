package com.salad.stepdefinitions;

import com.salad.core.SaladCommands;
import com.salad.enums.MatchType;
import com.salad.enums.TimeUnit;
import com.salad.enums.VerifyType;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class WebPageStepdefs {
    @Given("goto \"(.+)\"")
    public void gotoUrl(String path) {
        SaladCommands.goTo(path);
    }

    @Then("displays \"(.+)\"")
    public void displays(String css) {
        SaladCommands.verifyDisplayed(css);
    }

    @Given("enter \"(.+)\" \"(.+)\"")
    public void enter(String displayName, String value) {
        SaladCommands.enter(displayName, value);
    }

    @Given("select \"(.+)\" \"(.+)\"")
    public void select(String displayName, String value) {
        SaladCommands.select(displayName, value);
    }

    @Given("^click \"(.+)\"")
    public void click(String name) {
       SaladCommands.safeClick(name);
    }

    @And("wait ([a-zA-Z0-9_]+) (.+)")
    public void wait(long waitTime, TimeUnit unit) throws InterruptedException {
        SaladCommands.wait(waitTime, unit);
    }

    @And("waitUntil (ANY|ALL) \"(.+)\"(?: is|was)? ([!a-zA-Z0-9_ ]+)$")
    public void waitUntil(MatchType matchType, String text, String type) {
        SaladCommands.waitUntilForElements(text, matchType, VerifyType.fromValue(type));
    }

    @And("waitUntil \"(.+)\"(?: is| was)? ([!a-zA-Z0-9_ ]+)$")
    public void waitUntil(String text, String condition) {
        SaladCommands.waitUntil(text, VerifyType.fromValue(condition));
    }

    @And("waitUntil ([a-zA-Z0-9_]+)")
    public void waitUntil(VerifyType type) throws InterruptedException {
        SaladCommands.waitUntil(type);
    }

    @Given("hoverAndClick \"(.+)\" \"(.+)\"")
    public void hoverAndClick(String selector, String clickText) {
        SaladCommands.hoverAndClick(selector, clickText);
    }

    @Given("onPage \"(.+)\"")
    public void onPage(String url) {
        SaladCommands.assertPageUrl(url);
    }

    @When("^verify \"(.+)\" ([a-zA-Z0-9_=!]+) \"(.+)\"$")
    public void verify(String expression, String operators, String rhs) {
        String lhs = SaladCommands.getElementValue(expression);
        SaladCommands.match(lhs, MatchType.fromValue(operators), rhs);
    }

    @When("^verify \"(.+)\"(?: is|was)? (!?\\w+)$")
    public void verifyElement(String expression, String condition) {
        SaladCommands.verifyElement(expression, VerifyType.fromValue(condition));
    }

    @And("fill page \"(.+)\"$")
    public void fillPage(String data) {
        SaladCommands.fillPage(data);
    }

    @Given("setValue (.+) = \"(.+)\"")
    public void defineScenarioVar(String name, String value) {
        SaladCommands.setValue(name.replaceAll("['\"]", ""), value);
    }


    @And("alert (.+)")
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
}


