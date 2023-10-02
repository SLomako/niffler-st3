package guru.qa.niffler.api.intercepter;

import guru.qa.niffler.api.context.CookieContext;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nullable;
import java.io.IOException;

public class AddCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        CookieContext cookieContext = CookieContext.getInstance();
        Request originalRequest = chain.request();
        Headers.Builder builder = originalRequest.headers().newBuilder();

        @Nullable String jSessionIdCookie = cookieContext.getJSessionIdKey();
        @Nullable String xsrfTokenKey = cookieContext.getXsrfTokenKey();

        if (jSessionIdCookie != null || xsrfTokenKey != null) {
            builder.removeAll("Cookie");
            builder.add("Cookie",
                    cookieContext.getJSessionIdFormattedCookie()
                            + ";"
                            + cookieContext.getXsrfTokenFormattedCookie());
        }

        Headers newHeader = builder.build();

        return chain.proceed(originalRequest.newBuilder()
                .url(originalRequest.url())
                .headers(newHeader)
                .method(originalRequest.method(), originalRequest.body())
                .build());
    }
}
