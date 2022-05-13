package com.gopyyn.salad.core;


import io.cucumber.junit.CucumberOptions;

@CucumberOptions(features = {"src/test/resources/ui.feature"}, tags = "@compile")
public class SaladUIFeatureTest extends SaladJunit {
}