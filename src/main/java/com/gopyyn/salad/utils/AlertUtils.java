package com.gopyyn.salad.utils;

import com.gopyyn.salad.core.SaladCommands;
import io.cucumber.core.exception.CucumberException;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Arrays;

import static java.lang.String.format;

public class AlertUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertUtils.class);

    public static void alert(String actionText) {
        Actions action = Actions.fromValue(actionText);
        try {
            RemoteWebDriver driver = SaladCommands.getDriver();

            Alert alert = driver.switchTo().alert();

            switch (action) {
                case DISMISS:
                    alert.dismiss();
                    break;
                case SEND_TEXT:
                    alert.sendKeys("salad is healthy");
                    break;
                case ACCEPT:
                default:
                    alert.accept();
            }
        } catch(NoAlertPresentException ex){
            LOGGER.debug(() -> "No alert present"); //NOSONAR consume the exception
        }
    }


    public enum Actions {
        ACCEPT("accept"),
        DISMISS("dismiss"),
        SEND_TEXT("send text");

        private final String actionText;

        Actions(String actionText) {
            this.actionText = actionText;
        }

        public static Actions fromValue(String actionText) {
            return Arrays.stream(values())
                    .filter((action) -> action.name().equalsIgnoreCase(actionText) ||
                                        action.actionText.equalsIgnoreCase(actionText))
                    .findFirst()
                    .orElseThrow(() -> new CucumberException(format("No alert action defined for %s", actionText)));
        }
    }
}
