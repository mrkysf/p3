/**
 * Command to send client's new Nickname to Server.
 * 
 * <br/><br/>
 * 
 * + PreCondition: 
 * The Client should send an instance of this class to the Server 
 * after specifying its {@link #newName} field.
 * 
 * <br/><br/>
 * 
 * + PostCondition: 
 * The Server will call this Command's {@link #execute(ServiceDataProvider)} 
 * method after providing it with a {@link ServiceDataProvider} that contains
 * all the objects needed by the function to perform its task.
 * 
 * <br/><br/>
 * 
 * + {@link ServiceDataProvider} information needed by this command: <br/>
 * (1) ClientListener instance of the client who sent the message
 */
public final class ServerNameChangeCommand implements ICommand {

	private static final long serialVersionUID = 6419127542397490920L;

	private final String newName;
	
	/**
	 * ServerNameChangeCommand constructor
	 * 
	 * @param name - Client's new Nickname
	 */
	public ServerNameChangeCommand(String name) {
		this.newName = name;
	}
	
	@Override
	public void execute(ServiceDataProvider serviceProvider) {
		ClientListener client = serviceProvider.get_service(ClientListener.class);
		client.set_name(this.newName);
	}
}