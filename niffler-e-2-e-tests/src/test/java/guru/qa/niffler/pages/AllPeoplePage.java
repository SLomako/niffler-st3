package guru.qa.niffler.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.UserJson;

import java.time.Duration;
import java.util.Arrays;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage {

    public boolean isStatusDisplayedForUser(UserJson userForTest, String expectedStatus) {

            $("a[href='/people']").click();
            $$(".main-content__section").shouldBe(CollectionCondition.sizeGreaterThanOrEqual(1), Duration.ofSeconds(10));

        return $$(".main-content__section").stream().anyMatch(row -> {
                SelenideElement element = $(row);
                String text = element.getText();
                String[] values = text.split("\n");
                return Arrays.asList(values).contains(userForTest.getUsernamePeek()) &&
                        Arrays.asList(values).contains(expectedStatus);
            });
        }
}
