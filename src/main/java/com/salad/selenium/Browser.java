package com.salad.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public enum Browser {

    INTERNETEXPLORER(InternetExplorerDriver.class, new InternetExplorerOptions()),
    FIREFOX(FirefoxDriver.class, new FirefoxOptions()),
    CHROME(ChromeDriver.class, new ChromeOptions()),
    HEADLESS(ChromeDriver.class, new ChromeOptions().addArguments("headless")),
    PHANTOM(PhantomJSDriver.class, new DesiredCapabilities()),
    SAFARI(SafariDriver.class, new SafariOptions());

    private static final Logger LOGGER = LoggerFactory.getLogger(Browser.class);
    private final Class<? extends WebDriver> webDriverClass;
    private final MutableCapabilities capabilities;

    Browser(Class<? extends WebDriver> webDriverClass, MutableCapabilities capabilities) {
        this.webDriverClass = webDriverClass;
        capabilities.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);

        if(webDriverClass == InternetExplorerDriver.class) {
        	capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);  
        	capabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "about:blank"); 
        	capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        }

        capabilities.setCapability(CapabilityType.APPLICATION_NAME, "salad");

        this.capabilities = capabilities;
    }

    public static Browser detect() {
        String browserName = SeleniumSettings.browserName();
        for (Browser browser : values()) {
            if (browser.name().equalsIgnoreCase(browserName)) {
                return browser;
            }
        }
        throw new IllegalArgumentException("Can't find browser: " + browserName);
    }

    /** @return the WebDriver for the browser */
    public WebDriver instantiate() {
        Boolean isRemote = SeleniumSettings.isRemote();
        if (isRemote) {
            return instantiateRemote();
        } else {
            return instantiateLocal();
        }
    }

    private WebDriver instantiateLocal() {
        try {
            switch (this) {
                case INTERNETEXPLORER:
                    return new InternetExplorerDriver((InternetExplorerOptions) capabilities);
                case CHROME:
                    return new ChromeDriver((ChromeOptions) capabilities);
                case HEADLESS:
                    return new ChromeDriver((ChromeOptions) capabilities);
                case PHANTOM:
                    WebDriverManager.phantomjs().setup();
                    return new PhantomJSDriver();
                default:
                    return webDriverClass.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Can not create a webdriver.", e);
            return null;
        }
    }

    private WebDriver instantiateRemote() {
        URL gridUrl = SeleniumSettings.gridUrl();
        overrideUserAgent();
        WebDriver driver = new RemoteWebDriver(gridUrl, capabilities);
        driver = new Augmenter().augment(driver);
        ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        ((RemoteWebDriver)driver).setFileDetector(new LocalFileDetector());
        return driver;
    }

    private void overrideUserAgent() {
        String userAgent = System.getProperty("selenium.userAgent");
        if (userAgent != null) {
            switch (this) {
                case FIREFOX:
                    FirefoxProfile profile = new FirefoxProfile();
                    profile.setPreference("general.useragent.override", userAgent);
                    capabilities.setCapability(FirefoxDriver.PROFILE, profile);
                    break;
                case CHROME:
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments(String.format("user-agent=%s", userAgent));
                    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                    break;
                default:
                    break;
            }
        }
    }
}
