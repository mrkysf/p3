import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

public final class ChatClient {

	private String             hostname;
	private int                port;
	private Socket             clientSocket;
	private ObjectOutputStream socketOutput;
	private ServerListener     serverListener;
	private Scanner            standardInput;
	private PrintStream        standardOutput;
	private ServiceProvider    serviceProvider;
	
	public ChatClient(String host, int portNumber) throws UnknownHostException, IOException {
		this.hostname        = host;
		this.port            = portNumber;
		this.standardInput   = new Scanner(System.in);
		this.standardOutput  = System.out;
		this.clientSocket    = new Socket(this.hostname, this.port);
		this.socketOutput    = new ObjectOutputStream(this.clientSocket.getOutputStream());
		this.serviceProvider = new ServiceProvider();
		this.serviceProvider.add_service(ChatClient.class, this);
		this.serverListener  = new ServerListener(this.clientSocket, this.serviceProvider);
	}
	
	public PrintStream get_standardOutput() {
		return this.standardOutput;
	}
	
	public void start() throws IOException, InterruptedException {
		try {
			// Start ServerListener on separate thread to listen for commands 
			// coming from server	
			this.serverListener.start();
		
			while (standardInput.hasNext()) {
				String commandText = standardInput.nextLine();
				
				// Stop if the user input the QUIT command
				if (ChatCommands.convert(commandText).equals(ChatCommands.QUIT)) {
					break;
				}
				
				// Otherwise, initialize the command and send it to the server
				ICommand cmd = InitializeCommand(commandText);
				if (cmd != null) {
					this.socketOutput.writeObject(cmd);
				}
			}	
		} finally {
			// Client is done, so close its socket.
			this.stop();
			serverListener.join();
		}
	}

	public void stop() {
		try {
			if (!this.clientSocket.isClosed()) {
				System.out.println("Closing client socket...");
				this.socketOutput.close();
				this.clientSocket.getInputStream().close();
				this.clientSocket.close();
			}
		} catch (IOException ex) {
			// No need to do anything else here.
		}
	}
	
	private static ICommand InitializeCommand(String commandText) {
		ChatCommands commandType = ChatCommands.convert(commandText);
		List<String> commandArgs = ChatCommands.get_args(commandText);
		ICommand     command     = null;
		
		switch(commandType) {
			case CHANGE_NICKNAME:
			{
				command = new ServerNameChangeCommand(commandArgs.get(0));
				break;
			}
			case DIRECT_MESSAGE:
			{
				command = new ServerMessageCommand(commandArgs.get(1), commandArgs.get(0));				
				break;
			}
			case QUIT:
			{
				// This case should be handled directly
				// by the main function. So, return null.
				break;
			}
			default:
			{
				// Broadcast Message
				command = new ServerMessageCommand(commandText, null);
			}
		}
		
		return command;
	}
}
