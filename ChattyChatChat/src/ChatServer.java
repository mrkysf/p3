import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ChattyChatChat Server Implementation
 */
public final class ChatServer {

	private int                  port;
	private List<ClientListener> clients;
	private ServerSocket         serverSocket;
	private ServiceDataProvider  serviceProvider;
	
	public ChatServer(int portNumber) throws IOException {
		this.port            = portNumber;
		this.clients         = Collections.synchronizedList(new ArrayList<ClientListener>());
		this.serverSocket    = new ServerSocket(port);
		this.serviceProvider = new ServiceDataProvider();
		this.serviceProvider.add_service(ChatServer.class, this);
	}
	
	public synchronized void add_client(ClientListener client) {
		clients.add(client);
	}
	
	public synchronized void remove_client(ClientListener client) {
		clients.remove(client);
	}
	
	public List<ClientListener> get_clients() {
		return this.clients;
	}

	/**
	 * Listens on the server socket for client connections, and starts a separate
	 * {@link ClientListener} thread for each incoming connection.
	 * 
	 * @throws IOException
	 *             An exception is thrown by the {@link ServerSocket#accept()}
	 *             function.
	 */
	public void start() throws IOException {
		
		System.out.println(String.format("Server is running on port %d", this.port));
		
		try {
			while (true) {
				// Wait for client connections
				ClientListener connection = new ClientListener(serverSocket.accept(), this.serviceProvider);
				add_client(connection);
				connection.start();
			}	
		} finally {
			// Server is forced to stop, so close its socket.
			this.stop();
		}
	}

	/**
	 * Closes {@link #serverSocket}, if it is still open; then, it aborts
	 * all {@link #clients} and waits for their threads to die.
	 */
	public void stop() {
		try {
			System.out.println("Stopping ChatServer...");
			if (!this.serverSocket.isClosed()) {
				this.serverSocket.close();
				List<ClientListener> listeners = this.get_clients();
				
				synchronized(listeners) {
					for (ClientListener listener : listeners) {
						if (!listener.isInterrupted()) {
							listener.interrupt();
							listener.join(1000);
						}
					}
				}
			}
		} 
		catch (IOException e)          {e.printStackTrace();}
		catch (InterruptedException e) {e.printStackTrace();}
	}
}
