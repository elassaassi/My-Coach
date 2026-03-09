package org.elas.momentum.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.elas.momentum.activity.domain.model.*;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

public class ActivitySteps {

    private Activity activity;
    private Exception thrownException;

    @Given("an activity {string} with sport {string} and max {int} participants")
    public void an_activity(String title, String sport, int max) {
        activity = Activity.create("organizer-1", title, sport, "INTERMEDIATE",
                new Location(33.9, -6.8, "", "Rabat", "MA"),
                Instant.now().plusSeconds(86400), max);
    }

    @When("user {string} joins the activity")
    public void user_joins(String userId) {
        try {
            activity.join(userId);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the activity has {int} participants")
    public void the_activity_has_participants(int count) {
        assertThat(activity.getCurrentParticipantsCount()).isEqualTo(count);
    }

    @Then("the activity status is {string}")
    public void the_activity_status_is(String status) {
        assertThat(activity.getStatus().name()).isEqualTo(status);
    }

    @When("the activity is cancelled")
    public void the_activity_is_cancelled() {
        activity.cancel();
    }

    @Then("an error is thrown")
    public void an_error_is_thrown() {
        assertThat(thrownException).isNotNull();
    }
}
