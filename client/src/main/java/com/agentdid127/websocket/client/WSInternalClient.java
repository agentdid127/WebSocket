package com.agentdid127.websocket.client;

import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import org.glassfish.tyrus.client.ClientManager;

/**
 * Internal WebSocket Client.
 */
public class WSInternalClient {

  private ClientManager client;
  private final String url;
  private final MessageHandler handler;
  private final Consumer<Session> messageSender;
  private Thread requestThread;

  /**
   * Main Constructor
   *
   * @param url URL of connection
   * @param handler Message Handler
   * @param messageSender Tool to send messages
   * @throws DeploymentException If the deployment fails.
   * @throws URISyntaxException if the URI is bad
   */
  WSInternalClient(String url, MessageHandler handler, Consumer<Session> messageSender) throws DeploymentException, URISyntaxException {
    this.url = url;
    this.handler = handler;
    this.messageSender = messageSender;
    init();
  }

  /**
   * Initializes the WebSocketServer.
   *
   * @throws URISyntaxException if the URI is bad
   * @throws DeploymentException if the deployment fails.
   */
  private void init() throws URISyntaxException, DeploymentException {
      final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

      client = ClientManager.createClient();

      client.asyncConnectToServer((new Endpoint() {
        @Override
        public void onOpen(Session session, EndpointConfig endpointConfig) {

          session.addMessageHandler(handler);
          requestThread = new Thread(() -> {
            while (isRunning()) {
              messageSender.accept(session);
            }
          });
          requestThread.start();


        }
      }), cec, new URI(url));

  }

  /**
   * Closes the server.
   */
  public void close() {
    requestThread.interrupt();
    client.getScheduledExecutorService().shutdown();
    client.getExecutorService().shutdown();
  }

  /**
   * Restarts the server
   *
   * @throws DeploymentException If the deployment fails
   * @throws URISyntaxException If the syntax is bad.
   */
  public void restart() throws DeploymentException, URISyntaxException {
    close();
    init();
  }

  /**
   * Checks if the server is running
   *
   * @return {@code true} if the service is still running. {@code false} otherwise.
   */
  public boolean isRunning() {
    return !(client.getExecutorService().isShutdown() || client.getExecutorService().isTerminated());
  } // isRunning
}
