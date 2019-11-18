package com.salad.core;

import cucumber.api.CucumberOptions;

@CucumberOptions(features = {"src/test/resources/api.feature"}, tags = "@compile")
public class SaladApiFeatureTest extends CucumberSaladJunit {
}