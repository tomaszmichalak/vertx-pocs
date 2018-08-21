package pl.tomaszmichalak.example.web.common;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import pl.tomaszmichalak.example.web.api.RoutingHandlerFactory;

public class CommonMessageRoutingHandlerFactory implements RoutingHandlerFactory {

  @Override
  public String getName() {
    return CommonMessageHandler.NAME;
  }

  @Override
  public Handler<RoutingContext> create(JsonObject config) {
    return new CommonMessageHandler(config);
  }

  private class CommonMessageHandler implements Handler<RoutingContext> {

    private static final String NAME = "commonHandler";

    private String message;

    CommonMessageHandler(JsonObject config) {
      message = config.getString("commonMessage");
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
