package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.context.CookieContext;
import guru.qa.niffler.api.context.SessionStorageContext;
import guru.qa.niffler.api.intercepter.AddCookiesInterceptor;
import guru.qa.niffler.api.intercepter.ReceivedCodeInterceptor;
import guru.qa.niffler.api.intercepter.ReceivedCookiesInterceptor;

import java.io.IOException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AuthServiceClient extends RestService {

    public AuthServiceClient() {
        super(config.nifflerAuthURL(), true,
                new ReceivedCodeInterceptor(),
                new AddCookiesInterceptor(),
                new ReceivedCookiesInterceptor());
    }

    private final AuthService authService = retrofit.create(AuthService.class);

    public void doLogin(String username, String password) throws IOException {
        SessionStorageContext sessionStorageContext = SessionStorageContext.getInstance();
        CookieContext cookieContext = CookieContext.getInstance();

        authService.authorize("code",
                "client",
                "openid",
                config.nifflerFrontURL() + "/authorized",
                sessionStorageContext.getCodeChallengeKey(),
                "S256").execute();

        authService.login(
                username,
                password,
                cookieContext.getXsrfTokenKey()).execute();

        JsonNode response = authService.token(
                "Basic " + new String(Base64.getEncoder().encode("client:secret".getBytes(UTF_8))),
                "client",
                config.nifflerFrontURL() + "/authorized",
                "authorization_code",
                sessionStorageContext.getCodeKey(),
                sessionStorageContext.getCodeVerifierKey()
        ).execute().body();

        assert response != null;
        sessionStorageContext.setTokenKey(response.get("id_token").asText());
    }
}
