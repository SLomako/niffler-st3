package guru.qa.niffler.test;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.AuthDAO;
import guru.qa.niffler.db.dao.UserdataDAO;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserdataUserEntity;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.pages.LoginPage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebTest
public class LoginTest {

    private final LoginPage loginPage = new LoginPage();

    @Test
    @DBCreateUser(username = "random", password = "random")
    public void mainPageShouldBeVisibleAfterLogin(AuthUserEntity currentUserAuthDB, @OriginalPassword String password) {
        loginPage.login(currentUserAuthDB.getUsername(), password);
        loginPage.verifyMainPageIsVisible();
        loginPage.logout();
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
            @AuthUserId UUID createdAuthUserId, @UserdataUserId UUID createdUserdataUserId,
            @OriginalPassword String password) {

        loginPage.login(currentUserAuthDB.getUsername(), password);
        loginPage.verifyMainPageIsVisible();
        loginPage.logout();

        Faker faker = new Faker();
        String renamedUsername = faker.name().firstName();
        authUserDAO.renameUserNameById(createdAuthUserId, renamedUsername);
        userDataDAO.renameUserNameById(createdUserdataUserId, renamedUsername);

        loginPage.login(renamedUsername, password);
        loginPage.verifyMainPageIsVisible();
        loginPage.logout();
    }

    @Test
    @DBCreateUser(username = "random", password = "random")
    void shouldMatchUsernameForCreatedAndFetchedUser(AuthDAO authDAO, AuthUserEntity currentUserAuthDB, @AuthUserId UUID createdAuthUserId) {
        AuthUserEntity fetchedUser = authDAO.getUserById(createdAuthUserId);
        assertEquals(currentUserAuthDB.getUsername(), fetchedUser.getUsername());
    }
}

