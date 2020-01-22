## Web Testing Made `Simple.`

Salad - The testing tool for the web-ui, rest-api and validating data in database with few commands. 

It is developed using Cucumber and Selenium for Java.
The BDD syntax popularized by Cucumber is language-neutral, and easy for even non-programmers.
The backend for the UI testing is written in Selenium for Java.

Salad will provide predefined step-definitions which can be used in the existing cucumber feature files 

# Index

<table>
<tr>
  <th>Start</th>
  <td>
      <a href="#maven">Maven</a> 
    | <a href="#gradle">Gradle</a>
    | <a href="#quickstart">Quickstart</a>
  </td>
</tr>
<tr>
  <th>Web-ui Commands</th>
  <td>
      <a href="#goto">goto</a> 
      <a href="#enter">enter</a> 
    | <a href="#select">select</a>
    | <a href="#click">click</a>
    | <a href="#displays">displays</a>
    | <a href="#verify">verify</a>
    | <a href="#displays">displays</a>
    | <a href="#onpage">onPage</a>
    | <a href="#wait">wait</a>
    | <a href="#waitUntil">waitUntil</a>
    | <a href="#hoverandclick">hoverAndClick</a>
    | <a href="#fill-page">fill page</a>
    | <a href="#setValue">setValue</a>
    | <a href="#skip-browser-restart">skip browser restart</a>
  </td>
</tr>
<tr>
  <th>Rest api Commands</th>
  <td>
      <a href="#post">post</a> 
      <a href="#put">put</a> 
      <a href="#get">get</a> 
  </td>
</tr>
<tr>
  <th>Database Commands</th>
  <td>
      <a href="#query">query</a> 
  </td>
</tr>
<tr>
  <th>Common Commands</th>
  <td>
      <a href="#set">set</a> 
    | <a href="#def">def</a>
    | <a href="#print">print</a>
    | <a href="#match">match</a>
    | <a href="#random">random</a>
    | <a href="#call-java-methods">call java methods</a>
    | <a href="#inbuilt-java-utilities">inbuilt java utilities</a>
  </td>
</tr>
<table>

