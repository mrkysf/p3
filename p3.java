import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class CCCS
{
	private static ServerSocket server;

	private static ArrayList<ThreadedSocket> threads = new ArrayList<ThreadedSocket>();

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
				for(int i=0; i < threads.size(); i++)
				{
					if (threads.get(i)==null && !foundEmptyThread)
					{
						emptyThreadIndex = i;
						foundEmptyThread = true;
					}
				}
				if(foundEmptyThread)
					threads.set(emptyThreadIndex,new ThreadedSocket(socket)).start();
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
					name = name.trim();
					if (name == null) 
					{
						name = "anonymous";
					}					

				output.writeObject("Your nickname is: " + name);
				
				while (runThread) 
				{
					String message = (String) input.readObject();
					if (message == null) 
					{
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
						//words[1] = words[1].trim();
						String to = words[1];
						String msg = null;
						for(int i = 1; i < words.length; i++)
						{  
							msg += words[1];
						}
						assert(msg.length() > 0);

						if (words.length > 1 && msg != null) 
						{
							if (!msg.isEmpty()) 
							{
								synchronized (this) 
								{
									for (ThreadedSocket s : threads) 
										if (s != null && s != this
										&& s.name != null
										&& s.name.equals(to))
										{
											s.output.writeObject(name + ": " + msg);
											this.output.writeObject(name + ": " + msg);
										}
								}
							}
						}
					}
					else
					{
						synchronized(this)
						{
							for (ThreadedSocket s : threads) 
								if(s != null && s.name != null)
									s.output.writeObject(name + ": " + message);

						}
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
					for (ThreadedSocket s : threads) 
						if(s == this)
							s = null;
					input.close();
					output.close();
					socket.close();
					System.out.println("Chat Ended"); 
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}
