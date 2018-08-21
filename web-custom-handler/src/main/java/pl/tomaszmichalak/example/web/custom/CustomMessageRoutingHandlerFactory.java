package pl.tomaszmichalak.example.web.custom;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import pl.tomaszmichalak.example.web.api.RoutingHandlerFactory;

public class CustomMessageRoutingHandlerFactory implements RoutingHandlerFactory {

  @Override
  public String getName() {
    return CustomMessageHandler.NAME;
  }

  @Override
  public Handler<RoutingContext> create(JsonObject config) {
    return new CustomMessageHandler(config);
  }

  private class CustomMessageHandler implements Handler<RoutingContext> {

    private static final String NAME = "customHandler";

    private String message;

    private CustomMessageHandler(JsonObject config) {
      message = config.getString("customMessage");
    }

    @Override
    public void handle(RoutingContext context) {
      JsonArray messages = context.get("messages");
      if (messages == null) {
        messages = new JsonArray();
      }
      messages.add(new JsonObject().put("handlerName", NAME).put("messageValue", message));
      context.put("messages", messages);
      context.next();
    }

  }

}
