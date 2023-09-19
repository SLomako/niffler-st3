package guru.qa.niffler.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ProfilePage {

    private final SelenideElement
            profileOpen = $("a[href='/profile']"),
            elementInput = $("input[type='file']"),
            imageAvatar = $(".profile__avatar"),
            avatarClickOn = $(".edit-avatar__container"),
            categoryInput = $(".form__input[name='category']"),
            createButton = $x("//button[text()='Create']");

    private final ElementsCollection categoriesListAll = $$(".categories__item");

    public void openProfilePage() {
        profileOpen.click();

    }
    public String uploadAvatar(String filename) {
        avatarClickOn.click();
        elementInput.uploadFromClasspath(filename);
        return imageAvatar.attr("src");
    }

    public void createCategory(String name) {
        categoryInput.setValue(name);
        createButton.click();
    }

    public void shouldHaveCategory(String categoryName) {
        categoriesListAll.findBy(text(categoryName)).shouldBe(visible);
    }
}
