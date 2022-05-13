Feature: Demo Login Page

  @compile
  Scenario: Demo login with invalid credentials
    Given go to "http://www.phptravels.net/login"
    And enter "Email" "user@phptravels.com"
    And enter "Password" "demouser123"
    When click "Login"[3]
    And wait 2 SECONDS
    And wait until "Please enter your account credentials below" is VISIBLE
    Then verify "Please enter your account credentials below" is VISIBLE

  @compile
  Scenario: Demo login with valid credentials
    Given go to "http://www.phptravels.net/login"
    And enter "Email" "user@phptravels.com"
    And enter "Password" "demouser"
    When click "Login"[3]
    And wait until PAGELOAD
    And wait until "Welcome Back" is VISIBLE
    Then verify "Wallet Balance" is VISIBLE
    And click "My Profile"
    And verify "name=email" == "user@phptravels.com"