package com.gopyyn.salad.core;


import io.cucumber.junit.CucumberOptions;

@CucumberOptions(features = {"src/test/resources/api.feature"}, tags = "@compile")
public class SaladApiFeatureTest extends SaladJunit {
}