## Getting Started
Salad requires [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 8 (at least version 1.8.0_112 or greater) and then either [Maven](http://maven.apache.org), [Eclipse](#eclipse-quickstart) or [IntelliJ](https://github.com/intuit/Cucumber-Salad/wiki/IDE-Support#intellij-community-edition) to be installed.

## Maven
you just need one `<dependency>`:

```xml
<dependency>
    <groupId>com.salad</groupId>
    <artifactId>cucumber-salad</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Gradle
Alternatively for [Gradle](https://gradle.org) you need one entries:

```yml
    testCompile 'com.salad:cucumber-salad:0.0.1'
```

## Quickstart
Cucumber-Salad embrace the cucumber framework. 
* configure properties [optional]
* Add the *.feature file
* Add the glue to cucumber test __"glue = {"com.salad.stepdefinitions"}"__

#### Configuration
All the environment variables should be specified in the __"resources/config/\<environment>.yaml"__ file. 
The environment should be supplied as the VM arguments.
Example ```-Denvironment=qa```
> If environment is not specified then it will default to qa

##### Example
src/test/resources/config/qa.yaml
```
url=https://salad.com
userId=testuserId
password=!password987
```

#### Creating the test
##### Example
###### JUnit Test
> @CucumberOptions is from package cucumber.api.CucumberOptions;
```java
@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber-reports"},
        features={"src/test/resources/login.feature"},
        glue = {"com.salad.stepdefinitions"})
public class WebUITest {
}
```
##### OR
```java
@CucumberOptions(features={"src/test/resources"})
public class WebUITest extends CucumberSaladJunit {
}
```
###### TestNG Test
> @CucumberOptions is from package io.cucumber.testng.CucumberOptions;
```java
@CucumberOptions(features={"src/test/resources"})
public class WebUITest extends CucumberSaladTestng {
}
```
##### To run scenarios in parallel
> Note: do not use `def` command if running scenarios in parallel
```java
@CucumberOptions(features = {"src/test/resources/match.feature"})
public class WebUITest extends CucumberSaladTestng {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
```
#### Script Structure
Cucumber scripts are technically in 'Gherkin' format - but all you need to understand intuitively as someone who needs to test web are the three sections: Feature, Background and Scenario. There can be multiple Scenario-s in a *.feature file, and at least one should be present. The Background is optional.


Lines that start with a # are comments.
```
Feature: brief description of what is being tested
    more lines of description if needed.

Background:
  # this section is optional !
  # steps here are executed before each Scenario in this file
  # variables defined here will be 'global' to all scenarios
  # and will be re-initialized before every scenario
  
Scenario: brief description of this scenario
  # steps for this scenario

Scenario: a different scenario
  # steps for this other scenari
```

## Web-ui Commands
## `goto`
#### open browser and go to a web page
```cucumber
Scenario: go to login page
    goto "${hostname}/Web/Login"
```
## `enter`
#### enter a value inside the html input field
you can use CSS, XPATH, NAME, CLASSNAME, LINK TEXT or the display text to select an element
<br> enter "\<selector>" "value"
```cucumber
Scenario: enter user id and password
# input element is populated with text
    Given enter "User ID" "testUserId" 
    
# input element is populated with css id
    And enter "#password" "testPassword"
    
# input element is populated with css class
    And enter ".password" "testPassword"
    
# input element is populated with xpath
    And enter "//input[@value='password']" "testPassword"
    
# input element is populated with name attribute
    And enter "name=password" "testPassword"
    
# input element is populated with text
    And enter "class=password" "testPassword"
```
## `select`
#### select a value from dropdown
select "\<selector>" "value"
>you can use CSS, XPATH, NAME, CLASSNAME, LINK TEXT or the display text to select an element. refer ```enter``` above to see all possible selections

```cucumber
Scenario: enter user id and password
    And select "Transaction Type:" "Cash"

```
## `click`
#### Click the HTML element
>you can use CSS, XPATH, NAME, CLASSNAME, LINK TEXT or the display text to select an element. refer ```enter``` above to see all possible selections
```cucumber
Scenario: click using display text, css, xpath
    And click "Log In"
    And click ".dropdown-toggle click li[data-original-index='1']"
    And click "//h4[normalize-space()='Financial Structure']"
```
## `displays`
#### assert the HTML element is displayed on the page
>you can use CSS, XPATH, NAME, CLASSNAME, LINK TEXT or the display text to select an element. refer ```enter``` above to see all possible selections
```cucumber
Scenario: assert a vin is displayed in the ui
    Then displays "VIN: 2T2ZZMCA6KC119068"
    And displays "#dealership-selection"
```
## `verify`
#### verify the HTML element value on the page
verify "\<selector>" <operator> ["value"]
<br>Valid operators are ==, !=, contains, !contains, <, <=, >, >=
visible, invisible (or !visible), enabled, disabled ( or !enabled)
>you can use CSS, XPATH, NAME, CLASSNAME, LINK TEXT or the display text to select an element. refer ```enter``` above to see all possible selections

```cucumber
Scenario: assert a vin is displayed in the ui
    And verify "input#vin" == "2T2ZZMCA6KC119068"
## "is" is an optional text in the below text. it can be used for readability
    And verify ".app-vehicle-edit-save-btn" is disabled
    And verify ".app-vehicle-edit-save-btn" enabled
```
## `onPage`
#### assert the current url of the browser
```cucumber
Scenario: assert landed on customer page
    Given onPage "${hostname}/customer/${customerId}"
```
## `wait`
#### wait for given amount of time
wait \<number> <MILLI_SECOND|SECOND|MINITE|HOUR>
```cucumber
Scenario: wait for 1 second
    Given wait 1 SECOND
    Given wait 500 MILLI_SECOND
```
## `waitUntil`
#### wait until certain condition is met
waitUntil \<selector> [is] <visible|not visible(or !visible)|clickable|not clickable(or !clickable)|enabled|disabled(or !enabled/not enabled)>
<br>OR
<br>waitUntil PAGELOAD
>you can use CSS, XPATH, NAME, CLASSNAME, LINK TEXT or the display text to select an element. refer ```enter``` above to see all possible selections
```cucumber
Scenario: wait for 1 second
    Given waitUntil PAGELOAD
    And waitUntil ".app-vehicle-edit-save-btn" is not visible
    And waitUntil ".app-vehicle-edit-btn" VISIBLE
```
## `hoverAndClick`
#### hover a text and click from the drop down
hoverAndClick "hover text" "click text"
```cucumber
Scenario: Go to Desktop page
    And hoverAndClick "Deals" "Desktop"
```
## `alert`
#### alert action
alert "<accept|dismiss>"
```cucumber
Scenario: Go to Desktop page
    And alert accept
    Then alert dismiss
```
## `switch Window`
#### switch window to the new page or tab
switch window
switch window to "<window page>"
```cucumber
Scenario: switch window to next newly opened page/tab
    And switch window
Scenario: switch window to main page
    And switch window to "main"
```
## `fill page`
#### fill page with list of provided values
fill page "\<json key value>" or fill page "resources:jsonFilePath"
<br>key - element selector (CSS,Xpath, display text etc)
<br>value - value to be entered on the page
```cucumber
Scenario: fill page
    Given fill page "{"Lease": "click", "Last:": "##string", "First:": "##string", "SSN:": "##numeric(9)", "ZIP:": "##numeric(5)"}"
    
Scenario: fill page from json file
    Given fill page "resources:json/web_fill_page.json"
```

## `setValue`
#### Set a named variable with the value from ui-element
setValue variableName = "\<element selector>"
```cucumber
Scenario: assigning a value from ui element
    Given setValue contractNumberText = "#pageheaderContainer"
    Then print ${contractNumberText}
```

## `skip browser restart`
By default the browser is restarted after every scenario. Sometimes we need to verify multiple scenario on the same page. For those case, `def skip_browser_restart = true` at the beginning of the feature.
```cucumber
  Background: set up
    * set skip_browser_restart = "true"
```

## Rest Api Commands
## `post`
#### post a message to rest endpoint
<br>post \<url> \<body>.
<br> The result can be accessed by __"response"__ variable. response has status and body
Yes you can use variables in the url and body. Also body can be read from a file in classpath
```cucumber
Scenario: post body to a rest url
    When post "${hostname}/api/salad/customer" "{"firstName":"##string(8)","lastName":"##string","ssn":"##numeric(9)"}"
    Then match "${response.status}" == "200"
    * def "customerId" = "${response.body.id}"
    
Scenario: post body from file to a rest url
    When post "${hostname}/api/salad/customer" "resources:json/customerRequest.json"
    Then match "${response.status}" == "200"
```
## `put`
#### put a message to rest endpoint
<br>put \<url> \<body>.
<br> The result can be accessed by __"response"__ variable. response has status and body
Yes you can use variables in the url and body. Also body can be read from a file in classpath
```cucumber
Scenario: put body to a rest url
    When put "${hostname}/api/salad/customer/1" "{"firstName":"##string(8)","lastName":"##string","ssn":"##numeric(9)"}"
    Then match "${response.status}" == "200"
    * def "customerId" = "${response.body.id}"
    
Scenario: put body from file to a rest url
    When post "${hostname}/api/salad/customer/1" "resources:json/customerRequest.json"
    Then match "${response.status}" == "200"
```
## `get`
#### get a message to rest endpoint
<br>get \<url>.
<br> The result can be accessed by __"response"__ variable. response has status and body
Yes you can use variables in the url.
```cucumber
Scenario: get customer from a rest url
    When get "${hostname}/api/salad/customer/1" 
    Then match "${response.status}" == "200"
    * def "customerId" = "${response.body.id}"
```

## Database Commands
## `query`
####executes a sql query in the environment's database
query "\<query>"
<br>The result can be accessed by __"result"__ variable. response has status and body<br>
Yes you can use variables in the query
```
  Scenario: Get the customerId for customer table
    When query "select * from customer where cust_name='${name}'"
    Then print "CUST_ID of ${name} is ${result.CUST_ID}"
```

## Common Commands
## `set`
#### Set a named variable for a scenario
This variable will not be accessible between scenario. This will not be accessible in other scenarios
```cucumber
Scenario: assigning a value:
    Given def myVar = "hello"
    Then print "${myVar}"
```
## `def`
#### Set a named variable for a feature
This variable will be accessible between scenario under the same feature.
```cucumber
Scenario: assigning a value:
    Given def myVar = "hello"
    
Scenario: assigning a value:
    Then print "${myVar}"
```
## `print`
#### Log to the console
You can use print to log variables to the console in the middle of a script. you can use multiple variables with in a print statement
```cucumber
Scenario: print two variable
    Given def myVar = "hello"
    Given set project = "Cucumber-Salad"
    Then print "${myVar} welcome to ${project}"
```
## `match`
#### assert a value
match "\<lhs>" \<operator> "\<rhs>"
<br>Valid operators are ==, !=, contains, !contains, <, <=, >, >=
```cucumber
Scenario: assert car  vaiable
    When set "car" = "{color: "red", "model": "toyota"}"
    Then match "${car.color}" == "red"
    match "${car.model}" !contains "gm"
```

## `random`
use `##` random values to populate ui or in rest-api request
```
  Scenario: define variable for pageObjects
# random string
    * enter "Last:" "##string"
# random string with length 8
    * enter "First:" "##string(8)"
# random number string
    * enter "Mileage:" "##numeric()"
# random number string with length 9
    * enter "SSN:" "##numeric(9)"
# random string with length 9 in rest api request
    When post "${hostname}/api/salad/customer" "{"firstName":"##string(8)","lastName":"##string","ssn":"##numeric(9)"}"
```
## `call Java methods`
you can define a variable for java class and access static methods and variables
```
  Scenario: define variable for pageObjects
    * def date = "Java.type('java.time.LocalDateTime')"
    * print ${date.now()}
```
## `inbuilt Java utilities`
date, string, and random are the inbuilt utilities<br>

|variable   |class          |
|:----------|:----------------|
| date      | java.time.LocalDate   |
| dateTime  | java.time.LocalDateTime   |
| string    | org.apache.commons.lang3.StringUtils   |
| random    | RandomUtils   |
| salad     | SaladCommands   |
```
    Scenario: default java utility methods
      * print ${date.now().toString()}
      * match ${string.join('hello', ' ', 'world')} == "hello world"
      * print ${random.string()}
      * print ${random.string(10)}
      * print ${random.numeric()}
      * print ${random.number()}
      * print ${random.decimal()}
      * print ${random.alphanumeric()}
      * print ${random.alphanumeric(10)}
      * set contractNumberText = "${salad.getElement("//*[@id='pageheaderContainer']/div[1]").getText()}"
```
## Run test
```
mvn test -Denvironment=qa
```

