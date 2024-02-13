package com.agentdid127.websocket.example;

import com.agentdid127.websocket.client.WSClient;

/**
 * Example Client Usage
 */
public class App {

  /**
   * Example Client
   * @param args Args
   * @throws InterruptedException if the Thread interrrupts
   */
  public static void main(String[] args) throws InterruptedException {

    // Create the client
    WSClient client = new WSClient("ws://localhost:8080", (message) -> {
        System.out.println(message);
    });

    // Send 5 Hello World Messages.
    int test = 0;
    while (client.isRunning() && test <= 5) {
      Thread.sleep(1000);
      client.sendMessage("ExamplePlugin.HelloWorld", "Hello there!");
      test++;
    }
  }
}
