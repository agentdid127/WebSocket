package com.agentdid127.websocket.api;

import com.agentdid127.converter.iface.Application;

import java.net.InetSocketAddress;

/**
 * Internal WebSocket Application.
 */
public abstract class IApp implements Application {

  // Instance of The application
  public static IApp instance;

  public abstract InetSocketAddress getAddress();

  /**
   * Sends a message to a client.
   * @param client Client to send the message to.
   * @param message Message to send to the client.
   * @return 0 if it was successful, otherwise a positive integer.
   */
  public abstract int sendMessage(String client, String message);

}
