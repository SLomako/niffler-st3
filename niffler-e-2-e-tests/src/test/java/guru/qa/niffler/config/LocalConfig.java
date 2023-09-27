package guru.qa.niffler.config;

import com.codeborne.selenide.Configuration;

public class LocalConfig implements Config {

    static final LocalConfig config = new LocalConfig();

    static {
        Configuration.browserSize = "1470x956";
        Configuration.headless = true;
    }

    private LocalConfig() {
    }

    @Override
    public String databaseHost() {
        return "localhost";
    }
}
