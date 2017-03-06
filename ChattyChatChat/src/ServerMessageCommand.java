import java.util.List;

/**
 * Command to send new client message to Server.
 * 
 * <br/><br/>
 * 
 * + PreCondition: 
 * The Client should send an instance of this class to the Server 
 * after specifying the {@link #message} and {@link #recipient} fields.
 * 
 * <br/><br/>
 * 
 * + PostCondition: 
 * The Server will call this Command's {@link #execute(ServiceProvider)} 
 * method after providing it with a {@link ServiceProvider} that contains
 * all the objects needed by the function to perform its task.
 * 
 * <br/><br/>
 * 
 * + {@link ServiceProvider} information needed by this command: <br/>
 * (1) ChatServer instance of the running server                 <br/>
 * (2) ClientListener instance of the client who sent the message
 */
public final class ServerMessageCommand implements ICommand {

	private static final long serialVersionUID = 8381604433324171629L;
	
	private String message;
	private String recipient;
	
	/**
	 * ServerMessageCommand constructor
	 * 
	 * @param message    - The message that should be sent to the server 
	 * @param recipients - The Nickname of the recipient. If this field is null
	 *                     or empty, the Server will broadcast the message to all
	 *                     available clients.
	 */
	public ServerMessageCommand(String message, String recipients) {
		this.message   = message;
		this.recipient = recipients;
	}

	@Override
	public void execute(ServiceProvider serviceProvider) {

		// Get Server and Client objects
		ChatServer     server = serviceProvider.get_service(ChatServer.class);
		ClientListener client = serviceProvider.get_service(ClientListener.class);
		
		// Get sender client name
		String senderClientName = client.get_name();
		
		// Create message command that will be send to clients
		ICommand cmd = new ClientMessageCommand(senderClientName, this.message);
		
		// Get all clients
		List<ClientListener> allClients = server.get_clients();
		
		synchronized(allClients) {
			if (StringHelper.isNullOrEmpty(this.recipient)) {
				// Broadcast
				for (ClientListener conn : allClients) {
					if (conn != client) {
						conn.send(cmd);
					}
				}
			} else {
				// Send message to recipient(s)
				for (ClientListener conn : allClients) {
					if (conn != client && conn.get_name().equals(this.recipient)) {
						conn.send(cmd);
					}
				}
			}
		}
 	}
}
