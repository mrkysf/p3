import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class CCCServer 
{
	private static HashSet<String> names = new HashSet<String>();

	private static HashSet<ObjectOutputStream> OutputStreams = new HashSet<ObjectOutputStream>();

	private static ServerSocket server;

	private static int numThreads = 100; 

	private static final ThreadedSocket[] threads = new ThreadedSocket[numThreads];

	public static void main(String[] args) throws Exception 
	{
		try
		{
			//			if(args.length < 1)
			//			{
			//				System.out.println("No port number entered. Now exiting.");
			//				System.exit(-1);
			//			}
			//			
			//			int portNumber = Integer.parseInt(args[0]);
			int portNumber = 7777;

			server = new ServerSocket(portNumber);

			System.out.println("Server is running.");
			while (true) 
			{
				int emptyThreadIndex = 0;
				boolean foundEmptyThread = false;
				Socket socket = server.accept();
				for(int i=0; i < numThreads; i++)
				{
					//.isAlive()
					if (threads[i]==null && !foundEmptyThread)
					{
						emptyThreadIndex = i;
						foundEmptyThread = true;
					}
				}
				if(foundEmptyThread)
					(threads[emptyThreadIndex] = new ThreadedSocket(socket)).start();
				else //reach maxed num threads
					socket.close();	
			}

		} 
		catch(IOException e) 
		{ 
			e.printStackTrace();
		}	
		finally
		{
			server.close();
		}
	} 

	private static class ThreadedSocket extends Thread 
	{
		private String name;
		private Socket socket;
		ObjectInputStream input; 
		ObjectOutputStream output;

		ThreadedSocket(Socket socket)
		{
			this.socket = socket;
		}

		public void run() 
		{
			boolean runThread = true;
			try {
				input = new ObjectInputStream(socket.getInputStream());

				output = new ObjectOutputStream(socket.getOutputStream());

				
					output.writeObject("Enter Your Nickname:");
					name = (String) input.readObject();
					
					if (name == null) 
					{
						name = "anonymous";
					}
							names.add(name);
					

				output.writeObject("Your nickname is: " + name);
				OutputStreams.add(output);

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
					}
					else if(message.startsWith("/nick")) 
					{
						String[] words = message.split("\\s");
						assert( words.length > 1);
						String newName = words[1];
						newName.trim();
						//assert has no spaces
						name = newName;
					}
					else if (message.startsWith("/dm")) 
					{
						String[] words = message.split("\\s");
						assert( words.length > 2);
						String to = words[1];
						String msg = null;
						for(int i = 1; i < words.length; i++)
						{  
							msg += words[1];
						}
						assert(msg.length() > 0);
						//this.socket.name.equals(to);

						if (words.length > 1 && msg != null) 
						{
							if (!msg.isEmpty()) 
							{
								synchronized (this) 
								{
									for (int i = 0; i < numThreads; i++) 
									{
										if (threads[i] != null && threads[i] != this
												&& threads[i].name != null
												&& threads[i].name.equals(to))
										{
											threads[i].output.writeObject(name + ": " + msg);
											this.output.writeObject(name + ": " + msg);
										}
									}
								}
							}
						}
					}
					else
					{
						for (ObjectOutputStream objectOutputStream : OutputStreams) 
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
