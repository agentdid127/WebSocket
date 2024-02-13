package com.agentdid127.websocket.api;

import com.agentdid127.converter.Runner;

/**
 * Determines a Messaging Endpoint
 */
public abstract class Endpoint extends Runner {

  /**
   * Constructs a Messaging Endpoint
   * @param name Name of Endpoint
   * @param priority Priority of endpoint
   */
  public Endpoint(String name, int priority) {
    super(name, priority);
  }

  /**
   * Message Handler
   * @param client Client's Connection Name
   * @param message Message sent to client.
   * @return The Message to respond to the client.
   */
  public abstract String onMessage(String client, String message);

}
