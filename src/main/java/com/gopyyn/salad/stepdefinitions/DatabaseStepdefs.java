package com.gopyyn.salad.stepdefinitions;

import com.gopyyn.salad.core.SaladCommands;
import io.cucumber.java.en.Given;

public class DatabaseStepdefs {

    @Given("^query \"(.+)\"$")
    public void query(String query) {
        SaladCommands.query(query);
    }

    @Given("^query")
    public void queryMultiLine(String query) {
        SaladCommands.query(query);
    }
}