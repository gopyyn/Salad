package com.gopyyn.salad.selenium;

import com.gopyyn.salad.core.SaladCommands;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Waits {
    private static final int FLUENT_WAIT_SECONDS = 30;
    private static final int QUIESCE_WAIT_SECONDS = 60;
    private static final int FLUENT_POLLING_IN_MS = 100;
    private static final int EXTENDED_POLLING_IN_MS = 500;

    private static final double DEFAULT_WAIT_MULTIPLIER = 1.0;

    private static final ThreadLocal<Waits> threadInstance = ThreadLocal.withInitial(Waits::new);
    private static final ThreadLocal<Double> currentWaitMultiplierInstance =
            ThreadLocal.withInitial(() -> DEFAULT_WAIT_MULTIPLIER);

    private static final Logger LOGGER = LoggerFactory.getLogger(Waits.class);

    private WebDriver webDriver;
    private JavascriptExecutor javaScriptExecutor;

    private Waits() {}

    public static Waits using(WebDriver webDriver) {
        Waits waiter = threadInstance.get();
        waiter.webDriver = webDriver;
        waiter.javaScriptExecutor = (JavascriptExecutor) waiter.webDriver;
        return waiter;
    }

    public FluentWait<WebDriver> pollingWait() {
        return pollingWait(FLUENT_WAIT_SECONDS);
    }

    public FluentWait<WebDriver> pollingWait(int seconds) {
        return new FluentWait<>(webDriver)
                .withTimeout(Duration.ofMillis(convertToRelativeWaitInMS(seconds)))
                .pollingEvery(Duration.ofMillis(getRelativeWait(EXTENDED_POLLING_IN_MS)));
    }

    private long convertToRelativeWaitInMS(int seconds) {
        return getRelativeWait(seconds * 1000);
    }
    
    private long getRelativeWait(int milliseconds) {
        return (long) (milliseconds * getCurrentWaitMultiplier());
    }

    static Double getCurrentWaitMultiplier() {
        return currentWaitMultiplierInstance.get();
    }

    public FluentWait<WebDriver> searchingWait() {
        return pollingWait().ignoring(NoSuchElementException.class);
    }
    
    public FluentWait<WebDriver> searchingWait(int seconds) {
        return pollingWait(seconds).ignoring(NoSuchElementException.class);
    }
    
    public void forPageLoad() {
        FluentWait<WebDriver> wait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofMillis(convertToRelativeWaitInMS(QUIESCE_WAIT_SECONDS)))
                .pollingEvery(Duration.ofMillis(getRelativeWait(FLUENT_POLLING_IN_MS)));

        wait.until(driver -> isPageLoaded());
    }

    private Boolean isPageLoaded() {
        String quiesceScript =
                "return document.readyState === 'complete' " +
                        "&& (!window.jQuery || (window.jQuery.active - window.Error.length) <= 0 && window.jQuery('.blockUI').length === 0)" +
                        "&& (!XMLHttpRequest.active || XMLHttpRequest.active == 0)";
        return runQuiesceScript(quiesceScript);
    }

    private Boolean runQuiesceScript(String quiesceScript) {
        try {
            return (Boolean) javaScriptExecutor.executeScript(quiesceScript);
        } catch (WebDriverException e) {
            webDriver.navigate().refresh();
            LOGGER.error("Application did not quiesce", e);
            return false;
        }
    }

    public void forDropDownToPopulate(final By findBy, int minimumOptionCount) {
        forPageLoad();
        searchingWait().until(driver -> {
            Select selectToCheck = new Select(driver.findElement(findBy));
            return selectToCheck.getOptions().size() >= minimumOptionCount;
        });
    }

    public WebElement forElement(final By findBy) {
        forPageLoad();
        return searchingWait().until(ExpectedConditions.visibilityOfElementLocated(findBy));
    }

    public WebElement forElement(WebElement element) {
        forPageLoad();
        return searchingWait().until(ExpectedConditions.visibilityOf(element));
    }

    public void implicitlyWait(int seconds) {
        try {
            searchingWait().withTimeout(Duration.ofMillis(convertToRelativeWaitInMS(seconds)))
                    .until(ExpectedConditions.elementToBeClickable(By.className("NeverShowUp")));
        } catch (TimeoutException e) {
            LOGGER.trace("Implicitly waited {} seconds", seconds);
        }
    }

    public void forTitle(String title) {
        searchingWait().until(ExpectedConditions.titleContains(title));
    }

    public Boolean forElementToNotBeDisplayed(final By findBy) {
        forPageLoad();
        return pollingWait().until(ExpectedConditions.invisibilityOfElementLocated(findBy));
    }

    public WebElement forElementToBeClickable(final By findBy) {
        forPageLoad();
        return searchingWait().until(ExpectedConditions.elementToBeClickable(findBy));
    }
}
