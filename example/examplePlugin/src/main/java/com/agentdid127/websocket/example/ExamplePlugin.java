package com.agentdid127.websocket.example;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.websocket.api.WSPlugin;
import com.agentdid127.websocket.example.runners.HelloWorldRunner;

/**
 * Example Plugin
 */
public class ExamplePlugin extends WSPlugin {

  /**
   * Example Plugin Constructor
   */
  public ExamplePlugin() {
    super("ExamplePlugin");
  }

  /**
   * Runs when the plugin is loaded.
   */
  @Override
  public void onLoad() {
    getRunners().clear();
    getLogger().info("ExamplePlugin loaded...");
  }

  /**
   * Runs when the plugin is initialized.
   */
  @Override
  public void onInit() {
    getRunners().add(new HelloWorldRunner(this));
    getLogger().warn("ExamplePlugin Initialized.");
  }

  /**
   * Runs when the plugin is unloaded.
   */
  @Override
  public void onUnload() {
    getRunners().clear();
    getLogger().info("ExamplePlugin unloaded.");
  }
}
