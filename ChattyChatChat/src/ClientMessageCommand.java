/**
 * Command to send new message received by the Server to a Client.
 * 
 * <br/><br/>
 * 
 * + PreCondition: 
 * The Sever should send an instance of this class to the Server 
 * after specifying the {@link #message} and {@link #sender} fields.
 * 
 * <br/><br/>
 * 
 * + PostCondition: 
 * The Client will call this Command's {@link #execute(ServiceDataProvider)} 
 * method after providing it with a {@link ServiceDataProvider} that contains
 * all the objects needed by the function to perform its task.
 * 
 * <br/><br/>
 * 
 * + {@link ServiceDataProvider} information needed by this command: <br/>
 * (1) ChatClient instance of the running client
 */
public final class ClientMessageCommand implements ICommand {

	private static final long serialVersionUID = 3521954735147953982L;
	
	private final String sender;
	private final String message;
	
	/**
	 * ClientMessageCommand constructor
	 * 
	 * @param senderName  - The Nickname of the sender
	 * @param messageText - The message that should be sent to the server 
	 */
	public ClientMessageCommand(String senderName, String messageText) {
		this.sender  = senderName;
		this.message = messageText;
	}
	
	@Override
	public void execute(ServiceDataProvider serviceProvider) {
		ChatClient client = serviceProvider.get_service(ChatClient.class);
		client.get_standardOutput().println(String.format("%s: %s", this.sender, this.message));
	}
}
