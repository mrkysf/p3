
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public final class ServerListener extends Thread {

	private Socket             socket;
	private ObjectInputStream  input;
	private ServiceProvider    serviceProvider;
	
	public ServerListener (Socket socket, ServiceProvider provider) throws IOException {
		this.socket          = socket;
		this.input           = new ObjectInputStream(this.socket.getInputStream());
		this.serviceProvider = new ServiceProvider(provider);
		this.serviceProvider.add_service(ServerListener.class, this);
	}
	
	public Socket get_socket() {
		return this.socket;
	}
	
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
