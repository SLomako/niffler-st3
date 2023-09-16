package guru.qa.niffler.test;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.AuthDAO;
import guru.qa.niffler.db.dao.UserdataDAO;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserdataUserEntity;
import guru.qa.niffler.jupiter.annotation.AuthUserId;
import guru.qa.niffler.jupiter.annotation.DBCreateUser;
import guru.qa.niffler.jupiter.annotation.UserdataUserId;
import guru.qa.niffler.jupiter.annotation.WebTest;
import guru.qa.niffler.pages.LoginPage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebTest
public class LoginTest {

    private LoginPage loginPage = new LoginPage();

    @Test
    @DBCreateUser(username = "random", password = "random")
    public void mainPageShouldBeVisibleAfterLogin(AuthUserEntity currentUserAuthDB) {
        loginPage.login(currentUserAuthDB.getUsername(), currentUserAuthDB.getPassword());
        $("h1.header__title").shouldHave(text("Niffler. The coin keeper."));
        $(".button-icon.button-icon_type_logout").click();
    }

    @Test
    @DBCreateUser(username = "random", password = "random")
    public void verifyUserExistsInAuthDB(AuthDAO authUserDAO, AuthUserEntity currentUserAuthDB) {
        List<AuthUserEntity> users = authUserDAO.getAllUsers();
        boolean isUserInDB = users.stream()
                .anyMatch(user -> currentUserAuthDB.getUsername().equals(user.getUsername()));
        assertTrue(isUserInDB);
    }

    @Test
    @DBCreateUser(username = "random", password = "random")
    public void verifyUserExistsInUserDataDB(UserdataDAO userDataDAO, UserdataUserEntity currentUserUserdataDB) {
        List<UserdataUserEntity> users = userDataDAO.getAllUsers();
        boolean isUserInDB = users.stream()
                .anyMatch(user -> currentUserUserdataDB.getUsername().equals(user.getUsername()));
        assertTrue(isUserInDB);
    }

    @Test
    @DBCreateUser(username = "random", password = "random")
    void shouldSuccessfullyLoginAfterUsernameIsChangedInDB(
            AuthUserEntity currentUserAuthDB,
            AuthDAO authUserDAO, UserdataDAO userDataDAO,
            @AuthUserId UUID createdAuthUserId, @UserdataUserId UUID createdUserdataUserId) {

        loginPage.login(currentUserAuthDB.getUsername(), currentUserAuthDB.getPassword());
        $("h1.header__title").shouldHave(text("Niffler. The coin keeper."));
        $(".button-icon.button-icon_type_logout").click();

        Faker faker = new Faker();
        String renamedUsername = faker.name().firstName();
        authUserDAO.renameUserNameById(createdAuthUserId, renamedUsername);
        userDataDAO.renameUserNameById(createdUserdataUserId, renamedUsername);

        loginPage.login(renamedUsername, currentUserAuthDB.getPassword());
        $("h1.header__title").shouldHave(text("Niffler. The coin keeper."));
    }

    @Test
    @DBCreateUser(username = "random", password = "random")
    void shouldMatchUsernameForCreatedAndFetchedUser(AuthDAO authDAO, AuthUserEntity currentUserAuthDB, @AuthUserId UUID createdAuthUserId) {
        AuthUserEntity fetchedUser = authDAO.getUserById(createdAuthUserId);
        assertEquals(currentUserAuthDB.getUsername(), fetchedUser.getUsername());
    }
}

