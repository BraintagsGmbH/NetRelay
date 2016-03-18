package examples;

import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.init.Settings;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.docgen.Source;

@Source(translate = false)
public class Main extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    String settingsPath = "src/main/resources/";
    String settingsFile = settingsPath + "fairytale-settings.json";
    DeploymentOptions options = new DeploymentOptions();
    options.setConfig(new JsonObject().put(Settings.SETTINGS_LOCATION_PROPERTY, settingsFile));
    vertx.deployVerticle(NetRelay.class.getName(), options, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        startFuture.fail(result.cause());
      } else {
        LOGGER.info(NetRelay.class.getSimpleName() + " successfully launched: " + result.result());
      }
    });
  }
}
