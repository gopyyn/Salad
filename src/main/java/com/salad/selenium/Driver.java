package com.salad.selenium;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {
    private static final ThreadLocal<Driver> threadInstance = ThreadLocal.withInitial(Driver::new);
    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);

    private WebDriver webDriver = null;


    private Driver() {}

    public static Driver instance() {
        return threadInstance.get();
    }

    public void initialize() {
        if (System.getProperty("os.name").contains("Windows 10")) {
            try {
                runSeleniumDriverCleanup();
            } catch (IOException ex) {
                LOGGER.warn("I/O error occured during attempted driver cleanup.", ex);
            }

            Path path = Paths.get(System.getenv("TEMP"));
            clearTempFileDirectories(path, "scoped*");
            clearTempFileDirectories(path, "anonymous*");
        }

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

    private void runSeleniumDriverCleanup() throws IOException {
        StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append("$Drivers = @(\\\"chromedriver\\\", \\\"IEDriverServer\\\")" + System.lineSeparator());
        scriptBuilder.append("$Minutes = 30" + System.lineSeparator());
        scriptBuilder.append("$Seconds = $Minutes * 60" + System.lineSeparator());
        scriptBuilder
                .append("Write-Host \\\"Killing Selenium driver processes open longer than $Minutes minutes...\\\"" +
                        System.lineSeparator());
        scriptBuilder.append("foreach ($Driver in $Drivers) {" + System.lineSeparator());
        scriptBuilder.append("    Get-Process $Driver -ErrorAction 'SilentlyContinue' " +
                "| ? { ([DateTime]::Now - $_.StartTime).TotalSeconds -gt $Seconds } | Stop-Process" +
                System.lineSeparator());
        scriptBuilder.append("}" + System.lineSeparator());
        scriptBuilder.append("Write-Host \\\"...done.\\\"");

        Process scriptProcess = new ProcessBuilder("powershell.exe", scriptBuilder.toString()).start();
        LOGGER.info("Selenium driver process cleanup script output follows...");
        logScriptOutput(scriptProcess, true);
        logScriptOutput(scriptProcess, false);
        LOGGER.info("Selenium driver process cleanup script complete.\n");
    }

    private void logScriptOutput(Process process, boolean forStandardOutput) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(forStandardOutput ? process.getInputStream() : process.getErrorStream()))) {
            String output = reader.readLine();

            if (output == null) {
                LOGGER.info("No {} output generated.", forStandardOutput ? "standard" : "error");
            } else {
                String logMessage = String.format("%s output:", forStandardOutput ? "Standard" : "Error");
                StringBuilder logBuilder = new StringBuilder(logMessage);
                do {
                    logBuilder.append(System.lineSeparator() + output);
                    output = reader.readLine();
                } while (output != null);

                logMessage = logBuilder.toString();
                if (forStandardOutput) {
                    LOGGER.info(logMessage);
                } else {
                    LOGGER.warn(logMessage);
                }
            }
        }
    }

    private void clearTempFileDirectories(Path path, String directoryPattern) {
        LOGGER.info("Deleting temp file directories at {}{}{}...", path, File.separator, directoryPattern);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, directoryPattern)) {
            directoryStream.forEach(directoryPath -> {
                try {
                    FileUtils.deleteDirectory(directoryPath.toFile());
                } catch (IOException|IllegalArgumentException ex) {
                    LOGGER.info("Could not delete {} (driver & browser processes may still be running)", directoryPath);
                    LOGGER.trace("Note the following exception: ", ex);
                }
            });
        } catch (IOException ex) {
            LOGGER.warn("Could not get the list of temp file directories to delete for {}{}{}.", path, File.separator,
                    directoryPattern, ex);
        }

        LOGGER.info("...done.\n");
    }
}
