package reqres.config;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:config.properties")
public interface ApiConfig extends Config {

    @Key("baseUri")
    String baseApiUri();

    @Key("basePath")
    @DefaultValue("/api")
    String basePath();
}
