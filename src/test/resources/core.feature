@compile
Feature: code commands usage

  Scenario: match equals usage
    * match "asdf" == "asdf"
    * set foo = "bar"
    * def "global" = "world"
    Then match "${foo} ${global}" == "bar world"

  Scenario: match not equals usage
    * match "asdf" != "1234"

  Scenario: match contains usage
    * match "asdf" contains "df"

  Scenario: match contains usage
    * match "asdf" !contains "gh"
    * set test = "hello"
#   the ${global} variable comes from previous step
    * match "${test} ${global}" == "hello world"

  Scenario: match json string
    * set car = "{color: "red", "model": "toyota", "accessories" : {"navigation":"true", "heatedSeat":"false"}}"
    * match "${car.accessories}" == "{"navigation":"true","heatedSeat":"false"}"
    * match "${car.color}" == "red"
    * match "${car.model}" !contains "gm"

  Scenario: match greaterThan lessThan
    * match 2 < 11
    * match "2.50" < "11.06"

  Scenario: accessing java methods
    * set helloWorld = "Java.type('org.apache.commons.lang3.StringUtils').join('hello', ' ', 'world')"
    * match ${helloWorld} == "hello world"

  Scenario: accessing static variables of java object
    * set mediaType = "Java.type('javax.ws.rs.core.MediaType')"
    * match ${mediaType.APPLICATION_JSON} == "application/json"

  Scenario: default java utility methods
    * print ${date.now().toString()}
    * print ${dateTime.now().toString()}
    * match ${string.join('hello', ' ', 'world')} == "hello world"
    * print ${random.string()}
    * print ${random.string(10)}
    * print ${random.number()}
    * print ${random.decimal()}
    * print ${random.alphanumeric()}
    * print ${random.alphanumeric(10)}
