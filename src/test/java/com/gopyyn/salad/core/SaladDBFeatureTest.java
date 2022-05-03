package com.gopyyn.salad.core;


import io.cucumber.junit.CucumberOptions;

@CucumberOptions(features = {"src/test/resources/db.feature"}, tags = "@compile")
public class SaladDBFeatureTest extends SaladJunit {
}