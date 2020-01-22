Feature: rest api

  @compile
  Scenario: create user
    Given header "Accept" "application/json"
    When post "https://jsonplaceholder.typicode.com/posts" "{"title":"##string(8)","body":"bar","userId":"1"}"
    Then print "response is ${response}"
    And match "${response.status}" == "201"
    And match "${response.status}" != "422"

  @compile
  Scenario: update user
    When put "https://jsonplaceholder.typicode.com/posts/1" "{"title":"##string(8)","body":"bar","userId":"1"}"
    Then print "response is ${response}"
    And match "${response.status}" == "200"

  @compile
  Scenario: update user with request from file
    When put "https://jsonplaceholder.typicode.com/posts/1" "resources:request/testRequest.json"
    Then print "response is ${response}"
    And match "${response.status}" == "200"

  @compile
  Scenario: get user
    When get "${rest-host}/posts/1"
    Then print "response is ${response}"
    And match "${response.status}" == "200"
    And match "${response.body.userId}" == "1"