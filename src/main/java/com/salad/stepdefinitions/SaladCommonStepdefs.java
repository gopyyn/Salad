package com.salad.stepdefinitions;

import com.salad.core.SaladCommands;
import com.salad.selenium.Driver;
import com.salad.utils.HibernateUtils;
import com.salad.enums.MatchType;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

import java.util.Hashtable;

import static com.salad.utils.HibernateUtils.closeSession;

public class SaladCommonStepdefs {
    private static Hashtable<String, Driver> activeDrivers = new Hashtable<>(); //NOSONAR [squid:S1149] shared by multiple threads

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            activeDrivers.forEach(((key, driver)-> driver.cleanup()));
            HibernateUtils.shutdown();
        }));
    }

    @Given("def (.+) = \"(.+)\"")
    public void defineFeatureVar(String name, String value) {
        SaladCommands.def(name.replaceAll("['\"]", ""), value);
    }

    @Given("set (.+) = \"(.+)\"")
    public void defineScenarioVar(String name, String value) {
        SaladCommands.set(name.replaceAll("['\"]", ""), value);
    }

    @Given("print (.+)")
    public void print(String value) {
        SaladCommands.print(value);
    }

    @When("^match (\"(.+)\"|(.+)) ([a-zA-Z0-9_=!<>]+) (\"(.+)\"|(.+))")
    public void match(String expression, String operators, String rhs) {
        SaladCommands.match(expression, MatchType.fromValue(operators), rhs);
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        beforeFeature(scenario);
    }

    private void beforeFeature(Scenario scenario) {
        if (scenario.getUri().equals(SaladCommands.getCurrentFeature())) {
            return;
        }
        SaladCommands.setCurrentFeature(scenario.getUri());
        //clears previous feature variables
        SaladCommands.clearFeatureVariables();
        SaladCommands.cleanupDriver(scenario);
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (Boolean.valueOf(SaladCommands.getVariableAsStringIfPresent("skip_browser_restart"))) {
            SaladCommands.takeSnapShot(scenario);
            activeDrivers.putIfAbsent(scenario.getUri(), Driver.instance());
        } else {
            SaladCommands.cleanupDriver(scenario);
        }
        SaladCommands.clearScenarioVariables();
        closeSession();
    }
}