package com.salad.stepdefinitions;

import com.salad.core.SaladCommands;
import io.cucumber.java.en.Given;

public class DatabaseStepdefs {

    @Given("^query \"(.+)\"$")
    public void query(String query) {
        SaladCommands.query(query);
    }
}