package org.elas.momentum.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.elas.momentum.rating.domain.model.*;

import static org.assertj.core.api.Assertions.*;

public class RatingSteps {

    private PlayerRating rating;
    private PlayerStats stats;
    private Exception thrownException;
    private String activityId = "activity-1";

    @Given("an activity with id {string} is completed")
    public void an_activity_with_id_is_completed(String id) {
        this.activityId = id;
    }

    @When("user {string} rates user {string} with behavior {int} technicality {int} teamwork {int} level {string}")
    public void user_rates_user(String raterId, String ratedId, int behavior, int technicality, int teamwork, String level) {
        try {
            rating = PlayerRating.create(activityId, raterId, ratedId, behavior, technicality, teamwork,
                    PlayerLevel.valueOf(level), false, "Good game");
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the rating is created with behavior {int}")
    public void the_rating_is_created(int behavior) {
        assertThat(rating).isNotNull();
        assertThat(rating.getBehavior()).isEqualTo(behavior);
    }

    @When("player stats are computed for {int} ratings with avg technicality {double}")
    public void player_stats_are_computed(int total, double avgTech) {
        stats = PlayerStats.empty("user1", "football")
                .withUpdatedRating(4.0, avgTech, 4.0, total, 0);
    }

    @Then("the pro score is greater than {int}")
    public void the_pro_score_is_greater_than(int minScore) {
        assertThat(stats.proScore()).isGreaterThan(minScore);
    }

    @When("an invalid score {int} is used")
    public void an_invalid_score_is_used(int score) {
        try {
            PlayerRating.create(activityId, "rater", "rated", score, 3, 3, PlayerLevel.AMATEUR, false, null);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("an IllegalArgumentException is thrown")
    public void an_illegal_argument_exception_is_thrown() {
        assertThat(thrownException).isInstanceOf(IllegalArgumentException.class);
    }
}
