package com.gopyyn.salad.core;


import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = {"src/test/resources/core.feature"}, tags = "@compile")
public class SaladCoreFeatureTestNGTest extends SaladTestng {
}