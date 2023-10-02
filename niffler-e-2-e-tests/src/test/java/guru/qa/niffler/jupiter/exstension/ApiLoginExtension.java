package guru.qa.niffler.jupiter.exstension;

import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.AuthServiceClient;
import guru.qa.niffler.api.context.CookieContext;
import guru.qa.niffler.api.context.SessionStorageContext;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;

import java.io.IOException;
import java.util.Map;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sessionStorage;

public class ApiLoginExtension implements BeforeEachCallback, AfterTestExecutionCallback {

    private final AuthServiceClient authServiceClient = new AuthServiceClient();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        ApiLogin annotation = extensionContext.getRequiredTestMethod().getAnnotation(ApiLogin.class);
        if (annotation != null) {
            Map userDataMap = extensionContext.getStore(DBCreateUserExtension.NAMESPACE)
                    .get(extensionContext.getUniqueId(), Map.class);

            String username = (userDataMap != null && userDataMap.containsKey("originalUsername")) ?
                    (String) userDataMap.get("originalUsername") : annotation.username();
            String password = (userDataMap != null && userDataMap.containsKey("originalPassword")) ?
                    (String) userDataMap.get("originalPassword") : annotation.password();

            doLogin(username, password);
        }
    }

    private void doLogin(String username, String password) {
        SessionStorageContext sessionStorageContext = SessionStorageContext.getInstance();
        sessionStorageContext.init();

        try {
            authServiceClient.doLogin(username, password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        open(Config.getInstance().nifflerFrontURL());
        sessionStorage().setItem("codeChallenge", sessionStorageContext.getCodeChallengeKey());
        sessionStorage().setItem("id_token", sessionStorageContext.getTokenKey());
        sessionStorage().setItem("codeVerifier", sessionStorageContext.getCodeVerifierKey());
        Cookie jsessionIdCookie = new Cookie("JSESSIONID", CookieContext.getInstance().getJSessionIdKey());
        WebDriverRunner.getWebDriver().manage().addCookie(jsessionIdCookie);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        SessionStorageContext.getInstance().clearContext();
        CookieContext.getInstance().clearContext();
    }
}
