package com.gopyyn.salad.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gopyyn.salad.enums.MatchType;
import com.gopyyn.salad.enums.VerifyType;
import com.gopyyn.salad.rest.RestApi;
import com.gopyyn.salad.selenium.Driver;
import com.gopyyn.salad.selenium.Waits;
import com.gopyyn.salad.utils.AlertUtils;
import com.gopyyn.salad.utils.Selector;
import com.jayway.jsonpath.JsonPath;
import com.gopyyn.salad.enums.TimeUnit;
import io.cucumber.core.exception.CucumberException;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.java.Scenario;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.gopyyn.salad.core.SaladContext.getContext;
import static com.gopyyn.salad.enums.SelectorType.TEXT;
import static com.gopyyn.salad.enums.TimeUnit.getDuration;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfAllElements;
import static org.openqa.selenium.support.ui.Sleeper.SYSTEM_SLEEPER;

public class SaladCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaladCommands.class);
    public static final String SPECIAL_CHARACTER_REGEX = "[^a-zA-Z0-9 _-]";
    public static final ObjectMapper mapper = new ObjectMapper();
    private static final int ONE_SECOND = 1;
    private static final int MIN_WAIT_SECONDS = 5;
    private static final List CLICKABLE_ELEMENTS = asList("button", "input", "select", "a", "textarea", "fieldset");
    private static final String SALAD_JS;

    static {
        SALAD_JS = getSaladJs();
    }

    private SaladCommands() {
    }

    public static void goTo(String path) {
        getDriver().get(parseString(path));
        runSaladScript();
    }

    public static RemoteWebDriver getDriver() {
        if (Driver.instance().getDriver() == null ||
                ((RemoteWebDriver) Driver.instance().getDriver()).getSessionId() == null) {
            Driver.instance().initialize();
        }

        return (RemoteWebDriver) Driver.instance().getDriver();
    }

    public static void verifyDisplayed(String name) {
        assertThat(getElement(name).isDisplayed()).isTrue();
    }

    public static String getElementValue(String name) {
        WebElement element = getElement(name);
        if ("input".equalsIgnoreCase(element.getTagName())) {
            return element.getAttribute("value");
        }
        if ("select".equalsIgnoreCase(element.getTagName())) {
            return new Select(element).getFirstSelectedOption().getText();
        }
        return element.getText();
    }

    private static WebElement getVisibleElement(String name) {
        List<WebElement> elements = getElements(name);
        return elements.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElse(null);
    }

    public static WebElement getElement(String name) {
        return getElement(getSelector(name));
    }

    private static WebElement getElement(Selector selector) {
        return getElement(selector.getBy());
    }

    private static WebElement getElement(By bySelector) {
        return getDriver().findElement(bySelector);
    }

    private static List<WebElement> getElements(String name) {
        return getDriver().findElements(getSelector(name).getBy());
    }

    private static Selector getSelector(String name) {
        return new Selector(name);
    }

    public static void enter(String displayName, String value) {
        WebElement inputElement = getInputElement(displayName);
        enter(inputElement, value);
    }

    private static void enter(WebElement inputElement, String value) {
        shortWaitForElementToBeClickable(inputElement);
        if ("select".equalsIgnoreCase(inputElement.getTagName())) {
            new Select(inputElement).selectByVisibleText(parseString(value));
        } else if ("checkbox".equalsIgnoreCase(inputElement.getAttribute("type")) ||
                "radio".equalsIgnoreCase(inputElement.getAttribute("type"))) {
            inputElement.click();
        } else {
            inputElement.sendKeys(parseString(value));
            inputElement.sendKeys(Keys.TAB);
            new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(Keys.TAB).keyUp(Keys.SHIFT).perform();
        }
    }


    private static void shortWaitForElementToBeClickable(String locator) {
        Waits waits = Waits.using(getDriver());
        waits.forPageLoad();
        waits.searchingWait(MIN_WAIT_SECONDS).until(elementToBeClickable(new Selector(locator).getBy()));
    }

    private static void shortWaitForElementToBeClickable(WebElement element) {
        Waits waits = Waits.using(getDriver());
        waits.forPageLoad();
        waits.searchingWait(MIN_WAIT_SECONDS).until(elementToBeClickable(element));
    }

    public static void select(String displayName, String value) {
        WebElement dropDown = getInputElement(displayName);
        enter(dropDown, value);
    }

    private static WebElement getInputElement(String selectorText) {
        Selector selector = getSelector(selectorText);
        if (selector.getType() == TEXT) {
            return getElementWithDisplayName(selectorText);
        }

        return getElement(selector);
    }

    private static WebElement getElementWithDisplayName(String selectorText) {
        WebElement elementWithLabel = getElementWithLabel(selectorText);
        if (elementWithLabel != null) {
            return elementWithLabel;
        }

        WebElement element = getVisibleElement(selectorText);
        if (element == null) {
            throw new CucumberException("unable to locate " + selectorText);
        }

        try {
            return getChild(element);
        } catch (RuntimeException e) {
            LOGGER.debug(e, () -> "input element not available as child. Searching under sibling");
            return getSibling(element);
        }
    }

    private static WebElement getElementWithLabel(String selectorText) {
        try {
            WebElement labelElement = getElement(By.xpath(format("//label[normalize-space()='%s']", selectorText)));
            String targetElementId = labelElement.getAttribute("for");


            return getElement(By.id(targetElementId));
        } catch (Exception e) {
            LOGGER.debug(e, () -> "Unable to find element with label");
        }
        return null;
    }

    private static WebElement getSibling(WebElement element) {
        try {
            WebElement tagElement = element.findElement(By.xpath("following-sibling::input|following-sibling::select|" +
                    "following-sibling::*//input|following-sibling::*//select"));
            String type = tagElement.getAttribute("type");
            if ("checkbox".equalsIgnoreCase(type) || "radio".equalsIgnoreCase(type)) {
                return element.findElement(By.xpath("preceding-sibling::*[1]/input|preceding-sibling::*[1]/select"));
            }
            return tagElement;
        } catch (RuntimeException e) {
            LOGGER.debug(e, () -> "unable to find following checkbox");
            return element.findElement(By.xpath("preceding-sibling::input[1]|preceding-sibling::select[1]|" +
                    "preceding-sibling::*[1]//input|preceding-sibling::*[1]//select"));
        }
    }

    private static WebElement getChild(WebElement element) {
        return element.findElement(By.xpath(".//input|.//select"));
    }

    public static void inputCss(String css, String value) {
        WebElement element = getDriver().findElement(By.cssSelector(css));
        enter(element, value);
    }

    public static void safeClick(String name, int nthOccurrence) {
        try {
            click(name, nthOccurrence);
        } catch (RuntimeException e) {
            shortWaitForElementToBeClickable(name);
            click(name, nthOccurrence);
        }
    }

    private static void click(String name, int nthOccurrence) {
        List<WebElement> elements = getElements(parseString(name)).stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());

        if (elements.isEmpty() || elements.size() < nthOccurrence-1) {
            throw new CucumberException(format("Unable to find clickable %s", name));
        }

        if (nthOccurrence == 1) {
            WebElement element = elements.stream()
                    .sorted(Comparator.comparing((e) -> isClickable(e) ? 0 : 1))
                    .findFirst()
                    .orElseThrow(() -> new CucumberException(format("Unable to find clickable %s", name)));
            element.click();
        } else {
            elements.get(nthOccurrence-1).click();
        }

        waitUntil(VerifyType.PAGELOAD);
    }

    private static boolean isClickable(WebElement element) {
        return CLICKABLE_ELEMENTS.contains(element.getTagName());
    }

    private static String getSaladJs() {
        try {
            InputStream inputStream = SaladContext.class.getClassLoader()
                    .getResourceAsStream("js/salad.js");
            return new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (RuntimeException e) {
            LOGGER.error(e, () -> "unable to run the javascript file");
            return null;
        }
    }

     public static Boolean runSaladScript() {
        if (SALAD_JS == null) {
            return false;
        }

         return runScript(SALAD_JS);
     }

    public static Boolean runScript(String js) {
        try {
            return (Boolean) getDriver().executeScript(js);
        } catch (WebDriverException e) {
            LOGGER.error(e, () -> "unable to run ");
            return false;
        }
    }

    public static void wait(long waitTime, TimeUnit unit) throws InterruptedException {
        SYSTEM_SLEEPER.sleep(getDuration(waitTime, unit));
    }

    public static void waitUntil(String text, VerifyType type) {
        Waits wait = Waits.using(getDriver());
        Selector selector = getSelector(text);
        if (selector.getType() == TEXT) {
            MatchType matchType = type == VerifyType.VISIBLE ? MatchType.ANY : MatchType.ALL;
            waitUntilForElements(text, matchType, type);
            return;
        }
        switch (type) {
            case VISIBLE:
                wait.forElement(selector.getBy());
                break;
            case INVISIBLE:
                wait.forElementToNotBeDisplayed(selector.getBy());
                break;
            case ENABLED:
            case CLICKABLE:
                wait.forElementToBeClickable(selector.getBy());
                break;
            case NOT_CLICKABLE:
            case DISABLED:
                wait.forElementToBeClickable(selector.getBy());
                break;
            default:
                throw new CucumberException(format("Undefined functionality for waitUntilType %s", type));
        }
    }

    public static void waitUntilForElements(String text, MatchType matchType, VerifyType type) {
        final String parseText = parseString(text);
        Waits wait = Waits.using(getDriver());
        switch (type) {
            case VISIBLE:
                waitUntilVisible(matchType, parseText, wait);
                break;
            case INVISIBLE:
                waitUntilInvisible(matchType, parseText, wait);
                break;
            case ENABLED:
            case CLICKABLE:
                waitUntilEnabled(matchType, parseText, wait);
                break;
            case DISABLED:
            case NOT_CLICKABLE:
                waitUntilDisabled(matchType, parseText, wait);
                break;
            default:
                throw new CucumberException(format("Undefined functionality for waitUntilType %s", type));
        }
    }

    private static void waitUntilInvisible(MatchType matchType, String parseText, Waits wait) {
        if (matchType == MatchType.ALL) {
            wait.searchingWait().until(invisibilityOfAllElements(getElements(parseText)));
        } else {
            wait.searchingWait().until(webDriver -> getElements(parseText).stream().anyMatch(element -> !element.isDisplayed())); 
        }
    }

    private static void waitUntilEnabled(MatchType matchType, String parseText, Waits wait) {
        if (matchType == MatchType.ALL) {
            wait.searchingWait().until(webDriver -> getElements(parseText).stream().allMatch(element -> element.isEnabled())); 
        } else {
            wait.searchingWait().until(webDriver -> getElements(parseText).stream().anyMatch(element -> element.isEnabled())); 
        }
    }

    private static void waitUntilDisabled(MatchType matchType, String parseText, Waits wait) {
        if (matchType == MatchType.ALL) {
            wait.searchingWait().until(webDriver -> getElements(parseText).stream().allMatch(element -> element.isEnabled())); 
        } else {
            wait.searchingWait().until(webDriver -> getElements(parseText).stream().anyMatch(element -> element.isEnabled())); 
        }
    }

    private static void waitUntilVisible(MatchType matchType, String parseText, Waits wait) {
        if (matchType == MatchType.ANY) {
            wait.searchingWait().until(webDriver -> getElements(parseText).stream().anyMatch(WebElement::isDisplayed)); 
        } else {
            wait.searchingWait().until(webDriver -> getElements(parseText).stream().allMatch(WebElement::isDisplayed)); 
        }
    }

    public static void waitUntil(VerifyType type) {
        Waits wait = Waits.using(getDriver());
        if (type == VerifyType.PAGELOAD) {
            try {
                SYSTEM_SLEEPER.sleep(Duration.ofSeconds(ONE_SECOND));
            } catch (InterruptedException e) {
                LOGGER.error(e, ()->"sleep Interrupted");
            }
            wait.forPageLoad();
        }
    }

    public static void assertPageUrl(String url) {
        assertThat(getDriver().getCurrentUrl()).contains(parseString(url));
    }

    public static void set(String name, String value) {
        getContext().getScenarioVariables().put(name, parse(value));
    }

    public static void setValue(String name, String value) {
        getContext().getScenarioVariables().put(name, getElementValue(value));
    }

    public static void def(String name, String value) {
        getContext().getFeatureVariables().put(name, parse(value));
    }

    public static void hoverAndClick(String hoverLinkName, String clickLinkName) {
        hoverAndClick(By.xpath(format("//a[normalize-space()='%s']", hoverLinkName)),
                By.xpath(format("//a[normalize-space()='%s']", clickLinkName)),
                false);
    }

    public static void hoverAndClick(By tabLocator, By linkLocator, boolean waitBeforeClick) {
        hover(tabLocator);
        if (waitBeforeClick) {
            Waits.using(getDriver()).implicitlyWait(2);
        }
        WebElement targetLink = Waits.using(getDriver()).forElementToBeClickable(linkLocator);
        targetLink.click();
    }

    public static WebElement hover(By locator) {
        WebDriver driver = Driver.instance().getDriver();
        WebElement element = driver.findElement(locator);
        new Actions(driver).moveToElement(element).perform();
        return element;
    }

    public static void print(String exp) {
        StringBuilder sb = new StringBuilder();
        sb.append("[print] ");
        sb.append(parseString(exp));
        LOGGER.info(() -> sb.toString());
    }

    public static String parseString(String exp) {
        Object parsedValue = parse(exp);
        if ("null".equals(parsedValue) || parsedValue == null) {
            return null;
        }

        return parsedValue.toString();
    }

    public static Object parse(String exp) {
        exp = removeEnclosingQuotes(exp);
        exp = replaceSaladRandomNotation(exp);
        if (exp.contains("${")) {
            String[] variables = substringsBetween(exp, "${", "}");
            for (String variable : variables) {
                Object expValue = resolveVariable(variable);
                exp = replaceOnce(exp, "${" + variable + "}", expValue.toString());
            }
            parse(exp);
        }

        if (exp.startsWith("Java.type(")) {
            return eval(exp);
        }

        if (exp.startsWith("eval ")) {
            return eval(substringAfter(exp, "eval "));
        }
        return exp;
    }

    private static String removeEnclosingQuotes(String exp) {
        if (exp.startsWith("\"")) {
            return StringUtils.removePattern(exp, "^\"|\"$");
        }
        return exp;
    }

    private static Object resolveVariable(String variable) {
        String effectiveVariable = substringBefore(variable, ".");
        String pathExpression = substringAfter(variable, ".");
        Object expValue = ofNullable(getContext().getVariableIfPresent(effectiveVariable))
                .orElse(variable);

        if (isJson(expValue) && isNotEmpty(pathExpression)) {
            return convertToJsonString(JsonPath.read((String) expValue, pathExpression));
        } else if (!(expValue instanceof String)) {
            return eval(variable);
        }

        return expValue;
    }

    //This method will Replace {ssn: ##numeric(8), name: ##string} with {ssn: ${random.numeric(8)}, name: ${random.string()}}
    private static String replaceSaladRandomNotation(String exp) {
        return replacePattern(exp, "##(\\w+\\(\\d+\\))", "\\${random.$1}")
                .replaceAll("##(\\w+)", "\\${random.$1()}");
    }

    public static Object eval(String exp) {
        return evalInNashorn(exp);
    }

    public static Object evalInNashorn(String exp) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine nashorn = manager.getEngineByName("nashorn");

        Bindings bindings = nashorn.getBindings(100); 
        Map<String, Object> map = getContext().getAllVariable();
        map.forEach(bindings::put);

        try {
            return nashorn.eval(exp);
        } catch (ScriptException e) {
            throw new CucumberException("javascript evaluation failed: " + exp, e);
        }
    }

    public static final boolean isJson(Object o) {
        if (o instanceof String) {
            String str = o.toString().trim();
            if (str.length() <= 1) {
                return false;
            }
            char startChar = str.charAt(0);
            char endChar = str.charAt(str.length() - 1);
            if ((startChar != '[' || endChar != ']') && (startChar != '{' || endChar != '}')) {
                return false;
            }

            try {
                (new JSONParser(-1)).parse(str);
                return true;
            } catch (ParseException e) {
                LOGGER.debug(e, () -> "unable to parse json");
                return false;
            }
        }
        return false;
    }

    public static boolean isXml(String text) {
        return text != null && text.startsWith("<");
    }

    public static void match(String lhs, MatchType operation, String rhs) {
        lhs = parseString(lhs);
        rhs = parseString(rhs);
        switch (operation) {
            case EQUALS:
                assertThat(lhs).isEqualTo(rhs);
                break;
            case NOT_EQUALS:
                assertThat(lhs).isNotEqualTo(rhs);
                break;
            case GREATER_THAN:
                assertThat(toNumber(lhs)).isGreaterThan(toNumber(rhs));
                break;
            case LESS_THAN:
                assertThat(toNumber(lhs)).isLessThan(toNumber(rhs));
                break;
            case GREATER_THAN_OR_EQUAL_TO:
                assertThat(toNumber(lhs)).isGreaterThanOrEqualTo(toNumber(rhs));
                break;
            case LESS_THAN_OR_EQUAL_TO:
                assertThat(toNumber(lhs)).isLessThanOrEqualTo(toNumber(rhs));
                break;
            case CONTAINS:
                assertThat(lhs).contains(rhs);
                break;
            case NOT_CONTAINS:
                assertThat(lhs).doesNotContain(rhs);
                break;
            default:
                throw new CucumberException(format("unable to match %s %s %s", lhs, operation, rhs));
        }
    }

    private static BigDecimal toNumber(String number) {
        try {
            return new BigDecimal(number);
        } catch (RuntimeException e) {
            throw new CucumberException(format("Invalid number %s", number), e);
        }
    }

    public static void verifyElement(String expression, VerifyType type) {
        switch (type) {
            case ENABLED:
                assertThat(getElement(expression).isEnabled()).isTrue();
                break;
            case DISABLED:
                assertThat(getElement(expression).isEnabled()).isFalse();
                break;
            case VISIBLE:
                assertThat(!isHidden(expression)).isTrue();
                break;
            case INVISIBLE:
                assertThat(isHidden(expression)).isTrue();
                break;
            default:
                throw new CucumberException(format("unable to verify %s %s", expression, type.name()));
        }
    }

    public static Boolean isHidden(String expression) {
        try {
            return !getElement(expression).isDisplayed();
        } catch(WebDriverException e) {
            LOGGER.debug(e, () -> "Error while waiting for Element not to be displayed");
            return true;
        }
    }

    public static String getVariableAsStringIfPresent(String name) {
        return getContext().getVariableAsStringIfPresent(name);
    }

    public static String getVariableAsString(String name) {
        return getContext().getVariableAsString(name);
    }

    public static Object getVariableIfPresent(String name) {
        return getContext().getVariableIfPresent(name);
    }

    public static void clearScenarioVariables() {
        getContext().getScenarioVariables().clear();
    }

    public static String getCurrentFeature() {
        return getContext().getCurrentFeature();
    }

    public static void setCurrentFeature(String featureUri) {
        getContext().setCurrentFeature(featureUri);
    }

    public static void clearFeatureVariables() {
        getContext().getFeatureVariables().clear();
    }

    public static String convertToJsonString(Object object) {
        try {
            if (object instanceof String) {
                return (String) object;
            }
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.debug(e, () -> "unable to convert object to string");
        }
        return "{}";
    }

    public static void cleanupDriver(Scenario scenario) {
        if (Driver.instance().getDriver() == null) {
            return;
        }

        takeSnapShot(scenario);
        Driver.instance().cleanup();
    }

    public static void takeSnapShot(Scenario scenario) {
        if (scenario.isFailed()) {
            takeScreenshot(scenario.getName().replaceAll("[^a-zA-Z0-9]", ""));
        }
    }

    public static void takeScreenshot(String methodName) {
        try {
            TakesScreenshot screenshotTaker = ((TakesScreenshot) Driver.instance().getDriver());
            File sourceFile = screenshotTaker.getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(sourceFile, new File("target/" + methodName + ".png"));
        } catch (IOException e) {
            LOGGER.error(e, () -> "Unable to take screen shot");
        }
    }

    public static List<Map<String, Object>> query(String query) {
        return DatabaseContext.execute(parseString(query));
    }

    public static String toDbUUID(String uuid){
        return uuid.replace("-", "").toUpperCase();
    }

    public static void fillPage(String data) {
        try {
            String content = parseString(readDataFromFile(data));
            Map<String, String> map = mapper.readValue(content, Map.class);
            map.forEach(SaladCommands::enter);
        } catch (IOException e) {
            LOGGER.debug(e, () -> format("unable to parse json %s", data));
            throw new CucumberException(format("unable to parse json %s", data));
        }
    }

    public static String readDataFromFile(String body) {
        if (isEmpty(body)) {
            return "";
        }

        if (body.startsWith("resources:")) {
            final String fileName = substringAfter(body, "resources:");
            try {
                return FileUtils.readFileToString(new File(RestApi.class.getClassLoader().getResource(fileName).toURI()));
            } catch (URISyntaxException |IOException e) {
                LOGGER.debug(e, () -> "unable to load resource");
                throw new CucumberException(format("unable to load resource %s", fileName));
            }
        } else if (body.startsWith("file:")) {
            final String fileName = substringAfter(body, "file:");
            try {
                return FileUtils.readFileToString(new File(fileName));
            } catch (IOException e) {
                LOGGER.debug(e, () -> "unable to load file");
                throw new CucumberException(format("unable to load file %s", fileName));
            }
        }
        return body;
    }

    public static void alert(String action) {
        AlertUtils.alert(action);
    }

    public static void switchWindow() {
        String currentWindow = getDriver().getWindowHandle();
        String windowHandle = getDriver().getWindowHandles().stream()
                .filter(window -> !window.equals(currentWindow))
                .findFirst()
                .orElseThrow(() -> new CucumberException("No other window to switch"));

        getDriver().switchTo().window(windowHandle);
    }

    public static void switchWindow(String windowHandle) {
        getDriver().switchTo().window(windowHandle);
    }

}
