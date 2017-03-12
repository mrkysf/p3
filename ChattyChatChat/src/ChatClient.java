import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

/**
 * ChattyChatChat Client Implementation
 */
public final class ChatClient {

	private String              hostname;
	private int                 port;
	private Socket              clientSocket;
	private ObjectOutputStream  socketOutput;
	private ServerListener      serverListener;
	private Scanner             standardInput;
	private PrintStream         standardOutput;
	private ServiceDataProvider serviceProvider;
	
	public ChatClient(String host, int portNumber) throws UnknownHostException, IOException {
		this.hostname        = host;
		this.port            = portNumber;
		this.standardInput   = new Scanner(System.in);
		this.standardOutput  = System.out;
		this.clientSocket    = new Socket(this.hostname, this.port);
		this.socketOutput    = new ObjectOutputStream(this.clientSocket.getOutputStream());
		this.serviceProvider = new ServiceDataProvider();
		this.serviceProvider.add_service(ChatClient.class, this);
		this.serverListener  = new ServerListener(this.clientSocket, this.serviceProvider);
	}
	
	public PrintStream get_standardOutput() {
		return this.standardOutput;
	}
	
	/**
	 * Starts the {@link #serverListener} thread to listen for commands coming
	 * from the Server, and then, it waits on standard input for user input.
	 * Once the user inputs a message/command, it parses it and sends out the
	 * necessary command to the Server.
	 * 
	 * @throws IOException
	 *             Any exception thrown by the {@link #socketOutput} stream.
	 * @throws InterruptedException
	 *             Any thread has interrupted the {@link #serverListener}
	 *             thread.
	 */
	public void start() throws IOException, InterruptedException {
		try {
			// Start ServerListener on separate thread to listen for commands 
			// coming from server	
			this.serverListener.start();
		
			System.out.println(String.format("Connected to server on '%s' machine at port %d",
					           this.hostname, this.port));
			
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

	/**
	 * Closes {@link #clientSocket}, if it is still open, along with all its
	 * streams.
	 */
	public synchronized void stop() {
		try {
			if (!this.clientSocket.isClosed()) {
				System.out.println("Closing client socket...");
				this.socketOutput.close();
				this.clientSocket.getInputStream().close();
				this.clientSocket.close();
			}
		} catch (IOException ex) {}
	}
	
	/**
	 * Parses the {@code commandText} and returns the corresponding command that
	 * needs to be sent to the server.
	 *
	 * @param commandText
	 *            - Message/Command input by the user
	 * 
	 * @return {@link ICommand} or null if the {@code commandText} corresponds
	 *         to the QUIT user command.
	 */
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
				// by the start() function. So, return null.
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
