Feature: Player Rating after match

  Scenario: Rate a player after activity completion
    Given an activity with id "act-001" is completed
    When user "alice" rates user "bob" with behavior 5 technicality 4 teamwork 5 level "PRO"
    Then the rating is created with behavior 5

  Scenario: Player stats pro score computed
    Given an activity with id "act-002" is completed
    When player stats are computed for 5 ratings with avg technicality 4.5
    Then the pro score is greater than 50

  Scenario: Invalid score rejected
    Given an activity with id "act-003" is completed
    When an invalid score 0 is used
    Then an IllegalArgumentException is thrown

  Scenario: Score above maximum rejected
    Given an activity with id "act-004" is completed
    When an invalid score 6 is used
    Then an IllegalArgumentException is thrown
