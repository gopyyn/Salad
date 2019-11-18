package com.salad.core;

import cucumber.api.CucumberOptions;

@CucumberOptions(features = {"src/test/resources/ui.feature"}, tags = "@compile")
public class SaladUIFeatureTest extends CucumberSaladJunit {
}