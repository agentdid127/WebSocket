package com.agentdid127.websocket.core;

import java.util.Arrays;
import joptsimple.OptionParser;
import joptsimple.OptionSpec;

/**
 * Command Line Argument Handler.
 */
public class Options {

  // Option Parser
  public static final OptionParser PARSER = new OptionParser();

  // Help
  public static final OptionSpec<Void> HELP = PARSER.acceptsAll(Arrays.asList("?", "h", "help"), "Print this message.").forHelp();

  // Server Port
  public static final OptionSpec<Integer> PORT = PARSER.acceptsAll(Arrays.asList("p", "port"), "Server Port").withRequiredArg().ofType(Integer.class).defaultsTo(8080);

  // Server Host
  public static OptionSpec<String> HOST = PARSER.acceptsAll(Arrays.asList("h", "host"), "Server Host").withRequiredArg().ofType(String.class).defaultsTo("localhost");

}
