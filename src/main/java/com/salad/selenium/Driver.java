package com.salad.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Driver {
    private static final ThreadLocal<Driver> threadInstance = ThreadLocal.withInitial(Driver::new);
    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);

    private WebDriver webDriver = null;


    private Driver() {}

    public static Driver instance() {
        return threadInstance.get();
    }

    public void initialize() {
        this.webDriver = Browser.detect().instantiate();
        this.webDriver.manage().window().maximize();
    }

    public void cleanup() {
        if (webDriver != null) {
            if (!webDriver.getClass().equals(InternetExplorerDriver.class)) {
                this.webDriver.manage().deleteAllCookies();
            }
            this.webDriver.quit();
            this.webDriver = null;
        }
    }

    public WebDriver getDriver() {
        return this.webDriver;
    }
}
