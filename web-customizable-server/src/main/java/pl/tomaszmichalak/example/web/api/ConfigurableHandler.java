package pl.tomaszmichalak.example.web.api;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public interface ConfigurableHandler extends Handler<RoutingContext>, Cloneable {

  ConfigurableHandler register(Router router, JsonObject routeConfig);

  default Route route(Router router, JsonObject routeConfig) {
    Route route = router.route();
    if (routeConfig != null) {
      String pathRegex = routeConfig.getString("pathRegex");
      // only one path is supported per route
      String path = routeConfig.getString("path");
      JsonArray methods = routeConfig.getJsonArray("methods");
      JsonArray consumesMIMETypes = routeConfig.getJsonArray("consumesMIMETypes");
      JsonArray producesMIMETypes = routeConfig.getJsonArray("producesMIMETypes");
      Boolean last = routeConfig.getBoolean("last");
      Integer order = routeConfig.getInteger("order");

      if (pathRegex != null) {
        route.pathRegex(pathRegex);
      } else {
        if (path != null) {
          route.path(path);
        }
      }
      if (methods != null) {
        methods.forEach(method ->
            route.method(HttpMethod.valueOf(method.toString()))
        );
      }
      if (consumesMIMETypes != null) {
        consumesMIMETypes.forEach(contentType ->
            route.consumes(contentType.toString())
        );
      }
      if (producesMIMETypes != null) {
        producesMIMETypes.forEach(contentType ->
            route.produces(contentType.toString())
        );
      }
      if (last != null && last) {
        route.last();
      } else if (order != null) {
        route.order(order);
      }
    }
    return route;
  }

}
