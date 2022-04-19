package com.salad.core;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        plugin = {"html:target/cucumber-reports/cucumber-html-report.html",
                "json:target/cucumber-reports/cucumber.json",
                "pretty:target/cucumber-reports/cucumber-pretty.txt",
                "junit:target/cucumber-reports/cucumber-results.xml"},
        glue = "com.salad.stepdefinitions"
)
public class CucumberSaladTestng extends AbstractTestNGCucumberTests {
}