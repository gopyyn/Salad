package com.salad.stepdefinitions;

import com.salad.rest.RestApi;
import io.cucumber.java.en.Given;

public class RestApiStepdefs {

    @Given("post \"(.+)\" \"(.+)?\"")
    public void post(String url, String body) {
        RestApi.post(url, body);
    }

    @Given("put \"(.+)\" \"(.+)?\"")
    public void put(String url, String body) {
        RestApi.put(url, body);
    }

    @Given("get \"(.+)\"")
    public void post(String url) {
        RestApi.get(url);
    }

    @Given("header \"(.+)\" \"(.+)\"")
    public void header(String name, String value) {
        RestApi.addHeader(name, value);
    }
}


