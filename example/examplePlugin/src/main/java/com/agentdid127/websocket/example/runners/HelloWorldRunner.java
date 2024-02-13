package com.agentdid127.websocket.example.runners;

import com.agentdid127.websocket.api.Endpoint;
import com.agentdid127.websocket.example.ExamplePlugin;

/**
 * Hello World Endpoint
 */
public class HelloWorldRunner extends Endpoint {

  private ExamplePlugin examplePlugin;

  /**
   * Main Runner
   * @param examplePlugin Plugin
   */
  public HelloWorldRunner(ExamplePlugin examplePlugin) {
    super("HelloWorld", 1);
    this.examplePlugin = examplePlugin;
  }

  /**
   * Message Handler
   * @param client Client's Connection Name
   * @param message Message sent to client.
   * @return Hello World Message
   */
  @Override
  public String onMessage(String client, String message) {
    examplePlugin.getLogger().info(client + " " + message);
    return "Hello, World!";
  }
}
