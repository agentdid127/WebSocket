package com.agentdid127.websocket.core;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.websocket.api.Endpoint;
import com.agentdid127.websocket.api.IApp;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * Web Socket Server main instance.
 */
public class WebServer extends WebSocketServer {

  // Instance variables
  protected Map<String, Endpoint> endpoints = new LinkedHashMap<>();
  protected Map<String, WebSocket> connections = new LinkedHashMap<>();

  // Main instance
  public static WebServer instance;

  /**
   * Main Server
   * @param address Address of server.
   */
  public WebServer(InetSocketAddress address) {
    super(address);
    instance = this;
  }

  /**
   * Gets all server endpoints.
   * @return All endpoints.
   */
  public Map<String,Endpoint> getEndpoints() {
    return endpoints;
  }

  /**
   * Gets a connection by its name.
   * @param connection The connection.
   * @return A WebSocket connection.
   */
  public WebSocket getConnection(String connection) {
    return connections.get(connection);
  }

  /**
   * Runs when a connection is made to the server.
   * @param webSocket Socket with the connection
   * @param clientHandshake Handshake
   */
  @Override
  public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
    connections.put(webSocket.getRemoteSocketAddress().getHostString(), webSocket);
  }

  /**
   * Runs when a connection is closed.
   * @param webSocket The socket
   * @param i Not sure, see WebSocket documentation.
   * @param s See WebSocket Documentation.
   * @param b See WebSocket Documentation.
   */
  @Override
  public void onClose(WebSocket webSocket, int i, String s, boolean b) {
    connections.remove(webSocket.getRemoteSocketAddress().getHostString());
  }

  /**
   * Runs when a message is received.
   * @param webSocket Socket receiving message.
   * @param message Message received.
   */
  @Override
  public void onMessage(WebSocket webSocket, String message) {
    // Ask to close.
    if (message.equals("close")) {
      connections.remove(webSocket.getRemoteSocketAddress().getHostString());
      webSocket.closeConnection(0, "Closed by User.");
      return;
    }

    // Split message into a header and data
    String[] split = message.split("::");
    String header = split[0];
    String data = "";

    for (int i = 1; i < split.length; i++) {
      data += split[i];
    }

    if (endpoints.containsKey(header)) {
      String response = endpoints.get(header)
          .onMessage(webSocket.getRemoteSocketAddress().getHostString(), data);
      if (response.length() > 0) {
        IApp.instance.sendMessage(webSocket.getRemoteSocketAddress().getHostString(), response);
      }
    }
  }

  /**
   * Handle Error
   * @param webSocket Error Socket
   * @param e Exception thrown
   */
  @Override
  public void onError(WebSocket webSocket, Exception e) {
    Logger.getLogger().trace(e.getMessage(), e);
    connections.remove(webSocket.getRemoteSocketAddress().getHostString());
    webSocket.closeConnection(1, e.getMessage());
  }

  /**
   * Runs on server start.
   */
  @Override
  public void onStart() {

  }
}
