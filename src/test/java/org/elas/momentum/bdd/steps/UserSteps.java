package org.elas.momentum.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.elas.momentum.user.domain.model.*;
import org.elas.momentum.user.domain.exception.InvalidEmailException;

import static org.assertj.core.api.Assertions.*;

public class UserSteps {

    private User user;
    private Exception thrownException;

    @Given("a new user with email {string} and name {string} {string}")
    public void a_new_user(String email, String firstName, String lastName) {
        user = User.register(Email.of(email), "hashed_password", firstName, lastName);
    }

    @When("the user activates their account")
    public void the_user_activates() {
        user.activate();
    }

    @Then("the user status is {string}")
    public void the_user_status_is(String status) {
        assertThat(user.getStatus().name()).isEqualTo(status);
    }

    @Then("the user has first name {string}")
    public void the_user_has_first_name(String firstName) {
        assertThat(user.getFirstName()).isEqualTo(firstName);
    }

    @Given("an invalid email {string}")
    public void an_invalid_email(String email) {
        try {
            Email.of(email);
        } catch (InvalidEmailException e) {
            thrownException = e;
        }
    }

    @Then("an InvalidEmailException is thrown")
    public void an_invalid_email_exception_is_thrown() {
        assertThat(thrownException).isInstanceOf(InvalidEmailException.class);
    }
}
