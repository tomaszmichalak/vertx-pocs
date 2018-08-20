package pl.tomaszmichalak.example.web.common;

import io.vertx.core.json.JsonObject;
import pl.tomaszmichalak.example.web.api.ConfigurableHandlerFactory;

public class CommonMessageConfigurableHandlerFactory implements ConfigurableHandlerFactory {

  @Override
  public CommonMessageConfigurableHandler create(JsonObject config) {
    return new CommonMessageConfigurableHandler(config);
  }
}
