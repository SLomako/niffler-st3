package guru.qa.niffler.test;

import com.codeborne.selenide.Condition;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.DBCreateUser;
import guru.qa.niffler.jupiter.annotation.WebTest;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@WebTest
public class UserAuthorizationTests {

    @DBCreateUser(username = "random", password = "random")
    @ApiLogin()
    @Test
    void shouldDisplaySpendingSectionAfterLoginWithDBUser() {
        open(Config.getInstance().nifflerFrontURL() + "/main");
        $("section.main-content__section-stats").should(Condition.visible);
    }
}
