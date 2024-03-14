package com.gopyyn.salad.stepdefinitions;

import com.gopyyn.salad.selenium.Driver;
import com.gopyyn.salad.utils.HibernateUtils;
import com.gopyyn.salad.core.SaladCommands;
import com.gopyyn.salad.enums.MatchType;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import java.util.Hashtable;

public class SaladCommonStepdefs {
    private static Hashtable<String, Driver> activeDrivers = new Hashtable<>(); //NOSONAR [squid:S1149] shared by multiple threads

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            activeDrivers.forEach(((key, driver)-> driver.cleanup()));
            HibernateUtils.shutdown();
            HibernateUtils.closeSession();
        }));
    }

    @Given("^def (.+) = \"(.+)\"$")
    public void defineFeatureVar(String name, String value) {
        SaladCommands.def(name.replaceAll("['\"]", ""), value);
    }

    @Given("^set (.+) = \"(.+)\"$")
    public void defineScenarioVar(String name, String value) {
        SaladCommands.set(name.replaceAll("['\"]", ""), value);
    }

    @Given("^print (.+)$")
    public void print(String value) {
        SaladCommands.print(value);
    }

//    @When("^(?:assert|match) (.+) ([a-zA-Z0-9_=!<>]+) (.+)$")
    @When("^(?:assert) (.+) (EQUALS|CONTAINS|equals|contains) (.+)$")
    @When("^(?:assert) (.+) (NOT_EQUALS) (.+)$")
    @When("^(?:assert) (.+) (NOT_CONTAINS) (.+)$")
    @When("^(?:assert) (.+) (GREATER_THAN) (.+)$")
    @When("^(?:assert) (.+) (LESS_THAN) (.+)$")
    @When("^(?:assert) (.+) (GREATER_THAN_OR_EQUAL_TO) (.+)$")
    @When("^(?:assert) (.+) (LESS_THAN_OR_EQUAL_TO) (.+)$")
    @When("^(?:assert) (.+) (==|!=|!contains|>|<|>=|<=) (.+)$")
    @When("^(?:match) (.+) (EQUALS|CONTAINS|equals|contains) (.+)$")
    @When("^(?:match) (.+) (NOT_EQUALS) (.+)$")
    @When("^(?:match) (.+) (NOT_CONTAINS) (.+)$")
    @When("^(?:match) (.+) (GREATER_THAN) (.+)$")
    @When("^(?:match) (.+) (LESS_THAN) (.+)$")
    @When("^(?:match) (.+) (GREATER_THAN_OR_EQUAL_TO) (.+)$")
    @When("^(?:match) (.+) (LESS_THAN_OR_EQUAL_TO) (.+)$")
    @When("^(?:match) (.+) (==|!=|!contains|>|<|>=|<=) (.+)$")
    public void match(String expression, String operators, String rhs) {
        SaladCommands.match(expression, MatchType.fromValue(operators), rhs);
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println(scenario);
        beforeFeature(scenario);
    }

    private void beforeFeature(Scenario scenario) {
        if (scenario.getUri().getPath().equals(SaladCommands.getCurrentFeature())) {
            return;
        }
        SaladCommands.setCurrentFeature(scenario.getUri().getPath());
        //clears previous feature variables
        SaladCommands.clearFeatureVariables();
        SaladCommands.cleanupDriver(scenario);
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (Boolean.valueOf(SaladCommands.getVariableAsStringIfPresent("skip_browser_restart"))) {
            SaladCommands.takeSnapShot(scenario);
            activeDrivers.putIfAbsent(scenario.getUri().getPath(), Driver.instance());
        } else {
            SaladCommands.cleanupDriver(scenario);
        }
        SaladCommands.clearScenarioVariables();
        HibernateUtils.closeSession();
    }
}