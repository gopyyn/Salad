package com.salad.stepdefinitions;

import com.salad.core.SaladCommands;
import cucumber.api.java.en.Given;

public class DatabaseStepdefs {

    @Given("query \"(.+)\"")
    public void query(String query) {
        SaladCommands.query(query);
    }
}