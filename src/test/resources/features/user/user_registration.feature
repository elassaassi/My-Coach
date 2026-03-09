Feature: User Registration

  Scenario: Successfully register a new user
    Given a new user with email "youness@momentum.app" and name "Youness" "Benali"
    Then the user status is "PENDING_VERIFICATION"
    And the user has first name "Youness"

  Scenario: Activate a registered user
    Given a new user with email "alice@momentum.app" and name "Alice" "Martin"
    When the user activates their account
    Then the user status is "ACTIVE"

  Scenario: Invalid email rejects registration
    Given an invalid email "not-an-email"
    Then an InvalidEmailException is thrown
