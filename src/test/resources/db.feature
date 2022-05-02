Feature: database query usage

  @compile
  Scenario: Get the person id by name
    Given set name = "John"
#    When query "select * from PERSON where name='${name}'"
#    Then match "${result.[0].ID}" == 1
#    And print "${result}"
#    And print "Id: ${result.[0].ID} is ${result.[0].NAME}"