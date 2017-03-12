
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Implementation of Server Listener thread that will listen
 * for Server commands and executes them on the Client.
 */
public final class ServerListener extends Thread {

	private Socket              socket;
	private ObjectInputStream   input;
	private ServiceDataProvider serviceProvider;
	
	public ServerListener (Socket socket, ServiceDataProvider provider) throws IOException {
		this.socket          = socket;
		this.input           = new ObjectInputStream(this.socket.getInputStream());
		this.serviceProvider = new ServiceDataProvider(provider);
		this.serviceProvider.add_service(ServerListener.class, this);
	}
	
	public Socket get_socket() {
		return this.socket;
	}
	
	/**
	 * Listens on the client socket for incoming commands from the server. Once
	 * it receives a command, it executes it on the client.
	 */
	@Override
	public void run() {
		try {
			while(true) {
				ICommand command = (ICommand) this.input.readObject();
				
				if (command != null) {
					command.execute(this.serviceProvider);
				}
			}
		} catch (ClassNotFoundException e) {
			// This exception should never be hit if all Commands
			// are implemented correctly.
			System.out.println(String.format("Failed to deserialize command.\nException: %s", e.getMessage()));
			e.printStackTrace();
		} catch (IOException e) {
			// The server input stream was terminated, so handle it 
			// gracefully and exit the client.
			System.out.println("Disconnected from the server. Exiting...");
		} finally {
			ChatClient client = this.serviceProvider.get_service(ChatClient.class);
			client.stop();
			System.exit(0);
		}
	}
}
