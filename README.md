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
      <a href="#go-to">go to</a> 
    | <a href="#enter">enter</a> 
    | <a href="#select">select</a>
    | <a href="#click">click</a>
    | <a href="#displays">displays</a>
    | <a href="#verify">verify</a>
    | <a href="#displays">displays</a>
    | <a href="#on-page">on page</a>
    | <a href="#wait">wait</a>
    | <a href="#wait-until">wait until</a>
    | <a href="#hover-and-click">hover and click</a>
    | <a href="#fill-page">fill page</a>
    | <a href="#set-value">set value</a>
    | <a href="#skip-browser-restart">skip browser restart</a>
  </td>
</tr>
<tr>
  <th>Rest api Commands</th>
  <td>
      <a href="#post">post</a> 
      | <a href="#put">put</a> 
      | <a href="#get">get</a> 
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
    | <a href="#match-or-assert">match or assert</a>
    | <a href="#random">random</a>
    | <a href="#call-java-methods">call java methods</a>
    | <a href="#inbuilt-java-utilities">inbuilt java utilities</a>
  </td>
</tr>
</table>

## Getting Started
Salad requires [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (11 or greater) and [Maven](http://maven.apache.org), and then either [Eclipse](https://www.eclipse.org/downloads/packages/release/kepler/sr1/eclipse-ide-java-developers) or [IntelliJ](https://www.jetbrains.com/idea/download) to be installed.

## Maven
you just need one `repository` and `<dependency>` to get started

```xml
<repositories>
   <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
   </repository>
</repositories>

<dependencies>
   <dependency>
      <groupId>com.github.gopyyn</groupId>
      <artifactId>Salad</artifactId>
      <version>1.0.5</version>
    </dependency>
</dependencies>
```

## Gradle
Alternatively for [Gradle](https://gradle.org) you need one entries:

```yml
   repositories {
      ...
      maven {
        url "https://jitpack.io"
      }
   }
   testCompile 'com.github.gopyyn:salad:1.0.5'
```

## Quickstart
Salad embrace the cucumber framework.
* Cucumber feature file
* Configuration yaml file [optional]
* Cucumber Java Test File [optional]

#### Cucumber feature file
Cucumber feature are technically in 'Gherkin' format - but all you need to understand intuitively as someone who needs to test web are the three sections: Feature, Background and Scenario. There can be multiple Scenario-s in a *.feature file, and at least one should be present. The Background is optional.

Lines that start with a `#` are comments.

```
Feature: brief description of what is being tested
    more lines of description if needed.

Background:
  # this section is optional !
  # steps here are executed before each Scenario in this file
  # variables defined here will be 'global' to all scenarios in this file
  # and will be re-initialized before every scenario
  
Scenario: brief description of this scenario
  # steps for this scenario

Scenario: a different scenario
  # steps for this other scenari
```

#### Configuration [optional]
The configuration should be written in YAML format under __"resources/config/\<environment>.yaml"__
All the environment variables should be specified in the file. 
>The environment should be supplied as the VM arguments.
Example ```-Denvironment=qa```
> If environment is not specified then it will default to qa

The configuration is in YAML format and it has 2 major section. system and database
 * system -> specify all the system/global properties here.
<table>
<tr>
  <th>salad.browser</th>
  <td>
     valid values are INTERNETEXPLORER, FIREFOX, CHROME, EDGE, OPERA, SAFARI, HEADLESS (chrome with headless).
     <br>If not provided then default to EDGE in windows, SAFARI in mac and FIREFOX in linux
  </td>
</tr>
<tr>
  <th>selenium.remote</th>
  <td>
     if true connects to selenium server running in remote server.
     <br>default false
  </td>
</tr>
<tr>
  <th>selenium.host</th>
  <td>
     remote selenium server host name
  </td>
</tr>
<tr>
  <th>selenium.port</th>
  <td>
     remote selenium server port
  </td>
</tr>
</table>

 * database -> database connection details. Accepts all java hibernate properties. 
               Only needed if connection to Database

##### Example
src/test/resources/config/qa.yaml
```
## Optional system properties
system:
  salad.browser: chrome
  # selenium.remote: false
  # selenium.host: remote-hostname.com
  # selenium.port: 4444
  ### Below are user defined properties 
  ### which can be accessed in feature files with ${<property name>}
  userId: testUserId
  password: somepassword@123
  rest-host: https://jsonplaceholder.typicode.com
  
## Optioanl database connection details. Accepts all java hibernate properties.   
database:
  hibernate.connection.driver_class: org.h2.Driver
  hibernate.connection.url: jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
  hibernate.connection.username: ""
  hibernate.connection.password: ""
  hibernate.dialect: org.hibernate.dialect.H2Dialect
  hibernate.default_schema: PUBLIC
  hibernate.show_sql: true

```

#### Cucumber Java Test File [Optional]
Salad provided an option of running feature file either as Junit or TestNG test.
Salad framework provide **SaladJunit** and **SaladTestng** which can be extended by the java test
All the reports of the test will be under _"target/cucumber-reports"_
##### Example
###### JUnit Test
> @CucumberOptions is from package cucumber.api.CucumberOptions;
```java
@CucumberOptions(features={"src/test/resources"})
public class WebUITest extends SaladJunit {
}
```
###### TestNG Test
> @CucumberOptions is from package io.cucumber.testng.CucumberOptions;
```java
@CucumberOptions(features={"src/test/resources"})
public class WebUITest extends SaladTestng {
}
```
##### To run scenarios in parallel
> Note: do not use `def` command if running scenarios in parallel
```java
@CucumberOptions(features = {"src/test/resources/match.feature"})
public class WebUITest extends SaladTestng {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
```

## Web-ui Commands
## `go to`
#### open browser and go to a web page
```cucumber
Scenario: go to login page
    go to "${hostname}/Web/Login"
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
>you can use CSS, XPATH, NAME, CLASSNAME, LINK TEXT or the display text to select an element. refer ```enter``` above to see all possible selector values

```cucumber
Scenario: enter user id and password
    And select "Transaction Type:" "Cash"

```
## `click`
#### Click the HTML element
>you can use CSS, XPATH, NAME, CLASSNAME, LINK TEXT or the display text to select an element. refer ```enter``` above to see all possible selector values
```cucumber
Scenario: click using display text, css, xpath
    And click "Log In"
    And click ".dropdown-toggle click li[data-original-index='1']"
    And click "//h4[normalize-space()='Financial Structure']"
```
## `displays`
#### assert the HTML element is displayed on the page
>you can use CSS, XPATH, NAME, CLASSNAME, LINK TEXT or the display text to select an element. refer ```enter``` above to see all possible selector values
```cucumber
Scenario: assert a vin is displayed in the ui
    Then displays "VIN: 2T2ZZMCA6KC119068"
    And displays "#dealership-selection"
```
## `verify`
#### verify the HTML element value on the page
1. verify "\<selector>" is \<operation>
<br>Valid operators CLICKABLE, VISIBLE, INVISIBLE, ENABLED, DISABLED, !CLICKABLE, !VISIBLE, !ENABLED
2. verify "\<selector>" \<match_operation> ["value"]
    1. selector will give the value or text of the selected html tag
    2. Valid match_operation are EQUALS, ==, NOT_EQUALS, !=, CONTAINS, contains, NOT_CONTAINS, !contains, GREATER_THAN, >, LESS_THAN, <, GREATER_THAN_OR_EQUAL_TO, >=, LESS_THAN_OR_EQUAL_TO
>you can use CSS, XPATH, NAME, CLASSNAME, LINK TEXT or the display text to select an element. refer ```enter``` above to see all possible selector values

```cucumber
Scenario: assert a vin is displayed in the ui
    And verify "input#vin" == "2T2ZZMCA6KC119068"
## "is" is an optional text in the below text. it can be used for readability
    And verify ".save-btn" is DISABLED
    And verify ".edit-btn" ENABLED
```
## `on page`
#### assert the current url of the browser
```cucumber
Scenario: assert landed on customer page
    Then on page "${hostname}/customer/${customerId}"
```
## `wait`
#### wait for given amount of time
wait \<number> <MILLI_SECONDS|SECONDS|MINUTES|HOURS>
```cucumber
Scenario: wait for 1 second
    Given wait 1 SECONDS
    Given wait 500 MILLI_SECONDS
```
## `wait until`
#### wait until certain condition is met
wait until \<selector> [is] <visible|not visible(or !visible)|clickable|not clickable(or !clickable)|enabled|disabled(or !enabled/not enabled)>
<br>OR
<br>wait until PAGELOAD
>you can use CSS, XPATH, NAME, CLASSNAME, LINK TEXT or the display text to select an element. refer ```enter``` above to see all possible selector values
```cucumber
Scenario: wait for 1 second
    Given wait until PAGELOAD
    And wait until ".save-btn" is not visible
    And wait until ".edit-btn" VISIBLE
```
## `hover and click`
#### hover a text and click from the drop down
hover and click "hover text" "click text"
```cucumber
Scenario: Go to Desktop page
    And hover and click "Menu" "profile"
```
## `alert`
#### alert action
alert "<accept|dismiss|send_text>"
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

## `set value`
#### Set a named variable with the value from ui-element
set value variableName = "\<element selector>"
```cucumber
Scenario: assigning a value from ui element
    Given set value contractNumberText = "#pageheaderContainer"
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
### `header`
#### add a header to Request

header \<name> \<body>
```cucumber
Scenario: add header for rest api 
    Given header "Accept" "application/json"
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
    Given set project = "Salad"
    Then print "${myVar} welcome to ${project}"
```
## `match` or `assert`
#### assert a value
match "\<lhs>" \<operator> "\<rhs>"
<br>or<br>
assert "\<lhs>" \<operator> "\<rhs>"
<br>Valid operators are ==, !=, contains, !contains, <, <=, >, >=
```cucumber
Scenario: assert car  vaiable
    When set "car" = "{color: "red", "model": "toyota"}"
    Then match "${car.color}" == "red"
    And match "${car.model}" !contains "gm"
    Then assert "${car.color}" != "red"
    And assert "${car.model}" contains "gm"
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

