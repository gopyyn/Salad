package com.salad.core;


import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = {"src/test/resources/ui.feature"}, tags = "@compile")
public class SaladUIFeatureTest extends CucumberSaladTestng {
}