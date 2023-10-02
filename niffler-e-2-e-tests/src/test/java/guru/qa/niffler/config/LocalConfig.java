package guru.qa.niffler.config;

public class LocalConfig implements Config {

    static final LocalConfig config = new LocalConfig();

    private LocalConfig() {
    }

    @Override
    public String databaseHost() {
        return "localhost";
    }

    @Override
    public String nifflerFrontURL() {
        return "http://127.0.0.1:3000";
    }

    @Override
    public String nifflerAuthURL() {
        return "http://127.0.0.1:9000";
    }
}
