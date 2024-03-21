package com.agentdid127.websocket.core;

import com.agentdid127.converter.core.PluginLoader;
import com.agentdid127.converter.iface.IPluginLoader;
import com.agentdid127.converter.util.Logger;
import com.agentdid127.date.unix.UnixFormat;
import com.agentdid127.date.unix.UnixSupportedDate;
import com.agentdid127.date.unix.UnixTimestamp;
import com.agentdid127.websocket.api.Endpoint;
import com.agentdid127.websocket.api.IApp;
import com.agentdid127.websocket.api.WSPlugin;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import joptsimple.OptionSet;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;

/**
 * Main Application Instance.
 */
public class App extends IApp {

  // Plugin Loader
  private IPluginLoader<WSPlugin> pluginLoader;

  /**
   * Constructs an instance of the App.
   * @param pluginLoader Plugin Loader for the app.
   */
  public App(IPluginLoader<WSPlugin> pluginLoader) {
    this.pluginLoader = pluginLoader;
  }

  /**
   * Main application method
   * @param args Command line arguments.
   * @throws IOException
   * @throws InterruptedException
   */
  public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {

    // Parse Command-line options
    OptionSet optionSet = Options.PARSER.parse(args);

    // Handle help
    if (optionSet.has(Options.HELP)) {
      Options.PARSER.printHelpOn(System.out);
      return;
    }

    //Create Logs
    Path logsPath = Paths.get("./logs/");
    if (!logsPath.toFile().exists()) {
      logsPath.toFile().mkdirs();
    }

    if (logsPath.resolve("latest.log").toFile().exists()) {
      UnixSupportedDate date = UnixTimestamp.current().toDate();
      String dateString = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
      int iter = 0;
      while (logsPath.resolve(dateString + "-" + iter + ".log").toFile().exists()) {
        iter++;
      }
      logsPath.resolve("latest.log").toFile().renameTo(logsPath.resolve(dateString + "-" + iter + ".log").toFile());
    }

    logsPath.resolve("latest.log").toFile().createNewFile();

    // Gather host and port of server.
    String host = optionSet.valueOf(Options.HOST);
    int port = optionSet.valueOf(Options.PORT);
    InetSocketAddress address = new InetSocketAddress(host, port);

    // Enable Logging
    PropertyConfigurator.configure(App.class.getResourceAsStream("/log4j.properties"));
    org.slf4j.Logger logger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    Logger.setLogger(logger);

    // Load Plugins
    logger.info("Loading Libraries...");
    Path pluginsPath = Paths.get("./plugins/");

    // If no plugins are found, Close the app.
    if (!pluginsPath.toFile().exists()) {
      pluginsPath.toFile().mkdirs();
      Logger.error("No Plugins Found. Exiting...");
      return;
    }

    // Start setting up to load plugins
    Logger.log("Loading Plugins...");
    PluginLoader<WSPlugin> pluginLoader = new PluginLoader(pluginsPath.toFile(), WSPlugin.class,
        Arrays.asList("com.agentdid127.websocket.core",
            "com.agentdid127.websocket.api", "com.agentdid127.converter", "org.slf4j"));

    // Create the application instance and load the plugins.
    App app = new App(pluginLoader);
    IApp.instance = app;
    pluginLoader.loadPlugins();


    // Create a new WebServer
    new WebServer(address);

    // Initialize Server Plugins.
    app.initPlugins();

    // Start the server.
    Logger.log("Starting Server...");
    WebServer.instance.start();
    Logger.log("Server Started.");


    // Handle Commands
    boolean run = true;
    Scanner in = new Scanner(System.in);
    while (run) {
      String cmd = in.nextLine();
      String[] split = cmd.split(" ");

      if (split[0].equalsIgnoreCase("stop")) {
        // Stop Command
        WebServer.instance.stop(0, "Server Stopped.");
        run = false;
      } else if (split[0].equalsIgnoreCase("help")) {
        // Help Command
        logger.info("HELP: ");
        logger.info(" 1.) help        - Print this message");
        logger.info(" 2.) stop        - Stop the server");
        logger.info(" 3.) reload      - Reload Plugins");
        logger.info(" 4.) connections - Gets server connections");
      } else if (split[0].equalsIgnoreCase("reload")) {
        app.reloadPlugins();
      } else if (split[0].equalsIgnoreCase("connections")) {
        if (split.length > 1) {
          for (int i = 1; i < split.length; i++) {
            if (WebServer.instance.getConnection(split[i]) != null) {
              InetSocketAddress conn = WebServer.instance.getConnection(split[i])
                  .getRemoteSocketAddress();
              logger.info(conn.getHostName() + "@" + conn.getHostString() + ":" + conn.getPort());
            }
          }
        } else {
          List<String> conn = WebServer.instance.getConnectionNames();

          for (String s : conn) {
            InetSocketAddress conn2 = WebServer.instance.getConnection(s).getRemoteSocketAddress();
            logger.info(conn2.getHostName() + "@" + conn2.getHostString() + ":" + conn2.getPort());
          }
        }
      }
    }

    // Unload server plugins on stop.
    app.unloadPlugins();

    // Close plugins
    logger.info("Server Closed.");
  }

  /**
   * Gets the Server Plugin Loader.
   * @return The plugin loader.
   */
  @Override
  public IPluginLoader getPluginLoader() {
    return pluginLoader;
  }

  /**
   * Sends a message to the socket given.
   * @param socket Socket to send the message to.
   * @param message Message to send to the client.
   * @return Always 0.
   */
  @Override
  public int sendMessage(String socket, String message) {
    WebServer.instance.getConnection(socket).send(message);
    return 0;
  }

  /**
   * Unloads all server plugins.
   */
  public void unloadPlugins() {
    // Unload plugins on stop
    for (WSPlugin pl : pluginLoader.getPlugins().values()) {
      for (Endpoint runner : pl.getRunners()) {
        String endpointName = pl.getName() + "." + runner.getName();
        WebServer.instance.getEndpoints().remove(endpointName);
      }
      Logger.log("Unloading: " + pl.getName());
      pl.onUnload();
    }

  }

  /**
   * Initializes all Plugins.
   */
  public void initPlugins() {
    pluginLoader.getPlugins().forEach((name, plugin) -> {
      plugin.setApplication(IApp.instance);
      plugin.onInit();
      for (Endpoint endpoint : plugin.getRunners()) {
        String endpointName = name + "." + endpoint.getName();
        if (!WebServer.instance.getEndpoints().containsKey(endpointName)) {
          WebServer.instance.getEndpoints().put(name + "." + endpoint.getName(), endpoint);
        } else {
          Logger.error("Could not add Endpoint: " + endpointName);
        }
      }
    });
  }

  /**
   * Reloads all plugins.
   */
  public void reloadPlugins() {
    unloadPlugins();
    initPlugins();
  }
}
