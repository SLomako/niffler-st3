package guru.qa.niffler.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBCreateUser;
import guru.qa.niffler.jupiter.annotation.OriginalPassword;
import guru.qa.niffler.jupiter.annotation.WebTest;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.ProfilePage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebTest
public class ProfileTest {

    private final LoginPage loginPage = new LoginPage();
    private final ProfilePage profilePage = new ProfilePage();

    @Test
    @DBCreateUser(username = "random", password = "random")
    void shouldSuccessfullyUploadNewAvatar(AuthUserEntity currentUserAuthDB, @OriginalPassword String password) {
        loginPage.login(currentUserAuthDB.getUsername(), password);
        profilePage.openProfilePage();
        String src = profilePage.uploadAvatar("upload-files/photo_profile.jpeg");
        assertTrue(src.startsWith("data:image"), "Src should start with the correct prefix");
    }

    @Test
    @DBCreateUser(username = "random", password = "random")
    void shouldCreateAndVerifyCategory(AuthUserEntity currentUserAuthDB, @OriginalPassword String password) {
        loginPage.login(currentUserAuthDB.getUsername(), password);
        profilePage.openProfilePage();
        profilePage.createCategory("Стрельба");
        profilePage.shouldHaveCategory("Стрельба");
    }

    @Test
    @DBCreateUser(username = "random", password = "random")
    void shouldNotAllowDuplicateCategory(AuthUserEntity currentUserAuthDB, @OriginalPassword String password) {
        loginPage.login(currentUserAuthDB.getUsername(), password);
        profilePage.openProfilePage();
        profilePage.createCategory("Стрельба");
        profilePage.createCategory("Стрельба");

        SelenideElement warningMessage = $(Selectors.byText("Can not add new category"));
        warningMessage.shouldBe(Condition.visible);
    }
}
