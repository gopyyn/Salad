package com.gopyyn.salad.core;


import io.cucumber.junit.CucumberOptions;

@CucumberOptions(features = {"src/test/resources/core.feature"}, tags = "@compile")
public class SaladCoreFeatureTest extends SaladJunit {
}