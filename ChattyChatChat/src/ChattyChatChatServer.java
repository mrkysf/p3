import java.io.IOException;

/**
 * Entry point for Chat Server
 */
public final class ChattyChatChatServer {

	public static void main(String[] args) throws IOException, MissingArgumentException {
		
		// Make sure port is specified
		if (args.length == 0) {
			System.out.println("Usage: java ChattyChatChatServer <port number>");
			throw new MissingArgumentException("Port");
		}
		
		// Get port number from args
		int port = Integer.parseInt(args[0]);
		
		// Start Server
		ChatServer server = new ChatServer(port);
		server.start();
	}
}
