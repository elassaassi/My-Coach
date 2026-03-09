Feature: Activity Creation and Joining

  Scenario: Create an activity and join
    Given an activity "Football Sunday" with sport "football" and max 10 participants
    Then the activity has 1 participants
    And the activity status is "OPEN"

  Scenario: Activity becomes FULL when max participants reached
    Given an activity "Mini Tennis" with sport "tennis" and max 2 participants
    When user "player2" joins the activity
    Then the activity has 2 participants
    And the activity status is "FULL"

  Scenario: Cancel an activity
    Given an activity "Basketball" with sport "basketball" and max 5 participants
    When the activity is cancelled
    Then the activity status is "CANCELLED"
