import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class MultiThreadedServer 
{
	private static HashSet<String> names = new HashSet<String>();

	private static HashSet<ObjectOutputStream> OutputStreams = new HashSet<ObjectOutputStream>();

	private static ServerSocket server;

	public static void main(String[] args) throws Exception 
	{
		try {
			server = new ServerSocket(9129);

			System.out.println("Server is running.");
			while (true) 
			{
				Socket socket = server.accept();
				new ThreadedSocket(socket).start();
			}

		} 
		catch(IOException e) 
		{ 
			e.printStackTrace();
		}	
	} 

	 private static class ThreadedSocket extends Thread {
		private String name;
		private Socket socket;
		ObjectInputStream input; 
		ObjectOutputStream output;

		ThreadedSocket(Socket socket)
		{
			this.socket = socket;
		}

		/**
		 * Services this thread's client by repeatedly requesting a
		 * screen name until a unique one has been submitted, then
		 * acknowledges the name and registers the output stream for
		 * the client in a global set, then repeatedly gets inputs and
		 * broadcasts them.
		 */
		public void run() 
		{
			boolean runThread = true;
			try {
				input = new ObjectInputStream(socket.getInputStream());

				output = new ObjectOutputStream(socket.getOutputStream());

				// Request a name from this client.  Keep requesting until
				// a name is submitted that is not already used.  Note that
				// checking for the existence of a name and adding the name
				// must be done while locking the set of names.
				while (runThread) 
				{
					output.writeObject("Enter Your Nickname:");
					name = (String) input.readObject();
					//                    input.close();
					//                    output.close();
					if (name == null) 
					{
						name = "anonymous";
					}
					synchronized (names) 
					{
						if (!names.contains(name)) 
						{
							names.add(name);
							break;
						}
					}
				}

				if (name == null) 
				{
					name = "anonymous";
				}
				// Now that a successful name has been chosen, add the
				// socket's print writer to the set of all writers so
				// this client can receive broadcast messages.
				output.writeObject("Your username is: " + name);
				OutputStreams.add(output);

				// Accept messages from this client and broadcast them.
				// Ignore other clients that cannot be broadcasted to.
				while (runThread) 
				{
					String message = (String) input.readObject();
					if (message == null) 
					{
						return;
					}
					else if(message.startsWith("/quit")) 
						{
							runThread = false;
						};
			        
					for (ObjectOutputStream objectOutputStream : OutputStreams) 
					{
						objectOutputStream.writeObject(name + ": " + message);
					}
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			} 
			finally 
			{
				try 
				{
				if (name != null) 
				{
					names.remove(name);
				}
				if (output != null) 
				{
					OutputStreams.remove(output);
				}
                  input.close();
                  output.close();
				  socket.close();
				  System.out.println("Session has ended."); 
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}
