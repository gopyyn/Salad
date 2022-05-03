package com.gopyyn.salad.selenium;

import com.gopyyn.salad.core.SaladContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Configurable through the following system configProperties:
 * 
 * salad.browser - Browser to use for the test selenium.remote - If true, direct tests to the
 * selenium.host - selenium grid Host name of the selenium grid hub
 * selenium.port - selenium grid Port the selenium grid hub is running on
 *
 */
public abstract class SeleniumSettings {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumSettings.class);
	public static final String MALFORMED_URL_EXCEPTION = "Malformed URL Exception";

	private SeleniumSettings(){
	}

	public static String browserName() {
		return SaladContext.getContext().getVariable("salad.browser", "chrome");
	}

	public static Boolean isRemote() {
		return Boolean.valueOf(SaladContext.getContext().getVariable("selenium.remote", "false"));
	}

	public static String gridHost() {
		return SaladContext.getContext().getVariable("selenium.host", "localhost");
	}

	public static Integer gridPort() {
		return Integer.valueOf(SaladContext.getContext().getVariable("selenium.port", "4444"));
	}

	public static URL gridUrl() {
		try {
			return new URL("http://" + gridHost() + ":" + gridPort() + "/wd/hub");
		} catch (MalformedURLException e) {
			LOGGER.error(MALFORMED_URL_EXCEPTION, e);
			return null;
		}
	}
}
