Feature: database query usage
  Background:
   * query "CREATE TABLE PERSON(id int primary key, name varchar(255))"
   * query "INSERT INTO PERSON (id, name) values ('1', '##string()}');"
   * query "INSERT INTO PERSON (id, name) values ('2', '##string(6)');"
   * query "INSERT INTO PERSON (id, name) values ('3', '##string(8)');"

  @compile
  Scenario: Get the person id by name
    When query "select * from PERSON where id='1'"
    Then match "${result.[0].ID}" == 1
    And print "${result}"
    And print "Id: ${result.[0].ID} is ${result.[0].NAME}"
    When query
    """
      select * from PERSON where id='2'
    """
    Then match "${result.[0].ID}" == 2
    And print "${result}"
    And print "Id: ${result.[0].ID} is ${result.[0].NAME}"