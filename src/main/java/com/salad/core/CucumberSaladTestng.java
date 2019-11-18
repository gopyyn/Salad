package com.salad.core;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.salad.core.SaladCommands.SPECIAL_CHARACTER_REGEX;
import static org.apache.commons.lang3.StringUtils.removePattern;

@CucumberOptions(
        plugin = {"html:target/cucumber-reports/cucumber-html-report",
                "json:target/cucumber-reports/cucumber.json",
                "pretty:target/cucumber-reports/cucumber-pretty.txt",
                "junit:target/cucumber-reports/cucumber-results.xml"},
        strict = true,
        glue = "com.salad.stepdefinitions"
)
public class CucumberSaladTestng implements ITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CucumberSaladTestng.class);
    protected TestNGCucumberRunner testNGCucumberRunner;
    private ThreadLocal<String> testName = new ThreadLocal<>();

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method, Object[] testData, ITestContext ctx){
        String scenarioName = testData[0].toString();
        String value = method.getName() + "_" + removePattern(scenarioName, SPECIAL_CHARACTER_REGEX);
        testName.set(value);
        ctx.setAttribute("testName", testName.get());
    }

    @Test(groups = {"cucumber", "regression", "smoke", "sanity"}, description = "Runs Cucumber Feature", dataProvider = "scenarios")
    public void scenario(PickleEventWrapper pickleEvent, CucumberFeatureWrapper cucumberFeature) throws Throwable {
        testNGCucumberRunner.runScenario(pickleEvent.getPickleEvent());
    }

    @DataProvider
    public Object[][] scenarios() {
        return testNGCucumberRunner.provideScenarios();
    }

    @AfterMethod(alwaysRun = true)
    public void setResultTestName(ITestResult result) {
        try {
            ITestNGMethod resultMethod = result.getMethod().clone();
            Field methodName = BaseTestMethod.class.getDeclaredField("m_methodName");
            methodName.setAccessible(true);
            methodName.set(resultMethod, getTestName());

            ((TestResult) result).setMethod(resultMethod);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LOGGER.info("Unable to update the scenario test name", e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        testNGCucumberRunner.finish();
    }

    @Override
    public String getTestName() {
        return testName.get();
    }
}