package com.salad.core;

import cucumber.api.CucumberOptions;
import org.junit.BeforeClass;

@CucumberOptions(features = {"src/test/resources/db.feature"}, tags = "@compile")
public class SaladDBFeatureTest extends CucumberSaladJunit {

    @BeforeClass
    public static void setUpOnce() {
        SaladCommands.query("CREATE TABLE PERSON(id int primary key, name varchar(255))");
        SaladCommands.query("INSERT INTO PERSON (id, name) values ('1', 'John');");
        SaladCommands.query("INSERT INTO PERSON (id, name) values ('2', 'Ahmad');");
        SaladCommands.query("INSERT INTO PERSON (id, name) values ('3', 'Ram');");
    }
}