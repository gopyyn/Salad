package com.salad.core;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"html:target/cucumber-reports/cucumber-html-report",
        "json:target/cucumber-reports/cucumber.json",
        "pretty:target/cucumber-reports/cucumber-pretty.txt",
        "junit:target/cucumber-reports/cucumber-results.xml"},
        glue = {"com.salad.stepdefinitions"})
public class CucumberSaladJunit {
}