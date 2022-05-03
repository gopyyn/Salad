package com.gopyyn.salad.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public class SaladContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaladContext.class);
    public static final int INITIAL_MAP_CAPACITY = 50;

    private static ThreadLocal<SaladContext> context = ThreadLocal.withInitial(SaladContext::new);
    private Map<String, Object> featureVariables = new HashMap(INITIAL_MAP_CAPACITY);
    private Map<String, Object> scenarioVariables = new HashMap<>(INITIAL_MAP_CAPACITY);
    private static Map<String, Object> globalVariables = new HashMap<>(INITIAL_MAP_CAPACITY);
    private String currentFeature;
    public static Map<String, Object> configProperties = new HashMap<>();

    static {
        readProperties();
        loadSystemProperties();
        loadDefaultJavaHelpers();
    }

    private SaladContext(){
    }

    private static void readProperties() {
        String propertyFileName = getEnvironment().toLowerCase() + ".yaml";
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            File file = retrieveFile(propertyFileName);
            configProperties = mapper.readValue(file, Map.class);

        } catch (Exception e) {
            LOGGER.error(e, () -> format("unable to load configProperties from %s", propertyFileName));
        }
    }

    private static void loadDefaultJavaHelpers() {
        globalVariables.put("date", SaladCommands.evalInNashorn("Java.type('java.time.LocalDate')"));
        globalVariables.put("dateTime", SaladCommands.evalInNashorn("Java.type('java.time.LocalDateTime')"));
        globalVariables.put("string", SaladCommands.evalInNashorn("Java.type('org.apache.commons.lang3.StringUtils')"));
        globalVariables.put("random", SaladCommands.evalInNashorn("Java.type('com.gopyyn.salad.utils.RandomUtils')"));
        globalVariables.put("salad", SaladCommands.evalInNashorn("Java.type('com.gopyyn.salad.core.SaladCommands')"));
    }

    private static void loadSystemProperties() {
        ofNullable(configProperties.get("system")).ifPresent((obj) -> {
            if (obj instanceof Map) {
                ((Map<String, String>) obj).entrySet().forEach((Map.Entry<String, String> entry) -> System.setProperty(entry.getKey(), entry.getValue()));
            }
        });
        Properties properties = System.getProperties();
        properties.stringPropertyNames().forEach(name -> globalVariables.put(name, properties.getProperty(name)));
    }


    private static String getEnvironment() {
        return System.getProperty("environment", "qa");
    }

    private static File retrieveFile(String fileName) {
        String extension = StringUtils.substringAfterLast(fileName, ".");
        Collection<File> files = FileUtils.listFiles(new File("."), new String[]{extension}, true);
        return files.stream()
                .filter(f -> f.getName().equalsIgnoreCase(fileName))
                .findFirst()
                .orElse(null);
    }

    public static SaladContext getContext() {
        return context.get();
    }

    Map<String, Object> getGlobalVariables() {
        return globalVariables;
    }

    Map<String, Object> getFeatureVariables() {
        return featureVariables;
    }

    Map<String, Object> getScenarioVariables() {
        return scenarioVariables;
    }

    Map<String, Object> getAllVariable() {
        Map<String, Object> allVariables = new HashMap<>();
        allVariables.putAll(scenarioVariables);
        allVariables.putAll(featureVariables);
        allVariables.putAll(globalVariables);

        return allVariables;
    }

    String getVariableAsString(String name) {
        return ofNullable(getVariableAsStringIfPresent(name))
                .orElseThrow(()->new NoSuchElementException(format("%s not found", name)));
    }

    public String getVariableAsStringIfPresent(String name) {
        Object value = getVariableIfPresent(name);

        return (value == null) ? null : value.toString();
    }

    public Object getVariableIfPresent(String name) {
        return  ofNullable(getContext().getScenarioVariables().get(name))
                .orElseGet(() -> ofNullable(getContext().getFeatureVariables().get(name))
                .orElseGet(() -> ofNullable(getContext().getGlobalVariables().get(name))
                .orElseGet(()-> System.getProperty(name))));
    }

    public String getVariable(String name, String def) {
        return  ofNullable(getContext().getScenarioVariables().get(name))
                .orElseGet(() -> ofNullable(getContext().getFeatureVariables().get(name))
                .orElseGet(() -> ofNullable(getContext().getGlobalVariables().get(name))
                .orElseGet(()-> System.getProperty(name, def)))).toString();
    }

    public String getCurrentFeature() {
        return currentFeature;
    }

    public void setCurrentFeature(String currentFeature) {
        this.currentFeature = currentFeature;
    }
}
