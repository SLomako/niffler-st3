package guru.qa.niffler.api.context;

import guru.qa.niffler.util.PkceUtil;

import java.util.HashMap;
import java.util.Map;

public class SessionStorageContext {

    private static final ThreadLocal<SessionStorageContext> INSTANCE = ThreadLocal.withInitial(SessionStorageContext::new);

    private static final String
            CODE_CHALLENGE_KEY = "codeChallenge",
            CODE_VERIFIER_KEY = "codeVerifier",
            TOKEN_KEY = "id_token",
            CODE_KEY = "code";

    private final Map<String, String> store = new HashMap<>();

    public static SessionStorageContext getInstance() {
        return INSTANCE.get();
    }

    public void init() {
        final String codeVerifier = PkceUtil.generateCodeVerifier();
        setCodeChallengeKey(PkceUtil.generateCodeChallenge(codeVerifier));
        setCodeVerifierKey(codeVerifier);
    }

    public void setCodeChallengeKey(String codeChallenge) {
        store.put(CODE_CHALLENGE_KEY, codeChallenge);
    }

    public void setCodeVerifierKey(String codeVerifier) {
        store.put(CODE_VERIFIER_KEY, codeVerifier);
    }

    public void setTokenKey(String token) {
        store.put(TOKEN_KEY, token);
    }

    public void setCodeKey(String code) {
        store.put(CODE_KEY, code);
    }

    public String getCodeChallengeKey() {
        return store.get(CODE_CHALLENGE_KEY);
    }

    public String getCodeVerifierKey() {
        return store.get(CODE_VERIFIER_KEY);
    }

    public String getTokenKey() {
        return store.get(TOKEN_KEY);
    }

    public String getCodeKey() {
        return store.get(CODE_KEY);
    }

    public void clearContext() {
        store.clear();
    }
}
