import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Implementation of Client Listener thread that will listen
 * for Client requests on the Server.
 */
public final class ClientListener extends Thread {

	private String              name;
	private Socket              socket;
	private ObjectInputStream   input;
	private ObjectOutputStream  output;
	private ServiceDataProvider serviceProvider;
	
	private static final String DEFAULT_NAME = "anonymous";
	
	public ClientListener (Socket socket, ServiceDataProvider provider) throws IOException {
		this.name            = DEFAULT_NAME;
		this.socket          = socket;
		this.input           = new ObjectInputStream(this.socket.getInputStream());
		this.output          = new ObjectOutputStream(this.socket.getOutputStream());
		this.serviceProvider = new ServiceDataProvider(provider);
		this.serviceProvider.add_service(ClientListener.class, this);
	}
	
	public String get_name() {
		return this.name;
	}
	
	public void set_name(String name) {
		this.name = name;
	}
	
	/**
	 * Listens on the client socket for incoming commands. Once it receives a
	 * command, it executes it on the server.
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
			// The client input stream was terminated, so handle it 
			// gracefully and exit the thread to avoid bugging down the
			// server with exceptions.
			ChatServer server = this.serviceProvider.get_service(ChatServer.class);
			server.remove_client(this);
		}
	}
	
	/**
	 * Sends the provided command to the Client.
	 * 
	 * @param cmd - Command that should be sent to the Client
	 */
	public synchronized void send(ICommand cmd) {
		try {
			if (cmd != null) {
				this.output.writeObject(cmd);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
