package com.agentdid127.websocket.api;

import com.agentdid127.converter.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocket Plugin.
 */
public abstract class WSPlugin extends Plugin<Endpoint> {

  // Plugin Logger
  private Logger logger;

  /**
   * Constructs a WebSocket Plugin.
   * @param name Name of Plugin.
   */
  public WSPlugin(String name) {
    super(name, "WSPlugin");
    this.logger = LoggerFactory.getLogger(name);
  }

  /**
   * Gets the current Logger for the plugin to access.
   * @return
   */
  public Logger getLogger() {
    return logger;
  }
}
