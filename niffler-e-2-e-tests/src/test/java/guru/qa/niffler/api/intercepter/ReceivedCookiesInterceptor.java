package guru.qa.niffler.api.intercepter;

import guru.qa.niffler.api.context.CookieContext;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        CookieContext cookieContext = CookieContext.getInstance();
        Response response = chain.proceed(chain.request());
        List<String> setCookiesHeader = response.headers("Set-cookie");
        if (!setCookiesHeader.isEmpty()) {
            for (String header : setCookiesHeader) {
                for (String cookie : header.split(";")) {
                    String[] rawCookie = cookie.split("=");
                    if (rawCookie[0].equals("JSESSIONID")) {
                        cookieContext.setJSessionIdKey(rawCookie.length == 2 ? rawCookie[1] : null);
                    } else if (rawCookie[0].equals("XSRF-TOKEN")) {
                        cookieContext.setXsrfTokenKey(rawCookie.length == 2 ? rawCookie[1] : null);
                    }
                }
            }
        }
        return response;
    }
}
