package pl.tomaszmichalak.example.web.custom;

import io.vertx.core.json.JsonObject;
import pl.tomaszmichalak.example.web.api.ConfigurableHandler;
import pl.tomaszmichalak.example.web.api.ConfigurableHandlerFactory;

public class CustomMessageConfigurableHandlerFactory implements ConfigurableHandlerFactory {

  @Override
  public ConfigurableHandler create(JsonObject config) {
    return new CustomMessageConfigurableHandler(config);
  }
}
