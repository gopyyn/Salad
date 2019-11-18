Feature: Demo Login Page

  @compile
  Scenario: Demo login with invalid credentials
    Given goto "https://s2.demo.opensourcecms.com/orangehrm/symfony/web/index.php/auth/login"
    And enter "Username" "abcd@gmail.com"
    And enter "Password" "testPassword"
    And click "LOGIN"
    Then verify "Invalid credentials" is visible

  @compile
  Scenario: Demo login with valid credentials
    Given goto "https://s2.demo.opensourcecms.com/orangehrm/symfony/web/index.php/auth/login"
    And enter "Username" "opensourcecms"
    And enter "Password" "opensourcecms"
    And click "LOGIN"
    Then verify "Welcome Admin" is visible