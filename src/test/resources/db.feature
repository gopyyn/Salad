Feature: database query usage
  Background:
   * query "CREATE TABLE PERSON(id int primary key, name varchar(255))"
   * query "INSERT INTO PERSON (id, name) values ('1', 'John');"
   * query "INSERT INTO PERSON (id, name) values ('2', 'Ahmad');"
   * query "INSERT INTO PERSON (id, name) values ('3', 'Ram');"

  @compile
  Scenario: Get the person id by name
    Given set name = "John"
    When query "select * from PERSON where name='${name}'"
    Then match "${result.[0].ID}" == 1
    And print "${result}"
    And print "Id: ${result.[0].ID} is ${result.[0].NAME}"