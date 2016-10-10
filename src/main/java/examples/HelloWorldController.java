package examples;

import java.util.Properties;

import de.braintags.netrelay.controller.AbstractController;
import io.vertx.docgen.Source;
import io.vertx.ext.web.RoutingContext;

@Source(translate = false)
public class HelloWorldController extends AbstractController {
  public static final String HELLO_PROPNAME = "helloProperty";

  private String propertyName;

  @Override
  public void handleController(RoutingContext context) {
    context.put(propertyName, "Hello world");
    context.next();
  }

  @Override
  public void initProperties(Properties properties) {
    propertyName = readProperty(HELLO_PROPNAME, null, true);
  }

}
