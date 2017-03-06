import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ChatServer {

	private int                  port;
	private List<ClientListener> clients;
	private ServerSocket         serverSocket;
	private ServiceProvider      serviceProvider;
	
	public ChatServer(int portNumber) throws IOException {
		this.port            = portNumber;
		this.clients         = new ArrayList<ClientListener>();
		this.serverSocket    = new ServerSocket(port);
		this.serviceProvider = new ServiceProvider();
		this.serviceProvider.add_service(ChatServer.class, this);
	}
	
	public synchronized void add_client(ClientListener client) {
		clients.add(client);
	}
	
	public synchronized void remove_client(ClientListener client) {
		clients.remove(client);
	}
	
	public List<ClientListener> get_clients() {
		return Collections.synchronizedList(this.clients);
	}

	public void start() throws IOException {
		
		System.out.println(String.format("Server is running on port %d", this.port));
		
		try {
			while (true) {
				// Wait for client connections
				ClientListener connection = new ClientListener(serverSocket.accept(), serviceProvider);
				add_client(connection);
				connection.start();
			}	
		} finally {
			// Server is forced to stop, so close its socket.
			this.stop();
		}
	}

	public void stop() {
		try {
			System.out.println("Stopping ChatServer...");
			if (!serverSocket.isClosed()) {
				
				serverSocket.close();
				
				List<ClientListener> listeners = this.get_clients();
				synchronized(listeners) {
					for (ClientListener listener : listeners) {
						listener.join();
					}
				}
			}
		} 
		catch (IOException e)          {}
		catch (InterruptedException e) {}
	}
}
