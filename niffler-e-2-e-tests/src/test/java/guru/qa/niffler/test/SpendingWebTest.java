package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@WebTest
public class SpendingWebTest {


    @ApiLogin(username = "SLomako", password = "12345")
    @Category(
            username = "SLomako",
            category = "Стрельба"
    )
    @Spend(
            username = "SLomako",
            description = "Соревнования",
            category = "Стрельба",
            amount = 10000.00,
            currency = CurrencyValues.RUB
    )
    @Test
    void spendingShouldBeDeletedAfterDeleteAction(SpendJson createdSpend) {
        open();
        Selenide.open(Config.getInstance().nifflerFrontURL() + "/main");

        $(".spendings__content tbody")
                .$$("tr")
                .findBy(text(createdSpend.getDescription()))
                .$("td", 0)
                .scrollIntoView(true)
                .click();

        $(byText("Delete selected")).click();

        $(".spendings__content tbody")
                .$$("tr")
                .shouldHave(size(0));
    }
}