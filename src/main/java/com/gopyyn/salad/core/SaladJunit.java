package com.gopyyn.salad.core;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"html:target/cucumber-reports/cucumber-html-report.html",
        "json:target/cucumber-reports/cucumber.json",
        "pretty:target/cucumber-reports/cucumber-pretty.txt",
        "junit:target/cucumber-reports/cucumber-results.xml"},
        glue = {"com.gopyyn.salad.stepdefinitions"})
public class SaladJunit {
}