package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public abstract class RestService {

    protected static final Config config = Config.getInstance();
    protected final OkHttpClient httpClient;
    protected final Retrofit retrofit;

    protected RestService(String baseUrl) {
        this(baseUrl, false, null);
    }

    protected RestService(String baseUrl, boolean followRedirect) {
        this(baseUrl, followRedirect, null);
    }

    protected RestService(String baseUrl, boolean followRedirect, Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(followRedirect)
                .addInterceptor(new AllureOkHttp3());

        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
        builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        this.httpClient = builder.build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}

