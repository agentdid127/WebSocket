package com.agentdid127.websocket.client;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.MessageHandler;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Main WebSocket Client
 */
public class WSClient {

  private String url;

  private ArrayList<String> requests;

  private Consumer<String> onResponse;

  private WSInternalClient client;

  /**
   * Constructor
   * @param url URL of Connection
   * @param onResponse Response handler
   */
  public WSClient(String url, Consumer<String> onResponse) {
    this.url = url;
    this.requests = new ArrayList<>();
    this.onResponse = onResponse;
    try {
      this.client = new WSInternalClient(url, new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
          onResponse.accept(message);
        }
      }, (session) -> {
        if (requests.size() > 0) {
          String req = requests.remove(0);
          session.getAsyncRemote().sendText(req);
        }
      });
    } catch (DeploymentException | URISyntaxException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends a message to the server.
   * @param loc Location to send, In format of Plugin.Handler
   * @param message Message to send to the server.
   */
  public void sendMessage(String loc, String message) {
    String toSend = loc + "::" + message;
    requests.add(toSend);
  }

  /**
   * Close the connection.
   */
  public void close() {
    client.close();
    requests.clear();
  }

  /**
   * Restarts the connection from scratch
   * @throws DeploymentException
   * @throws URISyntaxException
   */
  public void restart() throws DeploymentException, URISyntaxException {
    requests.clear();
    client.restart();
  }

  /**
   * Sets the main response handler.
   * @param responseHandler Response handler.
   */
  public void setResponseHandler(Consumer<String> responseHandler) {
    this.onResponse = responseHandler;
  }

  /**
   * Retrieves the URL
   * @return URL.
   */
  public String getUrl() {
    return url;
  }

  /**
   * Returns if the client is connected.
   * @return {@code true} if the client is connected.
   */
  public boolean isRunning() {
    return client.isRunning();
  }
}
