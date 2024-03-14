package com.gopyyn.salad.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Locale;

public enum Browser {

    INTERNETEXPLORER(InternetExplorerDriver.class, new InternetExplorerOptions()),
    FIREFOX(FirefoxDriver.class, new FirefoxOptions()),
    CHROME(ChromeDriver.class, new ChromeOptions()),
    HEADLESS(ChromeDriver.class, new ChromeOptions().addArguments("--window-size=1400,600", "--headless=new")),
    EDGE(EdgeDriver.class, new EdgeOptions()),
    SAFARI(SafariDriver.class, new SafariOptions());

    private static final Logger LOGGER = LoggerFactory.getLogger(Browser.class);
    private final Class<? extends WebDriver> webDriverClass;
    private final MutableCapabilities capabilities;

    Browser(Class<? extends WebDriver> webDriverClass, MutableCapabilities capabilities) {
        this.webDriverClass = webDriverClass;

        if(this.webDriverClass == InternetExplorerDriver.class) {
        	capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);  
        	capabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "about:blank"); 
        	capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        }

        this.capabilities = capabilities;
    }

    public static Browser detect() {
        String browserName = SeleniumSettings.browserName();
        for (Browser browser : values()) {
            if (browser.name().equalsIgnoreCase(browserName)) {
                return browser;
            }
        }
        LOGGER.info("No valid browser defined. Trying to initialize default browser");
        String osName = System.getProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH);
        if (osName.contains("windows")) {
            return EDGE;
        } else if (osName.contains("mac")) {
            return SAFARI;
        } else if (osName.contains("nix") || osName.contains("nux")
                || osName.contains("aix")) {
            return FIREFOX;
        }

        return HEADLESS;
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
        switch (this) {
            case INTERNETEXPLORER:
                WebDriverManager.iedriver().setup();
                return new InternetExplorerDriver((InternetExplorerOptions) capabilities);
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver((FirefoxOptions) capabilities);
            case SAFARI:
                WebDriverManager.safaridriver().setup();
                return new SafariDriver((SafariOptions) capabilities);
            case EDGE:
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver((EdgeOptions) capabilities);
            case CHROME:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = (ChromeOptions) capabilities;
                options.addArguments("--remote-allow-origins=*");
                return new ChromeDriver(options);
            case HEADLESS:
            default:
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver((ChromeOptions) HEADLESS.capabilities);
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
