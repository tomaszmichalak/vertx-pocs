package pl.tomaszmichalak.example.web.stack;

import pl.tomaszmichalak.example.web.ConfigurableServer;
import pl.tomaszmichalak.example.web.Runner;

public class App {

  public static void main(String[] args) {
    System.setProperty("org.vertx.logger-delegate-factory-class-name",
        "org.vertx.java.core.logging.impl.SLF4JLogDelegateFactory");
    Runner.runExample(ConfigurableServer.class);
  }

}
