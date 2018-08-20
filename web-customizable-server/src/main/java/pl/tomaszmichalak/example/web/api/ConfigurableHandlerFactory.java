package pl.tomaszmichalak.example.web.api;

import io.vertx.core.json.JsonObject;

public interface ConfigurableHandlerFactory {

  ConfigurableHandler create(JsonObject config);

}
