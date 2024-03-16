Feature: Demo Login Page

  @compile
  Scenario: Demo login with invalid credentials
    Given go to "http://www.phptravels.net/login"
    And enter "Email" "user@phptravels.com"
    And enter "Password" "demouser123"
    When click "Login"
    And wait 2 SECONDS
    And wait until "Please check your emal and password" is VISIBLE
    Then verify "Please check your emal and password" is VISIBLE

  @compile
  Scenario: Demo login with valid credentials
    Given go to "http://www.phptravels.net/login"
    And enter "Email" "user@phptravels.com"
    And enter "Password" "demouser"
    And click "Login[4]"
    And wait until PAGELOAD
    And wait until "Welcome Back" is VISIBLE
    Then verify "Wallet Balance" is VISIBLE
    And click "My Profile"
    And take screenshot "screen_shoot-2024"
    And verify "name=email" == "user@phptravels.com"
    And click "my bookings"
    And select "name=DataTables_Table_0_length" "3"