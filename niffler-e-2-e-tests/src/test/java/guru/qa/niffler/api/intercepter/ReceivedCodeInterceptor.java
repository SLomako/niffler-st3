package guru.qa.niffler.api.intercepter;

import guru.qa.niffler.api.context.SessionStorageContext;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class ReceivedCodeInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        String locationHeader = response.header("Location");
        if (response.code() == 302 && locationHeader != null && locationHeader.contains("authorized?code=")) {
            SessionStorageContext.getInstance().setCodeKey(StringUtils.substringAfter(locationHeader, "code="));
        }
        return response;
    }
}
