import java.io.IOException;

/**
 * Entry point for Chat Client
 */
final class ChattyChatChatClient {

	public static void main(String[] args) throws IOException, MissingArgumentException, InterruptedException {
		 
		// Make sure host name and port are specified
		if (args.length < 2) {
			System.out.println("Usage: java ChattyChatChatClient <host name> <port number>");
			throw new MissingArgumentException("Hostname or Port");
		}
		
		// Get host name and port number from args
		String hostname = args[0];
		int    port     = Integer.parseInt(args[1]);
		
		// Start Client
		ChatClient client = new ChatClient(hostname, port);
		client.start();
	}
}
